package net.totobirdcreations.gemblazeapi.detect

import net.minecraft.item.Items
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.*
import net.totobirdcreations.gemblazeapi.Main
import net.totobirdcreations.gemblazeapi.api.*
import net.totobirdcreations.gemblazeapi.api.hypercube.Inventory
import net.totobirdcreations.gemblazeapi.mod.Mod
import net.totobirdcreations.gemblazeapi.mod.config.Config
import net.totobirdcreations.gemblazeapi.mod.render.ContainerScreenRenderer
import net.totobirdcreations.gemblazeapi.mod.render.HUDRenderer
import net.totobirdcreations.gemblazeapi.util.ExpirableValue
import net.totobirdcreations.gemblazeapi.util.InventoryBuilder


internal object InboundPackets {

    fun onReceive(packet : Packet<out PacketListener>) : Packet<out PacketListener>? {
        var finalPacket : Packet<out PacketListener>? = packet;

        if (State.isOnDF()) {

            if (Packets.onReceive(packet)) {
                return null;
            }

            if (packet is GameJoinS2CPacket) {
                State.Internal.enterNode(null);
            }

            else if (packet is ScreenHandlerSlotUpdateS2CPacket) {
                if (packet.stack.isOf(Items.WRITTEN_BOOK) &&
                    packet.stack.getSubNbt("PublicBukkitValues")?.getString("hypercube:item_instance")
                        == Inventory.referenceBook?.getSubNbt("PublicBukkitValues")?.getString("hypercube:item_instance")
                ) {
                    ContainerScreenRenderer.referenceBook = packet.stack;
                }
            }

            else if (packet is OpenScreenS2CPacket) {
                ContainerScreenRenderer.openScreen();

                if (State.getPlot()?.mode == DiamondFireMode.DEV) {
                    val minimessage = Patterns.textToMiniMessage(packet.name);

                    if (minimessage == Patterns.DEV_MENU) {
                        Thread{-> Packets.waitForPacket(ScreenHandlerSlotUpdateS2CPacket::class.java, 100){ _ -> run {
                            Thread.sleep(100);
                            val builder = InventoryBuilder(true);
                            Inventory.DEV_ITEMS.trigger(builder);
                            builder.push();
                            false
                        }}}.start();
                    }

                    else if (minimessage == Patterns.VALUES_MENU) {
                        Thread{-> Packets.waitForPacket(ScreenHandlerSlotUpdateS2CPacket::class.java, 100){ _ -> run {
                            Thread.sleep(100);
                            val builder = InventoryBuilder(true, 18u, listOf(
                                13u, 14u, 15u, 16u, 17u
                            ));
                            Inventory.VALUE_ITEMS.trigger(builder);
                            builder.push();
                            false
                        }}}.start();
                    }
                }

            }

            else if (packet is OverlayMessageS2CPacket) {
                val match = Patterns.CPU_OVERLAY.matchEntire(Patterns.textToMiniMessage(packet.message));
                if (match != null) {
                    HUDRenderer.updateLagslayer(match.groups["percent"]!!.value.toFloat());
                    if (Mod.CONFIG.interfaceCpuOverlayPosition != Config.CpuOverlayPosition.ACTIONBAR) {return null;}
                }
            }

            ModeSwitch.onReceive(packet);

            finalPacket = Messages.onReceive(packet);
            if (finalPacket != packet) {
                return finalPacket;
            }

        }
        return finalPacket;
    }


    enum class ModeSwitch {
        IDLE,
        CLEARED_TITLE,
        POSITION_SET;

        companion object {

            private  val value    : ExpirableValue<ModeSwitch> = ExpirableValue(250);
            internal val skipExit : ExpirableValue<Boolean>    = ExpirableValue(500);

            internal var origin : Pair<Double, Double>? = null;

            fun reset() {this.value.invalidate();}

            fun onReceive(packet : Packet<out PacketListener>) : Boolean {
                when (this.value.getOrDefault(IDLE)) {

                    IDLE -> {if (packet is ClearTitleS2CPacket && packet.shouldReset()) {
                        this.value.put(CLEARED_TITLE);
                    }};

                    CLEARED_TITLE -> {if (packet is PlayerPositionLookS2CPacket) {
                        this.origin = Pair(packet.x, packet.z);
                        this.value.put(POSITION_SET);
                    }};

                    POSITION_SET -> {

                        if (packet is PlayerPositionLookS2CPacket) {
                            this.origin = Pair(packet.x, packet.z);
                        }

                        else if (packet is OverlayMessageS2CPacket) {
                            if (Patterns.SPAWN_OVERLAY.matches(Patterns.textToMiniMessage(packet.message))) {
                                if (State.getPlot() != null) {
                                    State.Internal.exitPlot();
                                }
                                this.value.invalidate();
                            }
                        }

                        else if (packet is GameMessageS2CPacket && ! packet.overlay) {
                            val minimessage = Patterns.textToMiniMessage(packet.content);
                            if (Patterns.BUILD_MESSAGE == minimessage) {
                                if (this.skipExit.getOrDefault(false)) {
                                    State.Internal.switchPlotMode(DiamondFireMode.BUILD);
                                } else {
                                    State.Internal.enterPlot(DiamondFirePlot(DiamondFireMode.BUILD));
                                }
                                this.value.invalidate();
                            }
                            else if (Patterns.DEV_MESSAGE == minimessage) {
                                if (this.skipExit.getOrDefault(false)) {
                                    State.Internal.switchPlotMode(DiamondFireMode.DEV);
                                } else {
                                    State.Internal.enterPlot(DiamondFirePlot(DiamondFireMode.DEV));
                                }
                                this.value.invalidate();
                            }
                            else {
                                val match = Patterns.PLAY_MESSAGE.matchEntire(minimessage);
                                if (match != null) {
                                    this.value.invalidate();
                                    val skipExit = this.skipExit.getOrDefault(false);
                                    this.skipExit.invalidate();
                                    val plot = if (skipExit) {
                                        State.getPlot()!!
                                    } else {
                                        DiamondFirePlot(DiamondFireMode.PLAY)
                                    };
                                    plot.name.put(Patterns.miniMessageToText(match.groups["plotName"]!!.value)!!);
                                    plot.owner.put(match.groups["plotOwner"]!!.value);
                                    if (skipExit) {
                                        State.Internal.switchPlotMode(DiamondFireMode.PLAY);
                                    } else {
                                        State.Internal.enterPlot(plot);
                                    }
                                }
                            }
                        }

                    };

                }
                return false;
            }

        }

    }

}