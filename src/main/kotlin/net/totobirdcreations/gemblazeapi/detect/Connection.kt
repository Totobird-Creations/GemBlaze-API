package net.totobirdcreations.gemblazeapi.detect

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.totobirdcreations.gemblazeapi.api.State
import net.totobirdcreations.gemblazeapi.api.Patterns


internal object Connection :
    ClientPlayConnectionEvents.Join,
    ClientPlayConnectionEvents.Disconnect
{

    fun init() {
        ClientPlayConnectionEvents.JOIN.register(this);
        ClientPlayConnectionEvents.DISCONNECT.register(this);
    }

    override fun onPlayReady(handler : ClientPlayNetworkHandler, sender : PacketSender, client : MinecraftClient) {
        if (Patterns.SERVER_ADDRESS.matchEntire(handler.connection.address.toString()) != null) {
            if (! State.isOnDF()) {
                State.Internal.enterDF();
            }
        } else if (State.isOnDF()) {
            State.Internal.exitDF();
        }
    }

    override fun onPlayDisconnect(handler : ClientPlayNetworkHandler, client : MinecraftClient) {
        if (State.isOnDF()) {
            State.Internal.exitDF();
        }
    }

}