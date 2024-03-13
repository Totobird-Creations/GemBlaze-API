package net.totobirdcreations.gemblaze.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.totobirdcreations.gemblaze.Main;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientConnection.class)
class ClientConnectionMixin {

    @Unique private static boolean cancelReceivePacket = false;
    @ModifyVariable(method = "handlePacket", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static Packet<?> receivePacket(Packet<?> packet) {
        if (Main.getLocation() == null) { return packet; }
        @Nullable Packet<?> newPacket = net.totobirdcreations.gemblaze.mixinternal.ClientConnectionMixin.INSTANCE.onReceive(packet);
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

    @Unique
    private boolean cancelSendPacket = false;
    @ModifyVariable(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;Z)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Packet<?> sendPacket(Packet<?> packet) {
        if (Main.getLocation() == null) { return packet; }
        @Nullable Packet<?> newPacket = net.totobirdcreations.gemblaze.mixinternal.ClientConnectionMixin.INSTANCE.onSend(packet);
        this.cancelSendPacket = newPacket == null;
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
