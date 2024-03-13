package net.totobirdcreations.gemblaze.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.LightmapTextureManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(LightmapTextureManager.class)
class LightmapTextureManagerMixin {

    // NightVision
    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1))
    private float update(Double instance, Operation<Float> original) {
        @Nullable Boolean nightVision = net.totobirdcreations.gemblaze.mixinternal.LightmapTextureManagerMixin.INSTANCE.getNightVision();
        if (nightVision != null && nightVision) { return 255.0f; }
        return original.call(instance);
    }

}
