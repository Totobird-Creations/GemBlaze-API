package net.totobirdcreations.gemblaze.mixin.codeclient;

import dev.dfonline.codeclient.Event;
import dev.dfonline.codeclient.location.Location;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Event.class)
class CCEventMixin {

    @Inject(method = "updateLocation", at = @At("TAIL"), remap = false)
    private static void updateLocation(Location location, CallbackInfo ci) {
        net.totobirdcreations.gemblaze.mixinternal.codeclient.CCEventMixin.INSTANCE.updateLocation(location);
    }

}
