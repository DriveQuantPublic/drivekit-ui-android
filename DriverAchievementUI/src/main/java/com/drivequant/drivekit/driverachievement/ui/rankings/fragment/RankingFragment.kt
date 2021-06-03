package com.drivequant.drivekit.driverachievement.ui.rankings.fragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.PseudoAlertDialog
import com.drivequant.drivekit.common.ui.component.PseudoChangedListener
import com.drivequant.drivekit.common.ui.component.ranking.fragment.DKRankingFragment
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverachievement.RankingSyncStatus
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorListener
import com.drivequant.drivekit.driverachievement.ui.rankings.commons.views.RankingSelectorView
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorData
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingViewModel
import kotlinx.android.synthetic.main.dk_fragment_ranking.*


class RankingFragment : Fragment(), RankingSelectorListener {

    companion object {
        fun newInstance(groupName: String?) : RankingFragment {
            val fragment = RankingFragment()
            fragment.rankingGroupName = groupName
            return fragment
        }
    }

    var rankingGroupName: String? = null
    lateinit var rankingViewModel: RankingViewModel
    private lateinit var selectedRankingSelectorView: RankingSelectorView
    private lateinit var fragment: DKRankingFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_rankings"
            ), javaClass.simpleName
        )
        if (!this::rankingViewModel.isInitialized) {
            rankingViewModel = ViewModelProviders.of(this).get(RankingViewModel::class.java)
        }
        setTabLayout()
        if (rankingViewModel.rankingSelectorsData.size > 1) {
            createRankingSelectors()
        }

        fragment = DKRankingFragment()
        fragmentManager?.beginTransaction()?.replace(R.id.dk_ranking_container, fragment)?.commit()
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
        savedInstanceState: Bundle?
    ): View =
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

    override fun onClickSelector(
        rankingSelectorData: RankingSelectorData,
        rankingSelectorView: RankingSelectorView
    ) {
        selectedRankingSelectorView.setRankingSelectorSelected(false)
        selectedRankingSelectorView = rankingSelectorView
        rankingSelectorView.setRankingSelectorSelected(true)
        rankingViewModel.selectedRankingSelectorData = rankingSelectorData
        updateRanking()
    }

    override fun onResume() {
        super.onResume()
        /* TODO check pseudo in SharedPrefs
           if there is local pseudo {
            call updateRanking
            } else {
                fragment.updateProgressVisibility(true)

                synchronize (remote) DriveKit userInfo {
                    if success, call updateRanking
                    else call PseudoAlertDialog().show()
                }
           }
         */
        PseudoAlertDialog().show(requireContext(), object : PseudoChangedListener {
            override fun onPseudoChanged(success: Boolean) {
            
            }

            override fun onCancelled() {
                activity?.finish()
            }
        })

        // remove that call once internal modules with get user info available
        updateRanking()
    }

    override fun onStop() {
        super.onStop()
        rankingViewModel.useCache.clear()
    }

    fun updateRanking() {
        var isToastShowed = false
        rankingViewModel.mutableLiveDataRankingData.observe(this,
            Observer {
                if (rankingViewModel.syncStatus == RankingSyncStatus.FAILED_TO_SYNC_RANKING_CACHE_ONLY && !isToastShowed) {
                    Toast.makeText(
                        context,
                        context?.getString(R.string.dk_achievements_failed_to_sync_rankings),
                        Toast.LENGTH_SHORT
                    ).show()
                    isToastShowed = true
                }

                fragment.configureDKDriverRanking(rankingViewModel.rankingData)
                fragment.updateProgressVisibility(false)
            })

        fragment.updateProgressVisibility(true)
        rankingViewModel.fetchRankingList(rankingGroupName)
    }
}