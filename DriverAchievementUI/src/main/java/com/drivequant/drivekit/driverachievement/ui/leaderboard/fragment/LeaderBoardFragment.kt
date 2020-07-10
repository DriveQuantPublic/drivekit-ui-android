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
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingSelector
import kotlinx.android.synthetic.main.dk_fragment_leaderboard.*

class LeaderBoardFragment : Fragment(), RankingSelectorListener {

    lateinit var rankingViewModel: RankingListViewModel
    lateinit var rankingsFragmentPagerAdapter: RankingsFragmentPagerAdapter
    val rankingSelectors = mutableListOf<RankingSelector>()
    var selectedRankingSelector:RankingSelector

    init {
        when (val rankingSelectorType = DriverAchievementUI.rankingSelector) {
            is RankingSelectorType.NONE -> {
                selectors_container.visibility = View.GONE
            }
            is RankingSelectorType.PERIOD -> {
                for ((index, rankingPeriod) in rankingSelectorType.rankingPeriods.withIndex()) {
                    val nbDays = when (rankingPeriod) {
                        RankingPeriod.WEEKLY -> 7
                        RankingPeriod.LEGACY -> 14
                        RankingPeriod.MONTHLY -> 30
                        RankingPeriod.ALL_TIME -> 0
                    }
                    rankingSelectors.add(RankingSelector( index,"$nbDays",rankingPeriod))
                }
            }
        }
        selectedRankingSelector = rankingSelectors.first()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rankingViewModel = ViewModelProviders.of(this).get(RankingListViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)
        setTabLayout()
        setViewPager()
        createRankingSelectors()
    }

    private fun createRankingSelectors() {
        for (rankingSelector in rankingSelectors) {
            val selectorItem = SelectorItemView(requireContext())
            selectorItem.setSelectorText(rankingSelector)
            val selected = selectedRankingSelector.index == rankingSelector.index
            selectorItem.setButtonSelected(selected)
            selectorItem.rankingSelectorListener = this
            selectorItem.onRankingSelectorButtonSelected(rankingSelector.rankingPeriod)
            selectors_container.addView(selectorItem)
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
            tab_layout_leader_board.visibility = View.GONE
        }
    }

    override fun onClickSelector(rankingPeriod: RankingPeriod) {
        val currentFragment = rankingsFragmentPagerAdapter.currentFragment as RankingListFragment
        currentFragment.rankingViewModel.rankingPeriod = rankingPeriod
        currentFragment.updateRanking()
    }
}