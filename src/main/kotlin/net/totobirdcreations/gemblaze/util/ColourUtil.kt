package net.totobirdcreations.gemblaze.util

import net.minecraft.util.math.MathHelper
import org.apache.commons.lang3.math.IEEE754rUtils


@Suppress("MemberVisibilityCanBePrivate")
internal object ColourUtil {

    // 0.0~1.0
    fun hsvToRgb(hue : Float, sat : Float, vlu : Float) : Triple<Float, Float, Float> {
        return intToRgb(MathHelper.hsvToRgb(hue, sat, vlu));
    }

    // 0.0~1.0
    fun rgbToHsv(red : Float, grn : Float, blu : Float) : Triple<Float, Float, Float> {
        val cMax = IEEE754rUtils.max(red, grn, blu);
        val cMin = IEEE754rUtils.min(red, grn, blu);
        val diff = cMax - cMin;
        val hue = (when (cMax) {
            cMin -> 0.0f
            red  -> (60.0f * ((grn - blu) / diff) + 360.0f)
            grn  -> (60.0f * ((blu - red) / diff) + 120.0f)
            blu  -> (60.0f * ((red - grn) / diff) + 240.0f)
            else -> 0.0f
        } / 360.0f).mod(1.0f);
        val sat = when (cMax) {
            0.0f -> 0.0f
            else -> diff / cMax
        };
        return Triple(hue, sat, cMax);
    }


    // 0.0~1.0
    fun rgbToInt(red : Float, grn : Float, blu : Float) : Int {
        val r = ((red * 255.0f).toInt() shl 16) and 0xff0000;
        val g = ((grn * 255.0f).toInt() shl  8) and 0x00ff00;
        val b = ((blu * 255.0f).toInt()       ) and 0x0000ff;
        return r or g or b;
    }

    // 0.0~1.0
    fun hsvToInt(hue : Float, sat : Float, vlu : Float) : Int {
        val (red, grn, blu) = this.hsvToRgb(hue, sat, vlu);
        return this.rgbToInt(red, grn, blu);
    }


    // 0.0~1.0
    fun intToRgb(rgb : Int) : Triple<Float, Float, Float> {
        val r = ((rgb and 0xff0000) shr 16).toFloat() / 255.0f;
        val g = ((rgb and 0x00ff00) shr  8).toFloat() / 255.0f;
        val b = ((rgb and 0x0000ff)       ).toFloat() / 255.0f;
        return Triple(r, g, b);
    }

    // 0.0~1.0
    fun intToHsv(rgb : Int) : Triple<Float, Float, Float> {
        val (red, grn, blu) = intToRgb(rgb);
        return rgbToHsv(red, grn, blu);
    }

}