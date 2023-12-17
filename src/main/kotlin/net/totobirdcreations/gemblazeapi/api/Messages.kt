package net.totobirdcreations.gemblazeapi.api

import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
import net.totobirdcreations.gemblazeapi.Main


object Messages {

    @JvmStatic fun sendCommand(command : String) : Boolean {
        return Main.CLIENT.networkHandler?.sendCommand(command) == true;
    }

    internal fun onReceive(packet : Packet<out PacketListener>) : Packet<out PacketListener>? {
        if (packet is GameMessageS2CPacket) {
            Main.LOGGER.warn(Patterns.textToMiniMessage(packet.content));
        }
        return packet;
    }

}