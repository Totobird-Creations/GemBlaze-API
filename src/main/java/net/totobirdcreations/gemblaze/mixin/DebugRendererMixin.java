package net.totobirdcreations.gemblaze.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.totobirdcreations.gemblaze.render.SearchRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(DebugRenderer.class)
class DebugRendererMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        SearchRenderer.INSTANCE.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }

}
