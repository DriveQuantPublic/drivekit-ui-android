package com.drivequant.drivekit.driverachievement.ui.leaderboard.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.RankingSelectorType
import com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter.RankingSelectorAdapter
import com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter.RankingsFragmentPagerAdapter
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingListViewModel
import kotlinx.android.synthetic.main.dk_fragment_leaderboard.*

class LeaderBoardFragment : Fragment(), RankingSelectorAdapter.RankingSelectorListener {

    lateinit var rankingViewModel: RankingListViewModel
    lateinit var rankingsFragmentPagerAdapter: RankingsFragmentPagerAdapter
    lateinit var rankingSelectorAdapter: RankingSelectorAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rankingViewModel = ViewModelProviders.of(this).get(RankingListViewModel::class.java)
        setTabLayout()
        setViewPager()

    }

    override fun onResume() {
        super.onResume()
        createRankingSelectors()
    }


    private fun createRankingSelectors() {
        when (val rankingSelectorType = DriverAchievementUI.rankingSelector) {
            is RankingSelectorType.NONE -> {
                recycler_view_selector.visibility = View.GONE
            }
            is RankingSelectorType.PERIOD -> {
                rankingSelectorAdapter = RankingSelectorAdapter(
                    requireContext(),
                    rankingSelectorType.rankingPeriods,
                    this
                )
            }
        }
        recycler_view_selector.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recycler_view_selector.adapter = rankingSelectorAdapter
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
        rankingsFragmentPagerAdapter = RankingsFragmentPagerAdapter(childFragmentManager)
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
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val currentFragment =
                    rankingsFragmentPagerAdapter.fragments[position] as RankingListFragment
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

    override fun onClickSelector(rankingPeriod: RankingPeriod) {
        val currentFragment = rankingsFragmentPagerAdapter.currentFragment as RankingListFragment
        currentFragment.updateRanking(rankingPeriod)
    }
}