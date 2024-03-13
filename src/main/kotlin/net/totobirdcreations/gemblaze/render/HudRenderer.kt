package net.totobirdcreations.gemblaze.render

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper
import net.totobirdcreations.gemblaze.Main
import net.totobirdcreations.gemblaze.util.ColourUtil
import net.totobirdcreations.gemblaze.util.CpuOverlayPosition
import kotlin.math.pow


internal object HudRenderer : HudRenderCallback {

    private const val LAGSLAYER_BAR_WIDTH : Int = 30;

    private var lagslayerPercent  : Float = 0.0f;
    private var lagslayerTimeLeft : Float = 0.0f;

    fun updateLagslayer(percent : Float) {
        this.lagslayerPercent  = percent;
        this.lagslayerTimeLeft = 100.0f;
    }


    override fun onHudRender(context : DrawContext, tickDelta: Float) {
        if (Main.location == null) { return; }

        // CpuOverlayPosition
        val position = Main.CONFIG.interfaceCpuOverlayPosition;
        if (this.lagslayerTimeLeft > 0.0f && position != CpuOverlayPosition.ACTIONBAR) {
            val lines : MutableList<MutableList<Text>> = mutableListOf();
            this.lagslayerTimeLeft -= tickDelta;
            lines.add(mutableListOf(
                Text.literal("ᴄᴘᴜ ᴜsᴀɢᴇ ").formatted(Formatting.WHITE),
                Text.literal("%.2f".format(this.lagslayerPercent)).formatted(Formatting.WHITE).formatted(Formatting.BOLD),
                Text.literal("%").formatted(Formatting.WHITE)
            ));
            val lagslayerFrac = MathHelper.clamp(this.lagslayerPercent, 0.0f, 100.0f) / 100.0f;
            val leftCount     = (LAGSLAYER_BAR_WIDTH.toFloat() * lagslayerFrac).toInt();
            var a = Text.literal("|".repeat(leftCount)).setStyle(Style.EMPTY.withColor(
                ColourUtil.hsvToInt(0.25f * (1.0f - lagslayerFrac.pow(5.0f)), 1.0f, if (lagslayerFrac >= 1.0) {0.75f} else {1.0f})
            ));
            var b = Text.literal("|".repeat(LAGSLAYER_BAR_WIDTH - leftCount)).formatted(Formatting.DARK_GRAY);
            if (position.right) {
                val c = a;
                a = b;
                b = c;
            }
            lines.add(mutableListOf(
                Text.literal("[ ").formatted(Formatting.DARK_GRAY),
                a,
                b,
                Text.literal(" ]").formatted(Formatting.DARK_GRAY)
            ));

            val lineHeight = Main.CLIENT.textRenderer.fontHeight;
            val margin     = 5;
            var y          = if (position.bottom) {context.scaledWindowHeight - lineHeight - margin} else {margin};
            for (line in lines) {
                var x = if (position.right) {context.scaledWindowWidth - margin} else {margin};
                if (position.right) {line.reverse();}
                for (text in line) {
                    val width = Main.CLIENT.textRenderer.getWidth(text);
                    if (position.right) {x -= width;}
                    context.drawText(
                        Main.CLIENT.textRenderer,
                        text,
                        x, y,
                        16777215, true
                    );
                    if (! position.right) {x += width;}
                }
                y += if (position.bottom) {-lineHeight} else {lineHeight};
            };
        }

    }

}