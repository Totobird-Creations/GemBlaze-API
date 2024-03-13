package net.totobirdcreations.gemblaze.render

import dev.dfonline.codeclient.location.Dev
import dev.dfonline.codeclient.location.Plot
import net.minecraft.util.math.BlockPos
import net.totobirdcreations.gemblaze.Main
import net.totobirdcreations.gemblaze.Main.CLIENT
import net.totobirdcreations.gemblaze.Main.CONFIG
import kotlin.math.pow


internal object WorldRenderer {

    @JvmStatic
    fun shouldShowCodeBlock(pos : BlockPos) : Boolean {
        try {
            var hide = false;
            val loc = Main.location;
            if (loc is Plot) {
                val player = CLIENT.player;
                if (loc.isInDev(pos)) {
                    if (loc !is Dev) { hide = true; }
                    else if (player != null) {
                        val eyePos = player.eyePos.y;
                        val y      = pos.y.toDouble();
                        if (   eyePos < y - (5.0 * CONFIG.optimisationChesthideCodespaces)
                            || eyePos > y + (5.0 * (1 + CONFIG.optimisationChesthideCodespaces)))
                        { hide = true; }
                    }
                } else if (!loc.isInPlot(pos)) { hide = true; }
                if (hide || (player != null && player.eyePos.squaredDistanceTo(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
                            >= CONFIG.optimisationChesthideDistance.pow(2.0)
                )) { return false; }
            }
        } catch (_ : Exception) {}
        return true;
    }

}