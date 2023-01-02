package com.drivequant.drivekit.common.ui.graphical

import android.graphics.Color

open class DKColors {
    open fun primaryColor(): Int = Color.parseColor("#0B4D6E")
    open fun secondaryColor(): Int = Color.parseColor("#77E2B0")
    open fun mainFontColor(): Int = Color.parseColor("#161616")
    open fun complementaryFontColor(): Int = Color.parseColor("#9E9E9E")
    open fun fontColorOnPrimaryColor(): Int = Color.WHITE
    open fun fontColorOnSecondaryColor(): Int = Color.WHITE
    open fun neutralColor(): Int = Color.parseColor("#F0F0F0")
    open fun backgroundViewColor(): Int = Color.parseColor("#FAFAFA")
    open fun warningColor(): Int = Color.parseColor("#F7A334")
    open fun criticalColor(): Int = Color.parseColor("#E52027")
    open fun transparentColor(): Int = Color.parseColor("#000000FF")
}
