package com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.StreakResult
import com.drivequant.drivekit.databaseutils.entity.StreakTheme.*
import com.drivequant.drivekit.databaseutils.entity.StreakTheme
import com.drivequant.drivekit.driverachievement.ui.R

class StreaksData (private var streakTheme: StreakTheme, private var best: StreakResult,
                   private var current: StreakResult
) {
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

    fun getResetText(context: Context) : String {
        return when (streakTheme) {
            PHONE_DISTRACTION -> context.getString(R.string.dk_streaks_phone_distraction_reset)
            SAFETY -> context.getString(R.string.dk_streaks_safety_reset)
            SPEEDING -> context.getString(R.string.dk_streaks_speeding_reset)
            ACCELERATION -> context.getString(R.string.dk_streaks_acceleration_reset)
            BRAKE -> context.getString(R.string.dk_streaks_brake_reset)
            ADHERENCE -> context.getString(R.string.dk_streaks_adherence_reset)
        }
    }

    fun getDescriptionText(context: Context) : String {
        return when (streakTheme) {
            PHONE_DISTRACTION -> context.getString(R.string.dk_streaks_phone_distraction_text)
            SAFETY -> context.getString(R.string.dk_streaks_safety_text)
            SPEEDING -> context.getString(R.string.dk_streaks_speeding_text)
            ACCELERATION -> context.getString(R.string.dk_streaks_acceleration_text)
            BRAKE -> context.getString(R.string.dk_streaks_brake_text)
            ADHERENCE -> context.getString(R.string.dk_streaks_adherence_text)
        }
    }

    fun getStreakStatus(): StreaksStatus {
        return if (best.tripNumber == 0 && current.tripNumber == 0) {
            StreaksStatus.INIT
        } else if (best.tripNumber <= current.tripNumber && best.tripNumber != 0 && current.tripNumber != 0) {
            StreaksStatus.BEST
        } else {
            StreaksStatus.IN_PROGRESS
        }
    }

    fun getBestStreak(): StreakResult {
        return best
    }

    fun getSteakTheme(): StreakTheme {
        return streakTheme
    }

    fun getCurrentStreak(): StreakResult {
        return current
    }

    fun computePercentage(): Int {
        if (best.tripNumber != 0) {
            val percent = ((current.tripNumber - 0) * 100) / (best.tripNumber - 0)
            if (percent == 0) return percent + 2
        }
        return 2
    }
}