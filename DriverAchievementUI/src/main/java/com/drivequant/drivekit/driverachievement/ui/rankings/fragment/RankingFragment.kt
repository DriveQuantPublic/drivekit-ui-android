package com.drivequant.drivekit.driverachievement.ui.rankings.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.PseudoChangeListener
import com.drivequant.drivekit.common.ui.component.PseudoCheckListener
import com.drivequant.drivekit.common.ui.component.PseudoUtils
import com.drivequant.drivekit.common.ui.component.ranking.views.DKRankingView
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverachievement.RankingSyncStatus
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.rankings.commons.views.RankingSelectorView
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorData
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorListener
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingViewModel
import com.google.android.material.tabs.TabLayout

class RankingFragment : Fragment(), RankingSelectorListener {

    companion object {
        fun newInstance(groupName: String?): RankingFragment {
            val fragment = RankingFragment()
            fragment.rankingGroupName = groupName
            return fragment
        }
    }

    var rankingGroupName: String? = null
    lateinit var rankingViewModel: RankingViewModel
    private lateinit var selectorsContainer: ViewGroup
    private lateinit var tabLayoutLeaderBoard: TabLayout
    private lateinit var rankingContainer: ViewGroup
    private lateinit var activityIndicator: ProgressBar
    private lateinit var selectedRankingSelectorView: RankingSelectorView
    private lateinit var rankingView: DKRankingView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_rankings"
            ), javaClass.simpleName
        )

        this.selectorsContainer = view.findViewById(R.id.selectors_container)
        this.tabLayoutLeaderBoard = view.findViewById(R.id.tab_layout_leader_board)
        this.rankingContainer = view.findViewById(R.id.dk_ranking_container)
        this.activityIndicator = view.findViewById(R.id.progress_circular)

        if (!this::rankingViewModel.isInitialized) {
            rankingViewModel = ViewModelProvider(this)[RankingViewModel::class.java]
        }
        setTabLayout()
        if (rankingViewModel.rankingSelectorsData.size > 1) {
            createRankingSelectors()
            this.selectorsContainer.apply {
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dkRankingBackgroundColor))
                visibility = View.VISIBLE
            }
        }
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
            this.selectorsContainer.addView(rankingSelectorView)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dk_fragment_ranking, container, false)
        view.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dkRankingBackgroundColor
            )
        )
        return view
    }

    private fun setTabLayout() {
        for (rankingTypeData in rankingViewModel.rankingTypesData) {
            val tab = this.tabLayoutLeaderBoard.newTab()
            val icon = DKResource.convertToDrawable(requireContext(), rankingTypeData.iconId)
            icon?.let {
                tab.setIcon(it)
            }
            this.tabLayoutLeaderBoard.addTab(tab)
        }

        for (i in 0 until this.tabLayoutLeaderBoard.tabCount) {
            val tab = this.tabLayoutLeaderBoard.getTabAt(i)
            tab?.setCustomView(R.layout.dk_icon_view_tab)
        }

        if (rankingViewModel.rankingTypesData.size < 2) {
            this.tabLayoutLeaderBoard.visibility = View.GONE
        }
        this.tabLayoutLeaderBoard.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dkRankingBackgroundColor
                )
            )
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    rankingViewModel.selectedRankingTypeData.rankingType =
                        DriverAchievementUI.rankingTypes[tabLayoutLeaderBoard.selectedTabPosition]
                    updateRanking(requireContext())
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    override fun onClickSelector(
        rankingSelectorData: RankingSelectorData,
        rankingSelectorView: RankingSelectorView
    ) {
        selectedRankingSelectorView.setRankingSelectorSelected(false)
        selectedRankingSelectorView = rankingSelectorView
        rankingSelectorView.setRankingSelectorSelected(true)
        rankingViewModel.selectedRankingSelectorData = rankingSelectorData
        updateRanking(requireContext())
    }

    override fun onResume() {
        super.onResume()
        checkPseudo()
    }

    override fun onStop() {
        super.onStop()
        rankingViewModel.useCache.clear()
    }

    private fun checkPseudo() {
        PseudoUtils.checkPseudo(object : PseudoCheckListener {
            override fun onPseudoChecked(hasPseudo: Boolean) {
                context?.let {
                    if (hasPseudo) {
                        updateRanking(it)
                    } else {
                        PseudoUtils.show(it, object : PseudoChangeListener {
                            override fun onPseudoChanged(success: Boolean) {
                                if (!success) {
                                    Toast.makeText(it, DKResource.convertToString(it, "dk_common_error_message"), Toast.LENGTH_LONG).show()
                                }
                                updateRanking(it)
                            }
                            override fun onCancelled() {
                                updateRanking(it)
                            }
                        })
                    }
                }
            }
        })
    }

    fun updateRanking(context: Context) {
        var isToastShowed = false
        rankingViewModel.mutableLiveDataRankingData.observe(this, Observer {
            if (rankingViewModel.syncStatus == RankingSyncStatus.FAILED_TO_SYNC_RANKING_CACHE_ONLY && !isToastShowed) {
                Toast.makeText(
                    context,
                    context.getString(R.string.dk_achievements_failed_to_sync_rankings),
                    Toast.LENGTH_SHORT
                ).show()
                isToastShowed = true
            }
            rankingView = DKRankingView(context)
            rankingView.apply {
                configure(rankingViewModel.rankingData)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }
            rankingContainer.removeAllViews()
            rankingContainer.addView(rankingView)
            updateProgressVisibility(false)
        })

        updateProgressVisibility(true)
        rankingViewModel.fetchRankingList(rankingGroupName)
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        this.activityIndicator.visibility = if (displayProgress) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}
