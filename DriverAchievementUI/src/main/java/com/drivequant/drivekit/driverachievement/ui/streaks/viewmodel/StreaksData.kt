package com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.StreakResult
import com.drivequant.drivekit.databaseutils.entity.StreakTheme.*
import com.drivequant.drivekit.databaseutils.entity.StreakTheme
import com.drivequant.drivekit.driverachievement.ui.R

class StreaksData {

    private var streakTheme: StreakTheme
    private var best: StreakResult
    private var current: StreakResult

    constructor(streakTheme: StreakTheme, best: StreakResult, current: StreakResult) {
        this.streakTheme = streakTheme
        this.best = best
        this.current = current
    }

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

    fun getStreakStatus(): StreaksStatus {
        return if (best.tripNumber == 0 && current.tripNumber == 0) {
            StreaksStatus.INIT
        } else if (best.tripNumber < current.tripNumber) {
            StreaksStatus.BEST
        } else if (current.tripNumber == 1) {
            StreaksStatus.FIRST
        } else {
            StreaksStatus.IN_PROGRESS
        }
    }

    fun getBestStreak(): StreakResult {
        return best
    }

    fun getCurrentStreak(): StreakResult {
        return current
    }
}