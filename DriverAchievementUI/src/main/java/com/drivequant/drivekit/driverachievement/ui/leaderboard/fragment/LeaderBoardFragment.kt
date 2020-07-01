package com.drivequant.drivekit.driverachievement.ui.leaderboard.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter.RankingsFragmentPagerAdapter
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingListViewModel
import kotlinx.android.synthetic.main.dk_fragment_leaderboard.*

class LeaderBoardFragment : Fragment() {

    lateinit var rankingViewModel: RankingListViewModel
    lateinit var rankingsFragmentPagerAdapter: RankingsFragmentPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTabLayout()
        setViewPager()
        rankingViewModel = ViewModelProviders.of(this).get(RankingListViewModel::class.java)

        weekly.setOnClickListener {
            val currentFragment = rankingsFragmentPagerAdapter.currentFragment as RankingListFragment
            currentFragment.updateRanking(RankingPeriod.WEEKLY)
        }

        monthly.setOnClickListener {
            val currentFragment = rankingsFragmentPagerAdapter.currentFragment as RankingListFragment
            currentFragment.updateRanking(RankingPeriod.MONTHLY)
        }

        legacy.setOnClickListener {
            val currentFragment = rankingsFragmentPagerAdapter.currentFragment as RankingListFragment
            currentFragment.updateRanking(RankingPeriod.LEGACY)
        }
    }

    private fun createRankingSelectors(selectorText: String): Button {
        val button = Button(requireContext())
        button.text = selectorText
        button.setBackgroundResource(R.drawable.button_selector)
        return button
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dk_fragment_leaderboard, container, false)
    }

    private fun setViewPager() {
      rankingsFragmentPagerAdapter =  RankingsFragmentPagerAdapter(childFragmentManager)
        view_pager_leader_board.offscreenPageLimit = 4
        view_pager_leader_board.adapter = rankingsFragmentPagerAdapter


        for ((index, rankType) in DriverAchievementUI.rankingTypes.withIndex()) {
            tab_layout_leader_board.getTabAt(index)?.let {
                val tabIcon = when (rankType) {
                    RankingType.DISTRACTION -> R.drawable.dk_common_distraction
                    RankingType.ECO_DRIVING -> R.drawable.dk_common_ecodriving
                    RankingType.SAFETY -> R.drawable.dk_common_safety
                }
                it.setIcon(tabIcon)
            }
        }

        view_pager_leader_board.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val currentFragment = rankingsFragmentPagerAdapter.fragments[position] as RankingListFragment
                currentFragment.updateRanking()
            }

            override fun onPageScrollStateChanged(position: Int) {

            }
        })
    }

    private fun setTabLayout() {
        tab_layout_leader_board.setupWithViewPager(view_pager_leader_board)
        if (DriverAchievementUI.rankingTypes.size == 1) {
            tab_layout_leader_board.removeAllTabs()
        }
    }
}