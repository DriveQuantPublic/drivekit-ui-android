package com.drivequant.drivekit.driverachievement.ui.leaderboard.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.RankingSyncStatus
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
        createRankingSelectors()
        setTabLayout()
        setViewPager()
        setLeaderBoardHeader()
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
        Log.e("TAG_RANKING_LIST", " -- setViewPager")
        rankingsFragmentPagerAdapter = RankingsFragmentPagerAdapter(childFragmentManager)
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

    private fun setLeaderBoardHeader() {
        rankingViewModel.mutableLiveDataLeaderBoardData.observe(
            this, Observer {
                if (rankingViewModel.syncStatus == RankingSyncStatus.NO_ERROR || rankingViewModel.syncStatus == RankingSyncStatus.USER_NOT_RANKED) {

                    Log.e("TAG_RANKING_LIST", " -- setLeaderBoardHeader")
                    ranking_status.setRankingStatus(
                        it!!.getLeaderBoardStatus(requireContext()),
                        it.getStatus()
                    )
                    text_view_ranking_title.text =
                        it.getLeaderBoardTitle()
                    image_view_ranking_type.setImageDrawable(
                        DKResource.convertToDrawable(
                            requireContext(),
                            it.getIcon()
                        )
                    )
                }
                updateProgressVisibility(false)
            })
        updateProgressVisibility(true)
        rankingViewModel.fetchLeaderBoardStatus(DriverAchievementUI.rankingTypes[0], RankingPeriod.LEGACY)
    }

    private fun setTabLayout() {
        Log.e("TAG_RANKING_LIST", " -- setTabLayout")
        tab_layout_leader_board.setupWithViewPager(view_pager_leader_board)
        if (DriverAchievementUI.rankingTypes.size == 1) {
            tab_layout_leader_board.removeAllTabs()
        } else {
            image_view_ranking_type.visibility = View.GONE
            text_view_ranking_title.visibility = View.GONE
        }
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
    }

    override fun onClickSelector(rankingPeriod: RankingPeriod) {
        val currentFragment = rankingsFragmentPagerAdapter.currentFragment as RankingListFragment
        currentFragment.updateRanking(rankingPeriod)
    }
}