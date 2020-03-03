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
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKSpannable


class StreaksData(
    private var streakTheme: StreakTheme, private var best: StreakResult,
    private var current: StreakResult) {
    private val currentStartDate = current.startDate.formatDate(DKDatePattern.FORMAT_STANDARD_DATE)
    private val currentTripsCount = current.tripNumber
    private val bestStartDate = best.startDate.formatDate(DKDatePattern.FORMAT_STANDARD_DATE)
    private val bestEndDate = best.endDate.formatDate(DKDatePattern.FORMAT_STANDARD_DATE)
    val bestTripsCount = best.tripNumber

    fun getTitle(context: Context): String {
        return when (streakTheme) {
            PHONE_DISTRACTION -> context.getString(R.string.dk_achievements_streaks_phone_distraction_title)
            SAFETY -> context.getString(R.string.dk_achievements_streaks_safety_title)
            SPEEDING -> context.getString(R.string.dk_achievements_streaks_speeding_title)
            ACCELERATION -> context.getString(R.string.dk_achievements_streaks_acceleration_title)
            BRAKE -> context.getString(R.string.dk_achievements_streaks_brake_title)
            ADHERENCE -> context.getString(R.string.dk_achievements_streaks_adherence_title)
        }
    }

    fun getIcon(): Int {
        return when (streakTheme) {
            PHONE_DISTRACTION -> R.drawable.dk_common_distraction
            SAFETY -> R.drawable.dk_common_safety
            SPEEDING -> R.drawable.dk_common_eco_decel
            ACCELERATION -> R.drawable.dk_common_eco_accel
            BRAKE -> R.drawable.dk_common_safety_decel
            ADHERENCE -> R.drawable.dk_common_safety_adherence
        }
    }

    private fun getResetText(context: Context): String {
        return when (streakTheme) {
            PHONE_DISTRACTION -> context.getString(R.string.dk_achievements_streaks_phone_distraction_reset)
            SAFETY -> context.getString(R.string.dk_achievements_streaks_safety_reset)
            SPEEDING -> context.getString(R.string.dk_achievements_streaks_speeding_reset)
            ACCELERATION -> context.getString(R.string.dk_achievements_streaks_acceleration_reset)
            BRAKE -> context.getString(R.string.dk_achievements_streaks_brake_reset)
            ADHERENCE -> context.getString(R.string.dk_achievements_streaks_adherence_reset)
        }
    }

    fun getDescriptionText(context: Context): String {
        return when (streakTheme) {
            PHONE_DISTRACTION -> context.getString(R.string.dk_achievements_streaks_phone_distraction_text)
            SAFETY -> context.getString(R.string.dk_achievements_streaks_safety_text)
            SPEEDING -> context.getString(R.string.dk_achievements_streaks_speeding_text)
            ACCELERATION -> context.getString(R.string.dk_achievements_streaks_acceleration_text)
            BRAKE -> context.getString(R.string.dk_achievements_streaks_brake_text)
            ADHERENCE -> context.getString(R.string.dk_achievements_streaks_adherence_text)
        }
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
        val currentDistance = DKDataFormatter.formatDistance(context, current.distance)
        val currentDuration = DKDataFormatter.formatDuration(context, current.duration)
        val trip = context.resources.getQuantityString(R.plurals.trip_plural, currentTripsCount)

        return DKSpannable()
            .append("$currentTripsCount ", context.resSpans {
                color(DriveKitUI.colors.secondaryColor())
                typeface(Typeface.BOLD)
                size(R.dimen.dk_text_big)
            }).append(trip, context.resSpans {
                color(DriveKitUI.colors.secondaryColor())
            }).appendSpace(" - $currentDistance - $currentDuration", before = false).toSpannable()
    }

    fun getBestStreakData(context: Context): SpannableString {
        val bestDistance = DKDataFormatter.formatDistance(context, best.distance)
        val bestDuration = DKDataFormatter.formatDuration(context, best.duration)
        val trip = context.resources.getQuantityString(R.plurals.trip_plural, bestTripsCount)

        return when (getStreakStatus()) {
            StreakStatus.INIT,StreakStatus.IN_PROGRESS, StreakStatus.RESET ->

                DKSpannable()
                .append("$bestTripsCount", context.resSpans {
                    typeface(Typeface.BOLD)
                    size(R.dimen.dk_text_big)
                }).appendSpace("$trip - $bestDistance - $bestDuration", before = true).toSpannable()

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
            StreakStatus.IN_PROGRESS -> context.getString(R.string.dk_achievements_streaks_congrats_text)
            StreakStatus.RESET -> context.getString(R.string.dk_achievements_streaks_since_to, bestStartDate, bestEndDate)
            StreakStatus.BEST -> context.getString(R.string.dk_achievements_streaks_congrats_text)
        }
    }
}