package com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.DurationUnit
import com.drivequant.drivekit.common.ui.utils.Meter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.StreakResult
import com.drivequant.drivekit.databaseutils.entity.StreakTheme
import com.drivequant.drivekit.driverachievement.ui.R


internal class StreaksData(
    private var streakTheme: StreakTheme, private var best: StreakResult,
    private var current: StreakResult) {
    private val currentStartDate = current.startDate.formatDate(DKDatePattern.STANDARD_DATE)
    private val currentTripsCount = current.tripNumber
    private val bestStartDate = best.startDate.formatDate(DKDatePattern.STANDARD_DATE)
    private val bestEndDate = best.endDate.formatDate(DKDatePattern.STANDARD_DATE)
    val bestTripsCount = best.tripNumber

    fun getTitle(context: Context) = when (streakTheme) {
        StreakTheme.PHONE_DISTRACTION -> R.string.dk_achievements_streaks_phone_distraction_title
        StreakTheme.SAFETY -> R.string.dk_achievements_streaks_safety_title
        StreakTheme.SPEEDING -> R.string.dk_achievements_streaks_speeding_title
        StreakTheme.ACCELERATION -> R.string.dk_achievements_streaks_acceleration_title
        StreakTheme.BRAKE -> R.string.dk_achievements_streaks_brake_title
        StreakTheme.ADHERENCE -> R.string.dk_achievements_streaks_adherence_title
        StreakTheme.CALL -> R.string.dk_achievements_streaks_phone_call_title
    }.let {
        context.getString(it)
    }

    fun getIcon() = when (streakTheme) {
        StreakTheme.PHONE_DISTRACTION -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_distraction
        StreakTheme.SAFETY -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_safety
        StreakTheme.SPEEDING -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_eco_accel
        StreakTheme.ACCELERATION -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_safety_accel
        StreakTheme.BRAKE -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_safety_decel
        StreakTheme.ADHERENCE -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_safety_adherence
        StreakTheme.CALL -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_call
    }

    private fun getResetText(context: Context) = when (streakTheme) {
        StreakTheme.PHONE_DISTRACTION -> R.string.dk_achievements_streaks_phone_distraction_reset
        StreakTheme.SAFETY -> R.string.dk_achievements_streaks_safety_reset
        StreakTheme.SPEEDING -> R.string.dk_achievements_streaks_speeding_reset
        StreakTheme.ACCELERATION -> R.string.dk_achievements_streaks_acceleration_reset
        StreakTheme.BRAKE -> R.string.dk_achievements_streaks_brake_reset
        StreakTheme.ADHERENCE -> R.string.dk_achievements_streaks_adherence_reset
        StreakTheme.CALL -> R.string.dk_achievements_streaks_call_reset
    }.let {
        context.getString(it)
    }

    fun getDescriptionText(context: Context) = when (streakTheme) {
        StreakTheme.PHONE_DISTRACTION -> R.string.dk_achievements_streaks_phone_distraction_text
        StreakTheme.SAFETY -> R.string.dk_achievements_streaks_safety_text
        StreakTheme.SPEEDING -> R.string.dk_achievements_streaks_speeding_text
        StreakTheme.ACCELERATION -> R.string.dk_achievements_streaks_acceleration_text
        StreakTheme.BRAKE -> R.string.dk_achievements_streaks_brake_text
        StreakTheme.ADHERENCE -> R.string.dk_achievements_streaks_adherence_text
        StreakTheme.CALL -> R.string.dk_achievements_streaks_phone_call_text
    }.let {
        context.getString(it)
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
        val currentDistance = DKDataFormatter.formatInKmOrMile(context, Meter(current.distance)).convertToString()
        val currentDuration = DKDataFormatter.formatDuration(context, current.duration, DurationUnit.HOUR).convertToString()
        val trip = context.resources.getQuantityString(com.drivequant.drivekit.common.ui.R.plurals.trip_plural, currentTripsCount)

        return DKSpannable()
            .append("$currentTripsCount ", context.resSpans {
                color(DKColors.secondaryColor)
                typeface(Typeface.BOLD)
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_big)
            }).append(trip, context.resSpans {
                color(DKColors.secondaryColor)
            }).append(" - $currentDistance - $currentDuration", context.resSpans {
                color(DKColors.mainFontColor)
            }).toSpannable()
    }

    fun getBestStreakData(context: Context): SpannableString {
        val bestDistance = DKDataFormatter.formatInKmOrMile(context, Meter(best.distance)).convertToString()
        val bestDuration = DKDataFormatter.formatDuration(context, best.duration, DurationUnit.HOUR).convertToString()
        val trip = context.resources.getQuantityString(com.drivequant.drivekit.common.ui.R.plurals.trip_plural, bestTripsCount)

        return when (getStreakStatus()) {
            StreakStatus.INIT,StreakStatus.IN_PROGRESS, StreakStatus.RESET ->

                DKSpannable()
                    .append("$bestTripsCount ", context.resSpans {
                        typeface(Typeface.BOLD)
                        size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_big)
                        color(DKColors.mainFontColor)
                    }).append("$trip - $bestDistance - $bestDuration", context.resSpans {
                        color(DKColors.mainFontColor)
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
