package com.drivequant.drivekit.driverachievement.ui

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.DriverAchievementUIEntryPoint
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.databaseutils.entity.BadgeCategory
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.databaseutils.entity.StreakTheme
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.rankings.activity.RankingActivity
import com.drivequant.drivekit.driverachievement.ui.rankings.extension.sanitize
import com.drivequant.drivekit.driverachievement.ui.rankings.fragment.RankingFragment
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorType
import com.drivequant.drivekit.driverachievement.ui.streaks.activity.StreaksListActivity
import com.drivequant.drivekit.driverachievement.ui.streaks.fragment.StreaksListFragment

object DriverAchievementUI : DriverAchievementUIEntryPoint {
    internal const val TAG = "DriveKit Driver Achievement UI"

    internal var streakThemes: List<StreakTheme> = StreakTheme.values().toList()
    internal var badgeCategories: List<BadgeCategory> = BadgeCategory.values().toList()
    internal var rankingTypes = listOf(RankingType.SAFETY, RankingType.ECO_DRIVING, RankingType.DISTRACTION)
        get() = field.sanitize()
        private set

    internal var rankingSelector: RankingSelectorType = RankingSelectorType.PERIOD(
        listOf(RankingPeriod.WEEKLY, RankingPeriod.MONTHLY, RankingPeriod.ALL_TIME)
    )

    internal var rankingDepth: Int = 5

    init {
        DriveKit.checkInitialization()
        DriveKitLog.i(TAG, "Initialization")
        DriveKitNavigationController.driverAchievementUIEntryPoint = this
    }

    fun initialize() {
        // Nothing to do currently.
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
            this.rankingTypes = rankingTypes
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

    @JvmOverloads
    fun startRankingActivity(context: Context, groupName: String? = null) {
        RankingActivity.launchActivity(context, groupName)
    }

    @JvmOverloads
    fun createRankingFragment(groupName: String? = null) = RankingFragment.newInstance(groupName)
}
