package com.drivequant.drivekit.common.ui.graphical

import android.graphics.Color
import androidx.annotation.ColorInt

open class DKColors {
    @ColorInt
    open fun primaryColor(): Int = Color.parseColor("#0B4D6E")
    @ColorInt
    open fun secondaryColor(): Int = Color.parseColor("#77E2B0")
    @ColorInt
    open fun mainFontColor(): Int = Color.parseColor("#161616")
    @ColorInt
    open fun complementaryFontColor(): Int = Color.parseColor("#9E9E9E")
    @ColorInt
    open fun fontColorOnPrimaryColor(): Int = Color.WHITE
    @ColorInt
    open fun fontColorOnSecondaryColor(): Int = Color.WHITE
    @ColorInt
    open fun neutralColor(): Int = Color.parseColor("#F0F0F0")
    @ColorInt
    open fun backgroundViewColor(): Int = Color.parseColor("#FAFAFA")
    @ColorInt
    open fun warningColor(): Int = Color.parseColor("#F7A334")
    @ColorInt
    open fun criticalColor(): Int = Color.parseColor("#E52027")
    @ColorInt
    open fun transparentColor(): Int = Color.parseColor("#000000FF")
}
