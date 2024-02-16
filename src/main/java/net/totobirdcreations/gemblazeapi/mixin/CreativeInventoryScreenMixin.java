package net.totobirdcreations.gemblazeapi.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.totobirdcreations.gemblazeapi.api.State;
import net.totobirdcreations.gemblazeapi.mod.Mod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CreativeInventoryScreen.class)
class CreativeInventoryScreenMixin {

    // Shift-click destroy item creative inventory slot (clear inventory).
    // Resets to compact or standard instead.
    @Inject(method = "onMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;getStacks()Lnet/minecraft/util/collection/DefaultedList;"), cancellable = true)
    private void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (State.isOnDF() && State.isInDev()) {
            ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
            if (handler != null) {
                if (Mod.INSTANCE.getCONFIG().autoCommandCompactInventory) {
                    handler.sendCommand("resetcompact");
                } else {
                    handler.sendCommand("reset");
                }
                ci.cancel();
            }
        }
    }

}
