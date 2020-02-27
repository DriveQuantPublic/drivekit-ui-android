package com.drivequant.drivekit.common.ui.graphical

import android.graphics.Color

interface DKColors {
    fun primaryColor(): Int = Color.parseColor("#0B4D6E")
    fun secondaryColor(): Int = Color.parseColor("#77E2B2")
    fun mainFontColor(): Int = Color.parseColor("#616161")
    fun complementaryFontColor(): Int = Color.parseColor("#9E9E9E")
    fun fontColorOnPrimaryColor(): Int = Color.WHITE
    fun fontColorOnSecondaryColor(): Int = Color.WHITE
    fun neutralColor(): Int = Color.parseColor("#F0F0F0")
    fun backgroundViewColor(): Int = Color.parseColor("#FAFAFA")
    fun warningColor(): Int = Color.parseColor("#FF6E57")
    fun criticalColor(): Int = Color.parseColor("#E52027")
}



