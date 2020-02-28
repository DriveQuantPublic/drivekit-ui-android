package com.drivequant.drivekit.driverachievement.ui

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverAchievementUIEntryPoint
import com.drivequant.drivekit.databaseutils.entity.StreakTheme
import com.drivequant.drivekit.driverachievement.ui.streaks.fragment.StreaksListFragment

object DriverAchievementUI : DriverAchievementUIEntryPoint{

    fun initialize() {
        DriveKitNavigationController.driverAchievementUIEntryPoint = this
    }

    var streaksTheme: List<StreakTheme> = listOf(
        StreakTheme.PHONE_DISTRACTION,
        StreakTheme.SAFETY,
        StreakTheme.SPEEDING,
        StreakTheme.ACCELERATION,
        StreakTheme.BRAKE,
        StreakTheme.ADHERENCE)

    override fun createStreakFragment(): Fragment = StreaksListFragment()

    override fun startStreakActivity(context: Context, className: Class<Any>) {
        context.startActivity(Intent(context, className))
    }
}