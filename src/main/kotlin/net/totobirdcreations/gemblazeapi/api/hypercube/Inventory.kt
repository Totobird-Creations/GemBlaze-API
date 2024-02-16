package net.totobirdcreations.gemblazeapi.api.hypercube

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.totobirdcreations.gemblazeapi.Main
import net.totobirdcreations.gemblazeapi.api.DiamondFireMode
import net.totobirdcreations.gemblazeapi.api.Messages
import net.totobirdcreations.gemblazeapi.api.Packets
import net.totobirdcreations.gemblazeapi.api.State
import net.totobirdcreations.gemblazeapi.util.*


object Inventory {

    /**
     * Triggered when the player's dev item list is being created (Instruction blocks, reference book, bracket finder, etc).
     *
     * Can be used to add items to or modify items in the item list.
     */
    @JvmStatic val DEV_ITEMS   : Event<(InventoryBuilder) -> Unit> = Event{ callbacks -> { builder -> callbacks.forEach{ callback -> try { callback(builder) } catch (_ : Exception) {}}}};
    /**
     * Triggered when the player's value item list is being created (Strings, numbers, locations, etc).
     *
     * Can be used to add items to or modify items in the item list.
     */
    @JvmStatic val VALUE_ITEMS : Event<(InventoryBuilder) -> Unit> = Event{ callbacks -> { builder -> callbacks.forEach{ callback -> try { callback(builder) } catch (_ : Exception) {}}}};

    /**
     * Whether the player currently has compact inventory enabled.
     *
     * Is `null` if not in dev/code mode.
     */
    @JvmStatic var isCompactInventory : Boolean? = null
        internal set;

    /**
     * The reference book item.
     *
     * Is `null` if not in dev/code mode.
     */
    @JvmStatic var referenceBook : ItemStack? = null
        private set;
    /**
     * The bracket finder item.
     *
     * Is `null` if not in dev/code mode.
     */
    @JvmStatic var bracketFinder : ItemStack? = null
        private set;
    /**
     * The not arrow item.
     *
     * Is `null` if not in dev/code mode.
     */
    @JvmStatic var notArrow      : ItemStack? = null
        private set;

    /**
     * The glitch stick item.
     *
     * *May be unknown.*
     */
    @JvmStatic val glitchStick : RequestableValue<ItemStack> = RequestableValue{-> run {
        if (State.getPlot()?.mode == DiamondFireMode.DEV) {
            if (Main.CLIENT.networkHandler?.sendCommand("plot glitch") == true) {
                val packet = Packets.waitForPacket(ScreenHandlerSlotUpdateS2CPacket::class.java, 100);
                if (packet != null && packet.stack.isOf(Items.STICK)) {
                    Thread.sleep(100);
                    InventoryBuilder.putInventoryItem(packet.slot, Items.AIR.defaultStack);
                    return@run packet.stack.copyWithCount(1);
                }
            }
            throw RequestFailedToBeFulfilledException();
        } else {
            throw RequestImpossibleToFulfillException();
        }
    }};
    /**
     * The cancel scythe item.
     *
     * *May be unknown.*
     */
    @JvmStatic val cancelWand  : RequestableValue<ItemStack> = RequestableValue{-> run {
        if (State.getPlot()?.mode == DiamondFireMode.DEV) {
            if (Main.CLIENT.networkHandler?.sendCommand("cancel") == true) {
                val packet = Packets.waitForPacket(ScreenHandlerSlotUpdateS2CPacket::class.java, 100);
                if (packet != null && packet.stack.isOf(Items.DIAMOND_SHOVEL)) {
                    Thread.sleep(100);
                    InventoryBuilder.putInventoryItem(packet.slot, Items.AIR.defaultStack);
                    return@run packet.stack.copyWithCount(1);
                }
            }
            throw RequestFailedToBeFulfilledException();
        } else {
            throw RequestImpossibleToFulfillException();
        }
    }};

    /**
     * The value menu item.
     *
     * Is `null` if not in dev/code mode.
     */
    @JvmStatic var valuesMenu : ItemStack? = null
        private set
        get() = field?.copy();
    /**
     * The instruction block menu item.
     *
     * Is `null` if not in dev/code mode, or if compact inventory is not enabled.
     */
    @JvmStatic var instructionBlocksMenu : ItemStack? = null
        internal set
        get() = field?.copy();


    /**
     * Checks if two `ItemStack`s have the same Hypercube item ID.
     */
    @JvmStatic fun hypercubeEquals(a : ItemStack?, b : ItemStack?) : Boolean {
        val aId = this.hypercubeId(a);
        val bId = this.hypercubeId(b);
        if (aId == null || bId == null) {
            return false;
        }
        return aId == bId;
    }

    /**
     * Returns the Hypercube item ID of an `ItemStack`, or `null`.
     */
    @JvmStatic fun hypercubeId(stack : ItemStack?) : String? {
        return stack?.getSubNbt("PublicBukkitValues")?.getString("hypercube:item_instance");
    }

    internal fun clear() {
        this.isCompactInventory = null;
        this.referenceBook = null;
        this.bracketFinder = null;
        this.notArrow      = null;
        this.glitchStick.putNull();
        this.cancelWand.putNull();
        this.valuesMenu            = null;
        this.instructionBlocksMenu = null;
    }

    internal fun tryPut(stack : ItemStack) {
        if (stack.isOf(Items.WRITTEN_BOOK)) {
            this.referenceBook = stack.copy();
        }
        else if (stack.isOf(Items.BLAZE_ROD)) {
            this.bracketFinder = stack.copy();
        }
        else if (stack.isOf(Items.SPECTRAL_ARROW)) {
            this.notArrow = stack.copy();
        }
        else if (stack.isOf(Items.IRON_INGOT)) {
            this.valuesMenu = stack.copy();
        }
        else if (stack.isOf(Items.DIAMOND)) {
            this.instructionBlocksMenu = stack.copy();
        }
    }

}