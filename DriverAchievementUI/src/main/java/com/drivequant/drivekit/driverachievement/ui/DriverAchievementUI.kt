package com.drivequant.drivekit.driverachievement.ui

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverAchievementUIEntryPoint
import com.drivequant.drivekit.databaseutils.entity.BadgeCategory
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.databaseutils.entity.StreakTheme
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorType
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
        BadgeCategory.GENERIC,
        BadgeCategory.SAFETY,
        BadgeCategory.ECO_DRIVING,
        BadgeCategory.PHONE_DISTRACTION)

    internal var rankingTypes = listOf(
        RankingType.SAFETY,
        RankingType.ECO_DRIVING,
        RankingType.DISTRACTION)

    internal var rankingSelector: RankingSelectorType = RankingSelectorType.PERIOD(
        listOf(RankingPeriod.WEEKLY, RankingPeriod.MONTHLY, RankingPeriod.ALL_TIME)
    )

    internal var rankingDepth:Int = 5

    fun initialize() {
        DriveKitNavigationController.driverAchievementUIEntryPoint = this
    }

    fun configureStreakThemes(streakThemes: List<StreakTheme>) {
        this.streakThemes = streakThemes
    }

    fun configureBadgeCategories(badgeCategories: MutableList<BadgeCategory>) {
        if (!badgeCategories.contains(BadgeCategory.GENERIC)) {
            badgeCategories.add(BadgeCategory.GENERIC)
        }
        this.badgeCategories = badgeCategories
    }

    fun configureRankingSelector(rankingSelector: RankingSelectorType) {
        this.rankingSelector = rankingSelector
    }

    fun configureRankingTypes(rankingTypes: List<RankingType>) {
        if (rankingTypes.isEmpty()) {
            this.rankingTypes = listOf(RankingType.SAFETY)
        } else {
            this.rankingTypes = rankingTypes.distinct()
        }
    }

    fun configureRankingDepth(rankingDepth: Int) {
        this.rankingDepth = rankingDepth
    }

    override fun startStreakListActivity(context: Context) {
        val intent = Intent(context, StreaksListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun createStreakListFragment(): Fragment = StreaksListFragment()
}