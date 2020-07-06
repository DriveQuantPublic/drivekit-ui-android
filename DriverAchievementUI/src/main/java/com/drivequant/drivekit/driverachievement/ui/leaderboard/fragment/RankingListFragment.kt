package com.drivequant.drivekit.driverachievement.ui.leaderboard.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.RankingSyncStatus
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter.RankingListAdapter
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingListViewModel
import kotlinx.android.synthetic.main.dk_fragment_ranking_list.*
import kotlinx.android.synthetic.main.dk_fragment_streaks_list.progress_circular

class RankingListFragment : Fragment() {

    private lateinit var rankingViewModel: RankingListViewModel
    lateinit var rankingAdapter: RankingListAdapter
    lateinit var rankingType: RankingType

    companion object {
        fun newInstance(
            rankingType: RankingType
        ): RankingListFragment {
            val fragment = RankingListFragment()
            fragment.rankingType = rankingType
            return fragment
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rankingViewModel = ViewModelProviders.of(this).get(RankingListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dk_fragment_ranking_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view_ranking.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onResume() {
        super.onResume()
        updateRanking()
    }

    private fun setupLeaderBoardHeader() {
        if (DriverAchievementUI.rankingTypes.size != 1) {
            image_view_ranking_type.visibility = View.GONE
            text_view_ranking_title.visibility = View.GONE
        }

        ranking_status.setRankingStatus(
            rankingViewModel.rankingListData.getLeaderBoardStatus(requireContext()),
            rankingViewModel.rankingListData.getStatus()
        )
        text_view_ranking_title.text =
            rankingViewModel.rankingListData.getLeaderBoardTitle()
        image_view_ranking_type.setImageDrawable(
            DKResource.convertToDrawable(
                requireContext(),
                rankingViewModel.rankingListData.getIcon()
            )
        )
    }

    fun updateRanking(rankingPeriod: RankingPeriod = RankingPeriod.LEGACY) {
        rankingViewModel.mutableLiveDataRankingListData.observe(this,
            Observer {
                //TODO check userNotRanked and handle UI with Popup
                if (rankingViewModel.syncStatus != RankingSyncStatus.NO_ERROR && rankingViewModel.syncStatus != RankingSyncStatus.USER_NOT_RANKED) {
                    Toast.makeText(
                        context,
                        //TODO Add rankings keys strings
                        context?.getString(R.string.dk_achievements_failed_to_sync_rankings),
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
        rankingViewModel.fetchRankingList(rankingType, rankingPeriod)
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
    }
}