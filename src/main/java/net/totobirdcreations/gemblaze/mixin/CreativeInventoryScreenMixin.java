package net.totobirdcreations.gemblaze.mixin;

import dev.dfonline.codeclient.location.Build;
import dev.dfonline.codeclient.location.Dev;
import dev.dfonline.codeclient.location.Location;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.totobirdcreations.gemblaze.Main;
import net.totobirdcreations.gemblaze.util.item.UtilityItems;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CreativeInventoryScreen.class)
class CreativeInventoryScreenMixin {

    @Inject(method = "onMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;getStacks()Lnet/minecraft/util/collection/DefaultedList;"), cancellable = true)
    private void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        @Nullable Location loc = Main.getLocation();
        if (loc instanceof Build || loc instanceof Dev) {
            ClientPlayNetworkHandler handler = Main.INSTANCE.getCLIENT().getNetworkHandler();
            if (handler != null) {
                UtilityItems.INSTANCE.resetInventory(null, true);
                ci.cancel();
            }
        }
    }

}
