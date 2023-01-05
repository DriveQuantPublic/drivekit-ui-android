package com.drivequant.drivekit.common.ui.extension

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils

/**
 * Take the tint/hue of the `color` and apply it to `this`.
 * If the `color` is fully desaturated, it only keeps the luminosity and alpha of `this`.
 * If `color` is fully transparent, it returns `this` unmodified.
 * @param color The base color from which to get the tint/hue to apply.
 * @return the current color tinted using `color`'s hue.
 */
@ColorInt
fun Int.tintFromHueOfColor(@ColorInt color: Int): Int {
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

@FloatRange(from = 0.0, to = 360.0)
fun Int.getHue(): Float = this.getHslValues().getHue()

fun FloatArray.getHue(): Float = this[0]

fun FloatArray.setHue(@FloatRange(from = 0.0, to = 360.0) hue: Float) {
    this[0] = hue
}

@FloatRange(from = 0.0, to = 1.0)
fun Int.getSaturation(): Float = this.getHslValues().getSaturation()

fun FloatArray.getSaturation(): Float = this[1]

fun FloatArray.setSaturation(@FloatRange(from = 0.0, to = 1.0) saturation: Float) {
    this[1] = saturation
}

@FloatRange(from = 0.0, to = 1.0)
fun Int.getLightness(): Float = this.getHslValues().getLightness()

fun FloatArray.getLightness(): Float = this[2]

fun FloatArray.setLightness(@FloatRange(from = 0.0, to = 1.0) lightness: Float) {
    this[2] = lightness
}

private fun Int.getHslValues(): FloatArray {
    val hslValues = FloatArray(3)
    ColorUtils.colorToHSL(this, hslValues)
    return hslValues
}
