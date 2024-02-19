package com.drivequant.drivekit.common.ui.graphical

import android.content.Context
import androidx.annotation.ColorInt
import com.drivequant.drivekit.common.ui.R

open class DKColors(context: Context) {

    private val appContext = context.applicationContext
    @ColorInt
    open fun primaryColor(): Int = appContext.getColor(R.color.primaryColor)
    @ColorInt
    open fun secondaryColor(): Int = appContext.getColor(R.color.secondaryColor)
    @ColorInt
    open fun mainFontColor(): Int = appContext.getColor(R.color.mainFontColor)
    @ColorInt
    open fun complementaryFontColor(): Int = appContext.getColor(R.color.complementaryFontColor)
    @ColorInt
    open fun fontColorOnPrimaryColor(): Int = appContext.getColor(R.color.fontColorOnPrimaryColor)
    @ColorInt
    open fun fontColorOnSecondaryColor(): Int = appContext.getColor(R.color.fontColorOnSecondaryColor)
    @ColorInt
    open fun neutralColor(): Int = appContext.getColor(R.color.neutralColor)
    @ColorInt
    open fun backgroundViewColor(): Int = appContext.getColor(R.color.backgroundViewColor)
    @ColorInt
    open fun warningColor(): Int = appContext.getColor(R.color.warningColor)
    @ColorInt
    open fun criticalColor(): Int = appContext.getColor(R.color.criticalColor)
    @ColorInt
    open fun transparentColor(): Int = appContext.getColor(R.color.transparentColor)
}
