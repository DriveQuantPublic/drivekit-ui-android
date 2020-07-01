package com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import com.drivequant.drivekit.databaseutils.entity.RankingType

import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.leaderboard.fragment.RankingListFragment

class RankingsFragmentPagerAdapter(
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    val fragments = mutableListOf<Fragment>()
    var currentFragment: Fragment? = null

    init {
        for (rankingType in DriverAchievementUI.rankingTypes) {
            val fragment =  when (rankingType) {
                RankingType.SAFETY -> RankingListFragment.newInstance(
                    RankingType.SAFETY
                )

                RankingType.DISTRACTION -> RankingListFragment.newInstance(
                    RankingType.DISTRACTION
                )

                RankingType.ECO_DRIVING -> RankingListFragment.newInstance(
                    RankingType.ECO_DRIVING
                )
            }
            fragments.add(fragment)
        }
    }

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if (currentFragment !== `object`) {
            currentFragment = `object` as Fragment
        }
        super.setPrimaryItem(container, position, `object`)
    }

    override fun getCount(): Int = DriverAchievementUI.rankingTypes.size
}