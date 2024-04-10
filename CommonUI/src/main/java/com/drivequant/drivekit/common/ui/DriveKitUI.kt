package com.drivequant.drivekit.common.ui

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.drivequant.drivekit.common.ui.analytics.DriveKitAnalyticsListener
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.graphical.DKFonts
import com.drivequant.drivekit.common.ui.utils.DistanceUnit
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.scoreslevels.DKScoreType

object DriveKitUI {
    internal const val TAG = "DriveKit UI"

    var analyticsListener: DriveKitAnalyticsListener? = null
        private set

    @JvmStatic
    var distanceUnit: DistanceUnit = DistanceUnit.KM
    var colors: DKColors
        private set

    @JvmStatic
    var scores: List<DKScoreType> = DKScoreType.values().toList()
        get() = field.filter { it.hasAccess() }
        set(value) {
            field = value.ifEmpty {
                listOf(DKScoreType.SAFETY)
            }
        }

    @JvmStatic
    var fonts = DKFonts()
        private set

    init {
        DriveKit.checkInitialization()
        DriveKitLog.i(TAG, "Initialization")
        this.colors = DKColors(DriveKit.applicationContext)
    }

    @JvmStatic
    fun initialize() {
        // Nothing to do currently.
    }

    @JvmStatic
    fun configureColors(colors: DKColors) {
        this.colors = colors
    }

    @JvmStatic
    fun configureFonts(fonts: DKFonts) {
        this.fonts = fonts
    }

    @JvmStatic
    fun configureAnalytics(listener: DriveKitAnalyticsListener) {
        this.analyticsListener = listener
    }

    @JvmStatic
    fun primaryFont(context: Context): Typeface? = ResourcesCompat.getFont(context, fonts.primaryFont())

    @JvmStatic
    fun secondaryFont(context: Context): Typeface? = ResourcesCompat.getFont(context, fonts.secondaryFont())
}
