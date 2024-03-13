package net.totobirdcreations.gemblaze.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.totobirdcreations.gemblaze.render.DocsRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GenericContainerScreen.class)
class GenericContainerScreenMixin {

    @Inject(method = "render" , at = @At("RETURN"))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        DocsRenderer.INSTANCE.render(context);
    }

}
