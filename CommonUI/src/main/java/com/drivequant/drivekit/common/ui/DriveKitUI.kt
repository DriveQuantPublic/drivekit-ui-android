package com.drivequant.drivekit.common.ui

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.drivequant.drivekit.common.ui.analytics.DriveKitAnalyticsListener
import com.drivequant.drivekit.common.ui.utils.DistanceUnit
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.scoreslevels.DKScoreType

object DriveKitUI {
    internal const val TAG = "DriveKit UI"
    private var primaryFont: Typeface? = null
    private var secondaryFont: Typeface? = null

    var analyticsListener: DriveKitAnalyticsListener? = null
        private set

    @JvmStatic
    var distanceUnit: DistanceUnit = DistanceUnit.KM

    @JvmStatic
    var scores: List<DKScoreType> = DKScoreType.values().toList()
        get() = field.filter { it.hasAccess() }
        set(value) {
            field = value.ifEmpty {
                listOf(DKScoreType.SAFETY)
            }
        }

    init {
        DriveKit.checkInitialization()
        DriveKitLog.i(TAG, "Initialization")
    }

    @JvmStatic
    fun initialize() {
        // Nothing to do currently.
    }

    @JvmStatic
    fun configureAnalytics(listener: DriveKitAnalyticsListener) {
        this.analyticsListener = listener
    }

    @JvmStatic
    fun primaryFont(context: Context): Typeface? {
        var primaryFont = this.primaryFont
        if (primaryFont == null) {
            primaryFont = ResourcesCompat.getFont(context, R.font.dkprimary)
            this.primaryFont = primaryFont
        }
        return primaryFont
    }

    @JvmStatic
    fun secondaryFont(context: Context): Typeface? {
        var secondaryFont = this.secondaryFont
        if (secondaryFont == null) {
            secondaryFont = ResourcesCompat.getFont(context, R.font.dksecondary)
            this.secondaryFont = secondaryFont
        }
        return secondaryFont
    }
}
