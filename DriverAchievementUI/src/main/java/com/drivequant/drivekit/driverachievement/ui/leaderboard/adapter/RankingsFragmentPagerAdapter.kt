package com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.fragment.RankingListFragment
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingListViewModel

class RankingsFragmentPagerAdapter(
    private val viewModel: RankingListViewModel,
    private val context: Context,
    fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment =
        RankingListFragment.newInstance(viewModel, DriverAchievementUI.rankingTypes[position])


    override fun getPageTitle(position: Int): CharSequence? {
        return when(DriverAchievementUI.rankingTypes[position]) {
            RankingType.SAFETY ->  context.getString(R.string.dk_common_safety)
            RankingType.DISTRACTION -> context.getString(R.string.dk_common_distraction)
            RankingType.ECO_DRIVING ->  context.getString(R.string.dk_common_ecodriving)
        }
    }

    override fun getCount(): Int = DriverAchievementUI.rankingTypes.size
}