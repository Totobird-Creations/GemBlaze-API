package net.totobirdcreations.gemblazeapi.api

import kotlinx.datetime.Clock
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.totobirdcreations.gemblazeapi.Main


object Packets {
    private val pending : HashMap<Class<out Packet<out PacketListener>>, ArrayList<(Packet<out PacketListener>) -> Unit>> = hashMapOf();

    private fun <T : Packet<out PacketListener>> waitForPacket(clazz : Class<T>, callback : (T) -> Unit) {
        if (! this.pending.containsKey(clazz)) {
            this.pending[clazz] = arrayListOf();
        }
        this.pending[clazz]!!.add(callback as ((Packet<out PacketListener>) -> Unit));
    }

    /**
     * Waits until an incoming packet of type `clazz` is detected, then calls `callback`.
     * If the packet is not received before timeout, `callback` is not called.
     */
    @JvmStatic fun <T : Packet<out PacketListener>> waitForPacket(clazz : Class<T>, timeoutMs : Long, callback : (T) -> Unit) {
        val packet = this.waitForPacket(clazz, timeoutMs);
        if (packet != null) {
            callback(packet);
        }
    }

    /**
     * Waits until an incoming packet of type `clazz` is detected, then returns it.
     * If the packet is not received before timeout, `null` is returned.
     */
    @JvmStatic fun <T : Packet<out PacketListener>> waitForPacket(clazz : Class<T>, timeoutMs : Long) : T? {
        var packet : T? = null;
        this.waitForPacket(clazz){response -> run {
            packet = response;
        }}
        val start = Clock.System.now();
        while (packet == null) {
            if ((Clock.System.now() - start).inWholeMilliseconds >= timeoutMs) {
                return null;
            }
        }
        return packet;
    }


    internal fun <T : Packet<out PacketListener>> onReceive(packet : T) {
        val pending = this.pending[packet::class.java];
        this.pending.remove(packet::class.java);
        pending?.forEach{callback -> callback(packet)};
    }

}