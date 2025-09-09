package com.drivequant.drivekit.common.ui.graphical

import androidx.annotation.ColorInt
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.core.DriveKit

object DKColors {
    private val appContext = DriveKit.applicationContext

    @ColorInt
    val primaryColor: Int = appContext.getColor(R.color.primaryColor)
    @ColorInt
    val secondaryColor: Int = appContext.getColor(R.color.secondaryColor)
    @ColorInt
    val mainFontColor: Int = appContext.getColor(R.color.mainFontColor)
    @ColorInt
    val complementaryFontColor: Int = appContext.getColor(R.color.complementaryFontColor)
    @ColorInt
    val fontColorOnPrimaryColor: Int = appContext.getColor(R.color.fontColorOnPrimaryColor)
    @ColorInt
    val fontColorOnSecondaryColor: Int = appContext.getColor(R.color.fontColorOnSecondaryColor)
    @ColorInt
    val neutralColor: Int = appContext.getColor(R.color.neutralColor)
    @ColorInt
    val backgroundViewColor: Int = appContext.getColor(R.color.backgroundViewColor)
    @ColorInt
    val informationColor: Int = appContext.getColor(R.color.informationColor)
    @ColorInt
    val warningColor: Int = appContext.getColor(R.color.warningColor)
    @ColorInt
    val criticalColor: Int = appContext.getColor(R.color.criticalColor)
    @ColorInt
    val transparentColor: Int = appContext.getColor(R.color.transparentColor)
}
