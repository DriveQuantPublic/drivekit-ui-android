package com.drivequant.drivekit.common.ui.extension

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.graphics.ColorUtils

/**
 * Take the tint/hue of the `color` and apply it to `this`.
 * If the `color` is fully desaturated, it only keeps the luminosity and alpha of `this`.
 * If `color` is fully transparent, it returns `this` unmodified.
 * @param color The base color from which to get the tint/hue to apply.
 * @return the current color tinted using `color`'s hue.
 */
@ColorInt
fun @receiver:ColorInt Int.tintFromHueOfColor(@ColorInt color: Int): Int {
    if (Color.alpha(color) == 0) return this
    val baseHslValues = color.getHslValues()
    val baseHue = baseHslValues.getHue()
    val baseSaturation = baseHslValues.getSaturation()
    val hslValues = this.getHslValues()
    hslValues.setHue(baseHue)
    if (baseSaturation == 0f) {
        hslValues.setSaturation(0f)
    }
    return ColorUtils.HSLToColor(hslValues)
}

@ColorInt
fun @receiver:ColorRes Int.tintFromHueOfColor(context: Context, @ColorRes color: Int): Int {
    return this.intColor(context).tintFromHueOfColor(color.intColor(context))
}

fun @receiver:ColorInt Int.shouldInvertTextColor(@ColorInt otherColor: Int) =
    // We should have at least a ratio of 2.8:1 or we need to invert foreground color
    ColorUtils.calculateContrast(this, otherColor) < 2.8

@FloatRange(from = 0.0, to = 360.0)
fun @receiver:ColorInt Int.getHue(): Float = this.getHslValues().getHue()

fun FloatArray.getHue(): Float = this[0]

fun FloatArray.setHue(@FloatRange(from = 0.0, to = 360.0) hue: Float) {
    this[0] = hue
}

@FloatRange(from = 0.0, to = 1.0)
fun @receiver:ColorInt Int.getSaturation(): Float = this.getHslValues().getSaturation()

fun FloatArray.getSaturation(): Float = this[1]

fun FloatArray.setSaturation(@FloatRange(from = 0.0, to = 1.0) saturation: Float) {
    this[1] = saturation
}

@FloatRange(from = 0.0, to = 1.0)
fun @receiver:ColorInt Int.getLightness(): Float = this.getHslValues().getLightness()

fun FloatArray.getLightness(): Float = this[2]

fun FloatArray.setLightness(@FloatRange(from = 0.0, to = 1.0) lightness: Float) {
    this[2] = lightness
}

private fun @receiver:ColorInt Int.getHslValues(): FloatArray {
    val hslValues = FloatArray(3)
    ColorUtils.colorToHSL(this, hslValues)
    return hslValues
}

@ColorInt
fun @receiver:ColorInt Int.withAlpha(@IntRange(from = 0x0, to = 0xFF) alpha: Int): Int {
    return ColorUtils.setAlphaComponent(this, alpha)
}

@ColorInt
fun @receiver:ColorRes Int.withAlpha(context: Context, @IntRange(from = 0x0, to = 0xFF) alpha: Int): Int {
    return context.getColor(this).withAlpha(alpha)
}

@ColorInt
fun @receiver:ColorRes Int.intColor(context: Context): Int {
    return context.getColor(this)
}

fun Drawable.tint(context: Context, @ColorRes color: Int) {
    val intColor = color.intColor(context)
    this.tintDrawable(intColor)
}
