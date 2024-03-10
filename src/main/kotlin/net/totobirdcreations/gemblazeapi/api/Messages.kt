package net.totobirdcreations.gemblazeapi.api

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.totobirdcreations.gemblazeapi.Main


object Messages {

    @JvmStatic fun sendCommand(command : String) : Boolean {
        while (Main.CLIENT.currentScreen != null) {
            Thread.sleep(10);
        }
        return Main.CLIENT.networkHandler?.sendCommand(command) == true;
    }

    internal fun onReceive(packet : Packet<out PacketListener>) : Packet<out PacketListener>? {
        if (packet is GameMessageS2CPacket) {
            val minimessage = Patterns.textToMiniMessage(packet.content);
            if (FabricLoader.getInstance().isDevelopmentEnvironment) {
                Main.LOGGER.warn(minimessage);
            }
            val match = Patterns.FIND_SELF_MESSAGE.matchEntire(minimessage);
            if (match != null) {
                val next = DiamondFireNode.from(match.groups["server"]!!.value);
                State.state!!.node.put(next);
                val mode = match.groups["mode"]!!.value;
                if (mode == "spawn") {
                    if (State.state!!.plot != null) {
                        State.Internal.exitPlot();
                    }
                } else {
                    State.state!!.plot!!.mode        = DiamondFireMode.from(mode)!!;
                    State.state!!.plot!!.name        .put(Patterns.miniMessageToText(match.groups["plotName"]!!.value)!!);
                    State.state!!.plot!!.id          .put(match.groups["plotId"      ]!!.value.toInt())
                    State.state!!.plot!!.owner       .put(match.groups["plotOwner"   ]!!.value)
                    State.state!!.plot!!.whitelisted .put(match.groups["whitelisted" ] != null)
                }
            }

            //return GameMessageS2CPacket(Text.empty().append(Text.literal("⇄").setStyle(Style.EMPTY.withColor(0x00ff00).withInsertion(packet.content.string.split(": ").drop(1).joinToString(": ").reversed()))).append(" ").append(packet.content), false);
        }
        return packet;
    }

}