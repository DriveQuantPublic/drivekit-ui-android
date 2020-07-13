package com.drivequant.drivekit.driverachievement.ui.leaderboard.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverachievement.RankingSyncStatus
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.RankingSelectorListener
import com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter.RankingListAdapter
import com.drivequant.drivekit.driverachievement.ui.leaderboard.commons.views.RankingSelectorView
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingSelectorData
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingViewModel
import kotlinx.android.synthetic.main.dk_fragment_ranking.*


class RankingFragment : Fragment(), RankingSelectorListener {

    lateinit var rankingViewModel: RankingViewModel
    private lateinit var rankingAdapter: RankingListAdapter
    private lateinit var selectedRankingSelectorView: RankingSelectorView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rankingViewModel = ViewModelProviders.of(this).get(RankingViewModel::class.java)
        recycler_view_ranking.layoutManager = LinearLayoutManager(requireContext())
        super.onViewCreated(view, savedInstanceState)
        setTabLayout()
        if (rankingViewModel.rankingSelectorsData.size > 1) {
            createRankingSelectors()
        }
        setStyle()
    }

    private fun createRankingSelectors() {
        val selectedRankingSelector = rankingViewModel.selectedRankingSelectorData
        for (rankingSelector in rankingViewModel.rankingSelectorsData) {
            val rankingSelectorView = RankingSelectorView(requireContext())
            rankingSelectorView.configureRankingSelectorButton(rankingSelector)
            val selected = selectedRankingSelector.index == rankingSelector.index
            rankingSelectorView.setButtonSelected(selected)
            if (selected) {
                selectedRankingSelectorView = rankingSelectorView
            }
            rankingSelectorView.rankingSelectorListener = this
            selectors_container.addView(rankingSelectorView)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.dk_fragment_ranking, container, false)
    }

    private fun setTabLayout() {
        for (rankingTypeData in rankingViewModel.rankingTypesData) {
            val tab = tab_layout_leader_board.newTab()
            val icon = DKResource.convertToDrawable(requireContext(), rankingTypeData.iconId)
            icon?.let {
                tab.setIcon(it)
            }
            tab_layout_leader_board.addTab(tab)
        }

        for (i in 0 until tab_layout_leader_board.tabCount) {
            val tab = tab_layout_leader_board.getTabAt(i)
            tab?.setCustomView(R.layout.dk_ranking_view_tab)
        }

        if (rankingViewModel.rankingTypesData.size < 2) {
            tab_layout_leader_board.visibility = View.GONE
        }

        tab_layout_leader_board.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                rankingViewModel.selectedRankingTypeData.rankingType =
                    DriverAchievementUI.rankingTypes[tab_layout_leader_board.selectedTabPosition]
                updateRanking()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
        })
    }

    override fun onClickSelector(rankingSelectorData: RankingSelectorData, rankingSelectorView: RankingSelectorView) {
        selectedRankingSelectorView.setButtonSelected(false)
        selectedRankingSelectorView = rankingSelectorView
        rankingSelectorView.setButtonSelected(true)
        rankingViewModel.selectedRankingSelectorData = rankingSelectorData
        updateRanking()
    }

    private fun setupLeaderBoardHeader() {
        driver_progression_view.setDriverProgression(
            rankingViewModel.rankingHeaderData.getLeaderBoardStatus(requireContext()),
            rankingViewModel.rankingHeaderData.getStatus(rankingViewModel.previousRank),
            rankingViewModel.rankingHeaderData.getLeaderBoardTitle(),
            rankingViewModel.rankingHeaderData.getIcon()
        )
    }

    override fun onResume() {
        super.onResume()
        updateRanking()
    }

    fun updateRanking() {
        rankingViewModel.mutableLiveDataRankingHeaderData.observe(this,
            Observer {
                if (rankingViewModel.syncStatus != RankingSyncStatus.NO_ERROR && rankingViewModel.syncStatus != RankingSyncStatus.USER_NOT_RANKED) {
                    Toast.makeText(
                        context, context?.getString(R.string.dk_achievements_failed_to_sync_rankings),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (this::rankingAdapter.isInitialized) {
                    rankingAdapter.notifyDataSetChanged()
                } else {
                    rankingAdapter = RankingListAdapter(requireContext(), rankingViewModel)
                    recycler_view_ranking.adapter = rankingAdapter
                }
                setupLeaderBoardHeader()
                updateProgressVisibility(false)
            })

        updateProgressVisibility(true)
        rankingViewModel.fetchRankingList()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
    }

    private fun setStyle() {
        text_view_position_header.normalText(DriveKitUI.colors.complementaryFontColor())
        text_view_nickname_header.normalText(DriveKitUI.colors.complementaryFontColor())
        tex_view_score_header.normalText(DriveKitUI.colors.complementaryFontColor())
        view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}