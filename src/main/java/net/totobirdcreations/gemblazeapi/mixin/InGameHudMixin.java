package net.totobirdcreations.gemblazeapi.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.totobirdcreations.gemblazeapi.api.State;
import net.totobirdcreations.gemblazeapi.mod.Mod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(InGameHud.class)
class InGameHudMixin {

    // Hide DF sidebar.
    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        if (Mod.INSTANCE.getCONFIG().interfaceHideSidebar && State.isOnDF() && ! State.isInPlay()) {
            ci.cancel();
        }
    }

}
