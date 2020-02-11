package com.drivequant.drivekit.driverachievement.ui

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.StreakTheme

class StreaksViewConfig(
    context: Context,
    val streaksTheme: List<StreakTheme> = listOf(
        StreakTheme.PHONE_DISTRACTION,
        StreakTheme.SAFETY,
        StreakTheme.SPEEDING,
        StreakTheme.ACCELERATION,
        StreakTheme.BRAKE,
        StreakTheme.ADHERENCE
    ),
    val failedToSyncStreaks: String = context.getString(R.string.dk_failed_to_sync_streaks),
    val okText: String = context.getString(R.string.dk_ok)
)