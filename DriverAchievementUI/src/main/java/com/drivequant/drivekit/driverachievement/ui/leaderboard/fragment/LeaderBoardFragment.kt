package com.drivequant.drivekit.driverachievement.ui.leaderboard.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.RankingSelectorListener
import com.drivequant.drivekit.driverachievement.ui.leaderboard.RankingSelectorType
import com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter.RankingsFragmentPagerAdapter
import com.drivequant.drivekit.driverachievement.ui.leaderboard.commons.views.SelectorItemView
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingListViewModel
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingViewModelFactory
import kotlinx.android.synthetic.main.dk_fragment_leaderboard.*


class LeaderBoardFragment : Fragment(), RankingSelectorListener {

    lateinit var rankingViewModel: RankingListViewModel
    lateinit var rankingsFragmentPagerAdapter: RankingsFragmentPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTabLayout()
        setViewPager()
        createRankingSelectors()
    }

    private fun createRankingSelectors() {
        if (DriverAchievementUI.rankingSelector is RankingSelectorType.NONE) {
            selectors_container.visibility = View.GONE
        } else {
            when (val rankingSelectorType = DriverAchievementUI.rankingSelector) {
                is RankingSelectorType.NONE -> {
                    //TODO
                }
                is RankingSelectorType.PERIOD -> {
                    if(rankingSelectorType.rankingPeriods.size == 1) {
                        selectors_container.visibility = View.GONE
                        if (!this::rankingViewModel.isInitialized) {
                            rankingViewModel = ViewModelProviders.of(this, RankingViewModelFactory(rankingSelectorType.rankingPeriods.first())).get(RankingListViewModel::class.java)
                        }
                    } else {
                        for (rankingPeriod in rankingSelectorType.rankingPeriods) {
                            val selectorItem = SelectorItemView(requireContext())
                            selectorItem.rankingSelectorListener = this
                            selectorItem.setSelectorText(
                                when (rankingPeriod) {
                                    //TODO Add strings keys
                                    RankingPeriod.LEGACY -> "legacy"
                                    RankingPeriod.MONTHLY -> "monthly"
                                    RankingPeriod.ALL_TIME -> "all time"
                                    RankingPeriod.WEEKLY -> "weekly"
                                }
                            )

                            selectors_container.addView(selectorItem)
                            selectorItem.onClickSelector(rankingPeriod)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dk_fragment_leaderboard, container, false)
    }

    private fun setViewPager() {
        rankingsFragmentPagerAdapter =
            RankingsFragmentPagerAdapter(childFragmentManager)
        view_pager_leader_board.offscreenPageLimit = 4
        view_pager_leader_board.adapter = rankingsFragmentPagerAdapter

        for ((index, rankType) in DriverAchievementUI.rankingTypes.withIndex()) {
            tab_layout_leader_board.getTabAt(index)?.let {
                val tabIcon = when (rankType) {
                    RankingType.DISTRACTION -> R.drawable.dk_achievements_phone_distraction
                    RankingType.ECO_DRIVING -> R.drawable.dk_achievements_ecodriving
                    RankingType.SAFETY -> R.drawable.dk_achievements_safety
                }
                it.setIcon(tabIcon)
            }
        }

        view_pager_leader_board.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int) {
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
            tab_layout_leader_board.visibility = View.GONE
        }
    }

    override fun onClickSelector(rankingPeriod: RankingPeriod) {
        val currentFragment = rankingsFragmentPagerAdapter.currentFragment as RankingListFragment
        currentFragment.rankingViewModel.rankingPeriod = rankingPeriod
        currentFragment.updateRanking()
    }
}