package net.totobirdcreations.gemblaze.mixin;

import dev.dfonline.codeclient.location.Dev;
import dev.dfonline.codeclient.location.Location;
import dev.dfonline.codeclient.location.Spawn;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.totobirdcreations.gemblaze.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(InGameHud.class)
class InGameHudMixin {

    // HideScoreboard
    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        Location location = Main.getLocation();
        if (location instanceof Spawn || location instanceof Dev) {
            if (Main.getCONFIG().interfaceHideSidebar) {
                ci.cancel();
            }
        }
        if (Main.INSTANCE.getCLIENT().getDebugHud().shouldShowDebugHud()) {
            ci.cancel();
        }
    }

}
