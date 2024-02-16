package net.totobirdcreations.gemblazeapi.mixin.codeclient;

import dev.dfonline.codeclient.dev.menu.customchest.CustomChestMenu;
import net.minecraft.client.gui.DrawContext;
import net.totobirdcreations.gemblazeapi.mod.render.ContainerScreenRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CustomChestMenu.class)
class CustomChestMenuMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ContainerScreenRenderer.INSTANCE.render(context);
    }

}
