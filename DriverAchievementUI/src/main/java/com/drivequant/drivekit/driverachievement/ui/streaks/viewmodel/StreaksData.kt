package com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel

import com.drivequant.drivekit.databaseutils.entity.streak.StreakTheme
import com.drivequant.drivekit.databaseutils.entity.streak.StreakTheme.*

class StreaksData(val streakTheme: StreakTheme) {

    fun getTitle(): String {
        return when (streakTheme) {
            PHONE_DISTRACTION -> TODO()
            SAFETY -> TODO()
            SPEEDING -> TODO()
            ACCELERATION -> TODO()
            BRAKE -> TODO()
            ADHERENCE -> TODO()
        }
    }

    fun getIcon(): Int {
        return when (streakTheme) {
            PHONE_DISTRACTION -> TODO()
            SAFETY -> TODO()
            SPEEDING -> TODO()
            ACCELERATION -> TODO()
            BRAKE -> TODO()
            ADHERENCE -> TODO()
        }
    }

    fun getStreakStatus(): StreaksStatus {
        return StreaksStatus.INIT
    }
}