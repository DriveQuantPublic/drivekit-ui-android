package com.drivequant.drivekit.common.ui

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.drivequant.drivekit.common.ui.analytics.DriveKitAnalyticsListener
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.graphical.DKFonts
import com.drivequant.drivekit.common.ui.utils.DistanceUnit
import com.drivequant.drivekit.core.scoreslevels.DKScoreType

object DriveKitUI {

    var analyticsListener: DriveKitAnalyticsListener? = null
    var distanceUnit: DistanceUnit = DistanceUnit.KM
    lateinit var colors: DKColors

    var scores: List<DKScoreType> = DKScoreType.values().toList()
        get() = field.filter { it.hasAccess() }
        set(value) {
            field = value.ifEmpty {
                listOf(DKScoreType.SAFETY)
            }
        }

    private var fonts = DKFonts()

    @JvmOverloads
    fun initialize(context: Context, colors: DKColors = DKColors(context), fonts: DKFonts = DKFonts()) {
        this.colors = colors
        this.fonts = fonts
    }

    fun configureAnalytics(listener: DriveKitAnalyticsListener) {
        this.analyticsListener = listener
    }

    fun primaryFont(context: Context): Typeface? = ResourcesCompat.getFont(context, fonts.primaryFont())

    fun secondaryFont(context: Context): Typeface? = ResourcesCompat.getFont(context, fonts.secondaryFont())
}
