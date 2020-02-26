package com.drivequant.drivekit.common.ui

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat

object DriveKitUI {

    lateinit var colors: DKColors
    lateinit var fonts: DKFonts

    @JvmOverloads
    fun initialize(colors: DKDefaultColors = DKDefaultColors(), fonts: DKFonts = DKDefaultFont()) {
        this.colors = colors
        this.fonts = fonts
    }

    fun primaryFont(context: Context): Typeface? =
        ResourcesCompat.getFont(context, fonts.secondaryFont)

    fun secondaryFont(context: Context): Typeface? =
        ResourcesCompat.getFont(context, fonts.secondaryFont)


}