package net.totobirdcreations.gemblazeapi.detect

import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.totobirdcreations.gemblazeapi.Main
import net.totobirdcreations.gemblazeapi.api.*
import net.totobirdcreations.gemblazeapi.api.hypercube.InstructionInfo
import net.totobirdcreations.gemblazeapi.api.hypercube.Utilities
import net.totobirdcreations.gemblazeapi.util.InventoryBuilder


internal object OutboundPackets {

    fun onSend(packet : Packet<out PacketListener>) : Packet<out PacketListener>? {
        if (State.isOnDF()) {

            if (packet is CommandExecutionC2SPacket) {
                var command = packet.command;

                if (Patterns.SWITCH_MODE.matches(command)) {
                    InboundPackets.ModeSwitch.skipExit.put(true);
                }

                else if (State.getPlot()?.mode == DiamondFireMode.DEV && Patterns.DEFAULT_INVENTORY.matches(command)) {
                    Thread{-> Packets.waitForPacket(ScreenHandlerSlotUpdateS2CPacket::class.java, 100){_ -> run {
                        Utilities.isCompactInventory    = false;
                        Utilities.instructionBlocksMenu = null;
                        Thread.sleep(10);
                        val builder = InventoryBuilder(false);
                        Utilities.DEV_ITEMS.trigger(builder);
                        builder.push();
                    }}}.start();
                }

                else if (State.getPlot()?.mode == DiamondFireMode.DEV && Patterns.COMPACT_INVENTORY.matches(command)) {
                    Thread{->Packets.waitForPacket(ScreenHandlerSlotUpdateS2CPacket::class.java, 100){_ -> run {
                        Utilities.isCompactInventory = true;
                        val inventory = Main.CLIENT.player!!.inventory.main;
                        for (stack in inventory) {
                            InstructionInfo.tryPut(stack);
                            Utilities.tryPut(stack);
                        };
                    }}}.start();
                }

            }

        }
        return packet;
    }

}