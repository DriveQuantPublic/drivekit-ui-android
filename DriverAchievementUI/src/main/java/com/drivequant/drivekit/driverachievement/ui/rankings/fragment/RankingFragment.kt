package com.drivequant.drivekit.driverachievement.ui.rankings.fragment

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
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverachievement.RankingSyncStatus
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorListener
import com.drivequant.drivekit.driverachievement.ui.rankings.adapter.RankingListAdapter
import com.drivequant.drivekit.driverachievement.ui.rankings.commons.views.RankingSelectorView
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorData
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingViewModel
import kotlinx.android.synthetic.main.dk_fragment_ranking.*


class RankingFragment : Fragment(),
    RankingSelectorListener {

    lateinit var rankingViewModel: RankingViewModel
    private lateinit var rankingAdapter: RankingListAdapter
    private lateinit var selectedRankingSelectorView: RankingSelectorView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rankingViewModel = ViewModelProviders.of(this).get(RankingViewModel::class.java)
        recycler_view_ranking.layoutManager = LinearLayoutManager(requireContext())
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
            rankingSelectorView.configureRankingSelector(rankingSelector)
            val selected = selectedRankingSelector.index == rankingSelector.index
            rankingSelectorView.setRankingSelectorSelected(selected)
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
        savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.dk_fragment_ranking, container, false).setDKStyle()


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
        selectedRankingSelectorView.setRankingSelectorSelected(false)
        selectedRankingSelectorView = rankingSelectorView
        rankingSelectorView.setRankingSelectorSelected(true)
        rankingViewModel.selectedRankingSelectorData = rankingSelectorData
        updateRanking()
    }

    override fun onResume() {
        super.onResume()
        updateRanking()
    }

    fun updateRanking() {
        var isToastShowed = false
        rankingViewModel.mutableLiveDataRankingHeaderData.observe(this,
            Observer {
                if ((rankingViewModel.syncStatus == RankingSyncStatus.FAILED_TO_SYNC_RANKING_CACHE_ONLY || rankingViewModel.syncStatus == RankingSyncStatus.CACHE_DATA_ONLY)  && !isToastShowed) {
                    Toast.makeText(
                        context, context?.getString(R.string.dk_achievements_failed_to_sync_rankings),
                        Toast.LENGTH_SHORT
                    ).show()
                    isToastShowed = true
                }

                if (this::rankingAdapter.isInitialized) {
                    rankingAdapter.notifyDataSetChanged()
                } else {
                    rankingAdapter = RankingListAdapter(requireContext(), rankingViewModel)
                    recycler_view_ranking.adapter = rankingAdapter
                }
                ranking_header_view.setHeaderData(rankingViewModel)
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
        text_view_position_header.smallText(DriveKitUI.colors.complementaryFontColor())
        text_view_nickname_header.smallText(DriveKitUI.colors.complementaryFontColor())
        text_view_score_header.smallText(DriveKitUI.colors.complementaryFontColor())
        view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}