package com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import com.drivequant.drivekit.databaseutils.entity.StreakResult
import com.drivequant.drivekit.databaseutils.entity.StreakTheme.*
import com.drivequant.drivekit.databaseutils.entity.StreakTheme
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.extension.formatStreaksDate
import com.drivequant.drivekit.driverachievement.ui.utils.DistanceUtils
import com.drivequant.drivekit.driverachievement.ui.utils.DurationUtils
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.RelativeSizeSpan


class StreaksData(
    private var streakTheme: StreakTheme, private var best: StreakResult,
    private var current: StreakResult) {
    private val currentStartDate = current.startDate.formatStreaksDate()
    private val currentTripsCount = current.tripNumber
    private val bestStartDate = best.startDate.formatStreaksDate()
    private val bestEndDate = best.endDate.formatStreaksDate()
    val bestTripsCount = best.tripNumber

    fun getTitle(context: Context): String {
        return when (streakTheme) {
            PHONE_DISTRACTION -> context.getString(R.string.dk_streaks_phone_distraction_title)
            SAFETY -> context.getString(R.string.dk_streaks_safety_title)
            SPEEDING -> context.getString(R.string.dk_streaks_speeding_title)
            ACCELERATION -> context.getString(R.string.dk_streaks_acceleration_title)
            BRAKE -> context.getString(R.string.dk_streaks_brake_title)
            ADHERENCE -> context.getString(R.string.dk_streaks_adherence_title)
        }
    }

    fun getIcon(): Int {
        return when (streakTheme) {
            PHONE_DISTRACTION -> R.drawable.dk_distraction_filled
            SAFETY -> R.drawable.dk_safety
            SPEEDING -> R.drawable.dk_speed_limit
            ACCELERATION -> R.drawable.dk_safety_accel
            BRAKE -> R.drawable.dk_safety_decel
            ADHERENCE -> R.drawable.dk_safety_adherence
        }
    }

    private fun getResetText(context: Context): String {
        return when (streakTheme) {
            PHONE_DISTRACTION -> context.getString(R.string.dk_streaks_phone_distraction_reset)
            SAFETY -> context.getString(R.string.dk_streaks_safety_reset)
            SPEEDING -> context.getString(R.string.dk_streaks_speeding_reset)
            ACCELERATION -> context.getString(R.string.dk_streaks_acceleration_reset)
            BRAKE -> context.getString(R.string.dk_streaks_brake_reset)
            ADHERENCE -> context.getString(R.string.dk_streaks_adherence_reset)
        }
    }

    fun getDescriptionText(context: Context): String {
        return when (streakTheme) {
            PHONE_DISTRACTION -> context.getString(R.string.dk_streaks_phone_distraction_text)
            SAFETY -> context.getString(R.string.dk_streaks_safety_text)
            SPEEDING -> context.getString(R.string.dk_streaks_speeding_text)
            ACCELERATION -> context.getString(R.string.dk_streaks_acceleration_text)
            BRAKE -> context.getString(R.string.dk_streaks_brake_text)
            ADHERENCE -> context.getString(R.string.dk_streaks_adherence_text)
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
        val currentDistance = DistanceUtils().formatDistance(context, current.distance)
        val currentDuration = DurationUtils().formatDuration(context, current.duration)
        return buildStreaksData(context,currentTripsCount, currentDistance, currentDuration)
    }

    fun getBestStreakData(context: Context): SpannableString {
        val bestDistance = DistanceUtils().formatDistance(context, best.distance)
        val bestDuration = DurationUtils().formatDuration(context, best.duration)

        return when (getStreakStatus()) {
            StreakStatus.INIT,StreakStatus.IN_PROGRESS, StreakStatus.RESET -> buildStreaksData(context, bestTripsCount, bestDistance, bestDuration)
            StreakStatus.BEST -> SpannableString.valueOf(context.getString(R.string.dk_streaks_congrats))
        }
    }

    fun getCurrentStreakDate(context: Context): String {
        return when (getStreakStatus()) {
            StreakStatus.INIT, StreakStatus.IN_PROGRESS, StreakStatus.BEST -> context.getString(R.string.dk_streaks_since, currentStartDate)
            StreakStatus.RESET -> getResetText(context)
        }
    }

    fun getBestStreakDate(context: Context): String {
        return when (getStreakStatus()) {
            StreakStatus.INIT -> context.getString(R.string.dk_streaks_empty)
            StreakStatus.IN_PROGRESS -> context.getString(R.string.dk_streaks_congrats_text)
            StreakStatus.RESET -> context.getString(R.string.dk_streaks_since_to, bestStartDate, bestEndDate)
            StreakStatus.BEST -> context.getString(R.string.dk_streaks_congrats_text)
        }
    }

    private fun buildStreaksData(
        context: Context,
        tripsCount: Int,
        distance: String,
        duration: String): SpannableString {

        val tripsCountText = "$tripsCount ${context.resources.getQuantityString(
            R.plurals.streak_trip_plural,
            tripsCount
        )}"
        val sb = SpannableString("$tripsCountText - $distance - $duration")
        val mediumSizeText = RelativeSizeSpan(2.0f)

        sb.setSpan(StyleSpan(Typeface.BOLD), 0, "$tripsCount".length, 0)
        sb.setSpan(mediumSizeText, 0, "$tripsCount".length, 0)
        sb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.dk_primary)), 0, tripsCountText.length, 0)
        return sb
    }
}