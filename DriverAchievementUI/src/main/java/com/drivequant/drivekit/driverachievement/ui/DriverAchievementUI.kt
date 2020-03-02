package com.drivequant.drivekit.driverachievement.ui

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverAchievementUIEntryPoint
import com.drivequant.drivekit.databaseutils.entity.StreakTheme
import com.drivequant.drivekit.driverachievement.ui.streaks.activity.StreaksListActivity
import com.drivequant.drivekit.driverachievement.ui.streaks.fragment.StreaksListFragment

object DriverAchievementUI : DriverAchievementUIEntryPoint {

    lateinit var streakThemes:List<StreakTheme>

    fun initialize(streakThemes:  List<StreakTheme> = listOf(
        StreakTheme.PHONE_DISTRACTION,
        StreakTheme.SAFETY,
        StreakTheme.SPEEDING,
        StreakTheme.ACCELERATION,
        StreakTheme.BRAKE,
        StreakTheme.ADHERENCE)) {

        this.streakThemes = streakThemes
        DriveKitNavigationController.driverAchievementUIEntryPoint = this
    }

    override fun startStreakListActivity(context: Context) {
        val intent = Intent(context, StreaksListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun createStreakListFragment(): Fragment = StreaksListFragment()
}