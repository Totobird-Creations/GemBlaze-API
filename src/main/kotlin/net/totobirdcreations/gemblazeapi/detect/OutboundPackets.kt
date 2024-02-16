package net.totobirdcreations.gemblazeapi.detect

import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.totobirdcreations.gemblazeapi.Main
import net.totobirdcreations.gemblazeapi.api.*
import net.totobirdcreations.gemblazeapi.api.hypercube.CodespaceStyle
import net.totobirdcreations.gemblazeapi.api.hypercube.InstructionInfo
import net.totobirdcreations.gemblazeapi.api.hypercube.Inventory
import net.totobirdcreations.gemblazeapi.api.hypercube.VariableScope
import net.totobirdcreations.gemblazeapi.mod.Mod
import net.totobirdcreations.gemblazeapi.util.InventoryBuilder


internal object OutboundPackets {

    fun onSend(packet : Packet<out PacketListener>) : Packet<out PacketListener>? {
        if (State.isOnDF()) {

            if (packet is CommandExecutionC2SPacket) {
                var command = packet.command;
                val args    = command.lowercase().split(" ");
                val isDev   = State.getPlot()?.mode == DiamondFireMode.DEV;


                if (Patterns.SWITCH_MODE.matches(command)) {
                    InboundPackets.ModeSwitch.skipExit.put(true);
                }

                else if (isDev && Patterns.DEFAULT_INVENTORY.matches(command)) {
                    Thread{-> Packets.waitForPacket(ScreenHandlerSlotUpdateS2CPacket::class.java, 100){_ -> run {
                        Inventory.isCompactInventory    = false;
                        Inventory.instructionBlocksMenu = null;
                        Thread.sleep(10);
                        val builder = InventoryBuilder(false);
                        Inventory.DEV_ITEMS.trigger(builder);
                        builder.push();
                        false
                    }}}.start();
                }

                else if (isDev && Patterns.COMPACT_INVENTORY.matches(command)) {
                    Thread{->Packets.waitForPacket(ScreenHandlerSlotUpdateS2CPacket::class.java, 100){_ -> run {
                        Inventory.isCompactInventory = true;
                        val inventory = Main.CLIENT.player!!.inventory.main;
                        for (stack in inventory) {
                            InstructionInfo.tryPut(stack);
                            Inventory.tryPut(stack);
                        };
                        false
                    }}}.start();
                }


                else if (isDev && command.startsWith(Patterns.CREATE_VARIABLE)) {
                    var finalScope : VariableScope? = null;
                    for (scope in VariableScope.entries) {
                        if (scope.flag in args) {
                            finalScope = scope;
                            break;
                        }
                    }
                    if (finalScope?.isDefault == true) {
                        command = command.replace(" ${finalScope.flag}", "");
                    }
                    val scope = Mod.CONFIG.autoCommandDefaultsVarCreateScope;
                    if (finalScope == null && ! scope.isDefault) {
                        command += " ${scope.flag}";
                    }
                }


                else if (isDev && Patterns.ADD_CODESPACE_LAYER.matchesAt(command, 0)) {
                    if (Mod.CONFIG.autoCommandDefaultsCodespaceAddCompact) {
                        if (args.contains("-c")) {
                            command = command.replace(" -c", "");
                        } else {
                            command += " -c";
                        }
                    }
                    var finalStyle : CodespaceStyle? = null;
                    for (style in CodespaceStyle.entries) {
                        if (style.flag in args) {
                            finalStyle = style;
                            break;
                        }
                    }
                    if (finalStyle?.isDefault == true) {
                        command = command.replace(" ${finalStyle.flag}", "");
                    }
                    val style = Mod.CONFIG.autoCommandDefaultsCodespaceAddStyle;
                    if (finalStyle == null && ! style.isDefault) {
                        command += " ${style.flag}";
                    }
                }


                if (command != packet.command) {
                    return CommandExecutionC2SPacket(command, packet.timestamp, packet.salt, packet.argumentSignatures, packet.acknowledgment);
                }

            }

        }
        return packet;
    }

}