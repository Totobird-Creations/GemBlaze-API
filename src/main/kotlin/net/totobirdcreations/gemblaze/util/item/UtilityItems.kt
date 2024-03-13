package net.totobirdcreations.gemblaze.util.item

import dev.dfonline.codeclient.location.Build
import dev.dfonline.codeclient.location.Dev
import dev.dfonline.codeclient.location.Location
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket
import net.totobirdcreations.gemblaze.Main
import net.totobirdcreations.gemblaze.util.value.ExpirableValue
import net.totobirdcreations.gemblaze.util.value.RequestableValue


internal object UtilityItems {


    const val GLITCH_STICK_NAME : String = "<!italic><red>Glitch Stick";
    var waitingForGlitchStick : ExpirableValue<Boolean> = ExpirableValue(1000);
    val glitchStick : RequestableValue<ItemStack> = RequestableValue { ->
        this.waitingForGlitchStick.put(true);
        Main.CLIENT.networkHandler?.sendCommand("plot glitch");
        null
    };

    const val REFERENCE_BOOK_NAME : String = "<!italic><gold>◆ </gold><aqua>Reference Book </aqua><gold>◆";
    var referenceBook : ItemStack? = null;

    var waitingForReset : ExpirableValue<Boolean> = ExpirableValue(1000);


    // CompactInventory
    fun resetInventory(location_ : Location?, forceReset : Boolean = false) {
        val location = location_ ?: Main.location;
        if (location is Build) {
            for (slot in 0..<41) {
                this.putInventory(slot, ItemStack.EMPTY);
            }
            if (Main.CONFIG.developmentAutoWorldeditWandBuild) {
                this.putInventory(6, Items.WOODEN_AXE.defaultStack);
            }
        } else if (location is Dev) {
            if (Main.CONFIG.developmentCompactInventory) {
                Main.CLIENT.networkHandler?.sendCommand("resetcompact");
            } else if (forceReset) {
                Main.CLIENT.networkHandler?.sendCommand("reset");
            }
            this.waitingForReset.put(true);
        }
    }

    fun invalidateDev() {
        this.glitchStick.putNull();
        this.referenceBook = null;
        InstructionBlocks.instructionBlocks.clear();
    }

    fun getInventory(slot : Int) : ItemStack {
        return Main.CLIENT.player?.inventory?.getStack(slot) ?: ItemStack.EMPTY;
    }
    fun putInventory(slot : Int, stack : ItemStack, updateClient : Boolean = true, updateServer : Boolean = true) {
        if (updateServer) {
            val trueSlot = if (slot in 0..<9) { slot + 36 } else { slot };
            Main.CLIENT.networkHandler?.sendPacket(CreativeInventoryActionC2SPacket(trueSlot, stack));
        }
        if (updateClient) {
            Main.CLIENT.player?.inventory?.setStack(slot, stack);
        }
    }

}