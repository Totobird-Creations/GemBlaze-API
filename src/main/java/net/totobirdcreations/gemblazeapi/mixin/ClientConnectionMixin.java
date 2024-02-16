package net.totobirdcreations.gemblazeapi.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.totobirdcreations.gemblazeapi.detect.InboundPackets;
import net.totobirdcreations.gemblazeapi.detect.OutboundPackets;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Environment(EnvType.CLIENT)
@Mixin(ClientConnection.class)
class ClientConnectionMixin {

    @Unique private static boolean cancelReceivePacket = false;
    @ModifyVariable(method = "handlePacket", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static Packet<?> receivePacket(Packet<?> packet) {
        @Nullable Packet<?> newPacket = InboundPackets.INSTANCE.onReceive(packet);
        if (newPacket == null) {cancelReceivePacket = true;}
        return newPacket;
    }
    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void receivePacketCancel(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        if (cancelReceivePacket) {
            cancelReceivePacket = false;
            ci.cancel();
        }
    }

    @Unique private boolean cancelSendPacket = false;
    @ModifyVariable(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;Z)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Packet<?> sendPacket(Packet<?> packet) {
        @Nullable Packet<?> newPacket = OutboundPackets.INSTANCE.onSend(packet);
        if (newPacket == null) {this.cancelSendPacket = true;}
        return newPacket;
    }
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;Z)V", at = @At("HEAD"), cancellable = true)
    private void sendPacketCancel(Packet<?> packet, PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        if (this.cancelSendPacket) {
            this.cancelSendPacket = false;
            ci.cancel();
        }
    }

}
