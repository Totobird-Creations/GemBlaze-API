package net.totobirdcreations.gemblazeapi.mixin;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.totobirdcreations.gemblazeapi.Main;
import net.totobirdcreations.gemblazeapi.api.DiamondFireMode;
import net.totobirdcreations.gemblazeapi.api.DiamondFirePlot;
import net.totobirdcreations.gemblazeapi.api.State;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SignBlockEntityRenderer.class)
class SignBlockEntityRendererMixin {

    @Inject(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("HEAD"), cancellable = true)
    private void render(SignBlockEntity entity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        if (entity.hasWorld()) {
            BlockPos pos = entity.getPos();
            boolean hide = false;
            DiamondFirePlot plot = State.getPlot();
            ClientPlayerEntity player = Main.INSTANCE.getCLIENT().player;
            if (plot != null) {if (Boolean.TRUE.equals(plot.getArea().isInDevArea(pos))) {
                if (plot.getMode() != DiamondFireMode.DEV) {
                    hide = true;
                } else if (player != null) {
                    double eyePos = Math.floor(player.getEyePos().y / 5.0) * 5.0;
                    double y      = pos.getY() + 1.0;
                    if (y <= eyePos - 5.0 || y >= eyePos + 10.0) {
                        ci.cancel();
                    }
                }
            }}
            if (hide || (player != null && player.getEyePos().squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) >= 1024.0)) {ci.cancel();}
        }
    }

}
