package com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel

import android.content.Context
import android.graphics.Typeface
import com.drivequant.drivekit.databaseutils.entity.StreakResult
import com.drivequant.drivekit.databaseutils.entity.StreakTheme.*
import com.drivequant.drivekit.databaseutils.entity.StreakTheme
import com.drivequant.drivekit.driverachievement.ui.R
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.*


internal class StreaksData(
    private var streakTheme: StreakTheme, private var best: StreakResult,
    private var current: StreakResult) {
    private val currentStartDate = current.startDate.formatDate(DKDatePattern.STANDARD_DATE)
    private val currentTripsCount = current.tripNumber
    private val bestStartDate = best.startDate.formatDate(DKDatePattern.STANDARD_DATE)
    private val bestEndDate = best.endDate.formatDate(DKDatePattern.STANDARD_DATE)
    val bestTripsCount = best.tripNumber

    fun getTitle(context: Context) = when (streakTheme) {
        PHONE_DISTRACTION -> "dk_achievements_streaks_phone_distraction_title"
        SAFETY -> "dk_achievements_streaks_safety_title"
        SPEEDING -> "dk_achievements_streaks_speeding_title"
        ACCELERATION -> "dk_achievements_streaks_acceleration_title"
        BRAKE -> "dk_achievements_streaks_brake_title"
        ADHERENCE -> "dk_achievements_streaks_adherence_title"
        CALL -> "dk_achievements_streaks_phone_call_title"
    }.let {
        DKResource.convertToString(context, it)
    }

    fun getIcon() = when (streakTheme) {
        PHONE_DISTRACTION -> R.drawable.dk_common_distraction
        SAFETY -> R.drawable.dk_common_safety
        SPEEDING -> R.drawable.dk_common_eco_accel
        ACCELERATION -> R.drawable.dk_common_safety_accel
        BRAKE -> R.drawable.dk_common_safety_decel
        ADHERENCE -> R.drawable.dk_common_safety_adherence
        CALL -> R.drawable.dk_common_call
    }

    private fun getResetText(context: Context) = when (streakTheme) {
        PHONE_DISTRACTION -> "dk_achievements_streaks_phone_distraction_reset"
        SAFETY -> "dk_achievements_streaks_safety_reset"
        SPEEDING -> "dk_achievements_streaks_speeding_reset"
        ACCELERATION -> "dk_achievements_streaks_acceleration_reset"
        BRAKE -> "dk_achievements_streaks_brake_reset"
        ADHERENCE -> "dk_achievements_streaks_adherence_reset"
        CALL -> "dk_achievements_streaks_call_reset"
    }.let {
        DKResource.convertToString(context, it)
    }

    fun getDescriptionText(context: Context) = when (streakTheme) {
        PHONE_DISTRACTION -> "dk_achievements_streaks_phone_distraction_text"
        SAFETY -> "dk_achievements_streaks_safety_text"
        SPEEDING -> "dk_achievements_streaks_speeding_text"
        ACCELERATION -> "dk_achievements_streaks_acceleration_text"
        BRAKE -> "dk_achievements_streaks_brake_text"
        ADHERENCE -> "dk_achievements_streaks_adherence_text"
        CALL -> "dk_achievements_streaks_phone_call_text"
    }.let {
        DKResource.convertToString(context, it)
    }

    fun getStreakStatus(): StreakStatus {
        return if (currentTripsCount == 0 && bestTripsCount == 0) {
            StreakStatus.INIT
        } else if (bestTripsCount == currentTripsCount && bestTripsCount != 0 && currentTripsCount != 0) {
            StreakStatus.BEST
        } else if (currentTripsCount == 0 && bestTripsCount != 0) {
            StreakStatus.RESET
        } else {
            StreakStatus.IN_PROGRESS
        }
    }

    fun computePercentage(): Int {
        return if (bestTripsCount != 0) {
            ((currentTripsCount * 100) / bestTripsCount) + 2
        } else {
            2
        }
    }

    fun getCurrentStreakData(context: Context) : SpannableString {
        val currentDistance = DKDataFormatter.formatMeterDistanceInKm(context, current.distance).convertToString()
        val currentDuration = DKDataFormatter.formatDuration(context, current.duration, DurationUnit.HOUR).convertToString()
        val trip = context.resources.getQuantityString(R.plurals.trip_plural, currentTripsCount)

        return DKSpannable()
            .append("$currentTripsCount ", context.resSpans {
                color(DriveKitUI.colors.secondaryColor())
                typeface(Typeface.BOLD)
                size(R.dimen.dk_text_big)
            }).append(trip, context.resSpans {
                color(DriveKitUI.colors.secondaryColor())
            }).append(" - $currentDistance - $currentDuration", context.resSpans {
                color(DriveKitUI.colors.mainFontColor())
            }).toSpannable()
    }

    fun getBestStreakData(context: Context): SpannableString {
        val bestDistance = DKDataFormatter.formatMeterDistanceInKm(context, best.distance).convertToString()
        val bestDuration = DKDataFormatter.formatDuration(context, best.duration, DurationUnit.HOUR).convertToString()
        val trip = context.resources.getQuantityString(R.plurals.trip_plural, bestTripsCount)

        return when (getStreakStatus()) {
            StreakStatus.INIT,StreakStatus.IN_PROGRESS, StreakStatus.RESET ->

                DKSpannable()
                    .append("$bestTripsCount ", context.resSpans {
                        typeface(Typeface.BOLD)
                        size(R.dimen.dk_text_big)
                        color(DriveKitUI.colors.mainFontColor())
                    }).append("$trip - $bestDistance - $bestDuration", context.resSpans {
                        color(DriveKitUI.colors.mainFontColor())
                    }).toSpannable()

            StreakStatus.BEST -> SpannableString.valueOf(context.getString(R.string.dk_achievements_streaks_congrats))
        }
    }

    fun getCurrentStreakDate(context: Context): String {
        return when (getStreakStatus()) {
            StreakStatus.INIT, StreakStatus.IN_PROGRESS, StreakStatus.BEST -> context.getString(R.string.dk_achievements_streaks_since, currentStartDate)
            StreakStatus.RESET -> getResetText(context)
        }
    }

    fun getBestStreakDate(context: Context): String {
        return when (getStreakStatus()) {
            StreakStatus.INIT -> context.getString(R.string.dk_achievements_streaks_empty)
            StreakStatus.IN_PROGRESS, StreakStatus.RESET -> context.getString(R.string.dk_achievements_streaks_since_to, bestStartDate, bestEndDate)
            StreakStatus.BEST -> context.getString(R.string.dk_achievements_streaks_congrats_text)
        }
    }
}

internal enum class StreakStatus {
    INIT, IN_PROGRESS, BEST, RESET
}
