package com.drivequant.drivekit.common.ui

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.graphical.DKFonts
import com.drivequant.drivekit.common.ui.utils.DistanceUnit

object DriveKitUI {

    var distanceUnit: DistanceUnit = DistanceUnit.KM
    lateinit var colors: DKColors
    private lateinit var fonts: DKFonts

    @JvmOverloads
    fun initialize(colors: DKColors = DKColors(), fonts: DKFonts = DKFonts()) {
        this.colors = colors
        this.fonts = fonts
    }

    fun primaryFont(context: Context): Typeface? = ResourcesCompat.getFont(context, fonts.primaryFont())

    fun secondaryFont(context: Context): Typeface? = ResourcesCompat.getFont(context, fonts.secondaryFont())

}