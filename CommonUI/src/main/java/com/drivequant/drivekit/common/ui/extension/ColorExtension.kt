package com.drivequant.drivekit.common.ui.extension

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils

@ColorInt
fun Int.tintFromHueOfColor(@ColorInt color: Int): Int {
    if (Color.alpha(color) == 0) return this
    val baseHue = color.getHue()
    val hslValues = this.getHslValues()
    hslValues.setHue(baseHue)
    return ColorUtils.HSLToColor(hslValues)
}

@FloatRange(from = 0.0, to = 360.0)
fun Int.getHue(): Float = this.getHslValues()[0]

fun FloatArray.setHue(@FloatRange(from = 0.0, to = 360.0) hue: Float) {
    this[0] = hue
}

@FloatRange(from = 0.0, to = 1.0)
fun Int.getSaturation(): Float = this.getHslValues()[1]

fun FloatArray.setSaturation(@FloatRange(from = 0.0, to = 1.0) saturation: Float) {
    this[1] = saturation
}

@FloatRange(from = 0.0, to = 1.0)
fun Int.getLightness(): Float = this.getHslValues()[2]

fun FloatArray.setLightness(@FloatRange(from = 0.0, to = 1.0) lightness: Float) {
    this[2] = lightness
}

private fun Int.getHslValues(): FloatArray {
    val hslValues = FloatArray(3)
    ColorUtils.colorToHSL(this, hslValues)
    return hslValues
}
