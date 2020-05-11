package com.drivequant.drivekit.driverachievement.ui

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverAchievementUIEntryPoint
import com.drivequant.drivekit.databaseutils.entity.BadgeCategory
import com.drivequant.drivekit.databaseutils.entity.StreakTheme
import com.drivequant.drivekit.driverachievement.ui.streaks.activity.StreaksListActivity
import com.drivequant.drivekit.driverachievement.ui.streaks.fragment.StreaksListFragment

object DriverAchievementUI : DriverAchievementUIEntryPoint {

    internal var streakThemes: List<StreakTheme> = listOf(
        StreakTheme.PHONE_DISTRACTION,
        StreakTheme.SAFETY,
        StreakTheme.ACCELERATION,
        StreakTheme.BRAKE,
        StreakTheme.ADHERENCE)

    internal var badgeCategories: List<BadgeCategory> = listOf(
        BadgeCategory.PHONE_DISTRACTION,
        BadgeCategory.SAFETY,
        BadgeCategory.ECO_DRIVING,
        BadgeCategory.GENERIC
    )

    fun initialize() {
        DriveKitNavigationController.driverAchievementUIEntryPoint = this
    }

    fun configureStreakThemes(streakThemes: List<StreakTheme>) {
        this.streakThemes = streakThemes
    }

    fun configureBadgeCategories(badgeCategories: List<BadgeCategory>) {
        this.badgeCategories = badgeCategories
    }

    override fun startStreakListActivity(context: Context) {
        val intent = Intent(context, StreaksListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun createStreakListFragment(): Fragment = StreaksListFragment()
}