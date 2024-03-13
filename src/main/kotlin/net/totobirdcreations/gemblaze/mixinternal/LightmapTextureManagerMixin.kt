package net.totobirdcreations.gemblaze.mixinternal

import dev.dfonline.codeclient.location.Build
import dev.dfonline.codeclient.location.Dev
import net.minecraft.entity.effect.StatusEffects
import net.totobirdcreations.gemblaze.Main


internal object LightmapTextureManagerMixin {

    var overrideNightVision : Boolean? = null;

    fun getNightVision() : Boolean? {
        val location = Main.location;
        if (location is Build || location is Dev) {
            return Main.CLIENT.player?.hasStatusEffect(StatusEffects.NIGHT_VISION) == true || (this.overrideNightVision ?: Main.CONFIG.developmentNightVision);
        }
        return null;
    }

}