package net.totobirdcreations.gemblazeapi.util

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.screen.slot.SlotActionType
import net.totobirdcreations.gemblazeapi.Main
import net.totobirdcreations.gemblazeapi.api.Packets


class InventoryBuilder internal constructor(
    val isScreen      : Boolean,
    val slotCount     : UInt             = if (isScreen) {27u} else {36u},
    val possibleSlots : Collection<UInt> = if (isScreen) {listOf(7u, 16u, 25u, 6u, 15u, 24u)} else {listOf(16u, 25u, 34u, 15u, 24u, 33u, 14u, 23u, 32u, 13u, 22u)}
) {

    private val putSlots : HashMap<UInt, ItemStack?> = run {
        val map : HashMap<UInt, ItemStack?> = hashMapOf();
        for (i in 0u..this.slotCount) {map[i] = null;}
        map
    };
    private val replaceSlots : ArrayList<(ItemStack) -> ItemStack?> = arrayListOf();

    /**
     * Inserts the stack at the specified slot in the inventory.
     * If the slot is already taken, this operation will be skipped.
     *
     * *Should run as fast as possible.*
     */
    @Deprecated("Use `InventoryBuilder.putWherePossible` for better compatibility with other mods.")
    @Throws(IndexOutOfBoundsException::class)
    fun put(slot : UInt, stack : ItemStack) {
        if (slot < 0u || slot >= this.slotCount) {
            throw IndexOutOfBoundsException();
        }
        if (this.putSlots[slot] == null && ! this.putSlots.containsValue(stack)) {
            this.putSlots[slot] = stack;
        }
    }

    /**
     * Inserts the stack in an open spot in the inventory.
     * If there is no more space, this operation will be skipped.
     *
     * *Should run as fast as possible.*
     *
     * ### Example:
     * Adding redstone dust to instruction block items.
     * ```
     * // Kotlin
     * Events.DEV_ITEMS.register{builder ->
     *     builder.putWherePossible(Items.REDSTONE.defaultStack);
     * };
     * // Java
     * Events.DEV_ITEMS.register((builder) -> {
     *     builder.putWherePossible(Items.REDSTONE.defaultStack);
     * });
     * ```
     */
    @Throws(NoSuchElementException::class)
    fun putWherePossible(stack : ItemStack) {
        val slot = this.possibleSlots.first{i -> this.putSlots[i] == null};
        @Suppress("DEPRECATION")
        this.put(slot, stack);
    }

    /**
     * Modifies stacks in the inventory. Returning `null`
     * leaves the `ItemStack` as is without modification.
     *
     * *Should run as fast as possible.*
     *
     * **This is an expensive operation and should be used
     * sparingly.**
     *
     * ### Example:
     * Sets the stack size of any magma cream item to 2.
     * ```
     * // Kotlin
     * Events.VALUE_ITEMS.register{builder ->
     *     builder.modify{stack ->
     *         if (stack.isOf(Items.MAGMA_CREAM)) {
     *             stack.copyWithCount(2)
     *         } else {null}
     *     };
     * };
     * // Java
     * Events.VALUE_ITEMS.register((builder) -> {
     *     builder.modify((stack) -> stack.isOf(Items.MAGMA_CREAM) ? stack.copyWithCount(2) : null);
     * });
     * ```
     */
    fun modify(handler: (ItemStack) -> ItemStack?) {
        this.replaceSlots.add(handler);
    }


    internal fun push() {
        // Modify Slots
        val getStack : ((Int) -> ItemStack?) = if (this.isScreen) {
            {slot -> run {
            val screen = Main.CLIENT.currentScreen;
            if (screen is HandledScreen<*>) {
                screen.screenHandler.getSlot(slot).stack
            } else {null}
            }}
        } else {
            {slot -> Main.CLIENT.player?.inventory?.getStack(slot)}
        };
        for (slot in 0u..this.slotCount) {
            var stack    = this.putSlots[slot] ?: getStack(slot.toInt()) ?: Items.AIR.defaultStack!!;
            var modified = false;
            if (! stack.isEmpty) {
                for (op in this.replaceSlots) {
                    if (! modified) {
                        stack = stack.copy();
                    }
                    try {
                        val result = op(stack);
                        if (result != null) {
                            stack    = result;
                            modified = true;
                        }
                    } catch (_ : Exception) {}
                }
            }
            if (modified) {
                this.putSlots[slot] = stack;
            }
        }
        // Put Slots
        val putStack : ((Int, ItemStack) -> Unit) = if (this.isScreen) {
            {slot, stack -> putScreenItem(slot, stack)}
        } else {
            {slot, stack -> putInventoryItem(slot, stack)}
        };
        val prevStack = Main.CLIENT.player?.inventory?.getStack(17);
        if (prevStack != null) {
            for ((slot, stack) in this.putSlots.entries) {
                if (stack != null) {
                    putStack(slot.toInt(), stack);
                }
            }
            putInventoryItem(17, prevStack);
        }
    }


    companion object {

        internal fun putScreenItem(slot : Int, stack : ItemStack) {
            val screen = Main.CLIENT.currentScreen;
            if (screen is HandledScreen<*>) {

                val clickSource = {-> Main.CLIENT.interactionManager?.clickSlot(
                    screen.screenHandler.syncId,
                    screen.screenHandler.slots.size - 28, 0, SlotActionType.PICKUP,
                    Main.CLIENT.player
                )};
                val clickTarget = {-> Main.CLIENT.interactionManager?.clickSlot(
                    screen.screenHandler.syncId,
                    slot, 0, SlotActionType.PICKUP,
                    Main.CLIENT.player
                )};

                this.putInventoryItem(17, Items.AIR.defaultStack);
                if (Main.CLIENT.currentScreen != screen) {return;}
                clickTarget();
                if (Main.CLIENT.currentScreen != screen) {return;}
                clickSource();
                if (Main.CLIENT.currentScreen != screen) {return;}
                this.putInventoryItem(17, stack);
                if (Main.CLIENT.currentScreen != screen) {return;}
                clickSource();
                if (Main.CLIENT.currentScreen != screen) {return;}
                clickTarget();
            }
        }

        internal fun putInventoryItem(slot : Int, stack : ItemStack) {
            Main.CLIENT.networkHandler?.sendPacket(CreativeInventoryActionC2SPacket(slot, stack));
            Main.CLIENT.player?.inventory?.setStack(slot, stack);
        }

        internal fun temporaryInventoryItem(slot : Int, stack : ItemStack, op : () -> Unit = {->}) : ItemStack? {
            val prevStack = Main.CLIENT.player?.inventory?.getStack(slot);
            if (prevStack != null) {
                this.putInventoryItem(slot, stack);
                op();
                var count                   = 0u;
                var finalStack : ItemStack? = null;
                while (true) {
                    val packet = Packets.waitForPacket(ScreenHandlerSlotUpdateS2CPacket::class.java, 75) ?: break;
                    count += 1u;
                    if (count >= 2u) {
                        finalStack = packet.stack;
                        break;
                    }
                }
                this.putInventoryItem(slot, prevStack);
                return finalStack;
            }
            return null;
        }

    }

}