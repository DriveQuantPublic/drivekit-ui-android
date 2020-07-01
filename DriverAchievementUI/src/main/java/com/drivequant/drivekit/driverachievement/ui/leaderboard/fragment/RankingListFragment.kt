package com.drivequant.drivekit.driverachievement.ui.leaderboard.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.RankingSyncStatus
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter.RankingListAdapter
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.DriverInfoViewModel
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingListViewModel
import kotlinx.android.synthetic.main.dk_fragment_ranking_list.*
import kotlinx.android.synthetic.main.dk_fragment_streaks_list.progress_circular

class RankingListFragment : Fragment() {

    private lateinit var rankingViewModel: RankingListViewModel
    lateinit var rankingAdapter: RankingListAdapter
    lateinit var rankingType: RankingType
    lateinit var driverInfoViewModel: DriverInfoViewModel

    companion object {
        fun newInstance(
            rankingType: RankingType
        ): RankingListFragment {
            Log.e("TAG_RANKING_LIST", "$rankingType -- newInstance")
            val fragment = RankingListFragment()
            fragment.rankingType = rankingType
            return fragment
        }
    }

    override fun onPause() {
        Log.e("TAG_RANKING_LIST", "$rankingType -- onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.e("TAG_RANKING_LIST", "$rankingType -- onStop")
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG_RANKING_LIST", "$rankingType -- onDestroy")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.e("TAG_RANKING_LIST", "$rankingType -- onActivityCreated")
        super.onActivityCreated(savedInstanceState)
        driverInfoViewModel = ViewModelProviders.of(this).get(DriverInfoViewModel::class.java)
        rankingViewModel = ViewModelProviders.of(this).get(RankingListViewModel::class.java)

    }

    override fun onDetach() {
        Log.e("TAG_RANKING_LIST", "$rankingType -- onDetach")

        super.onDetach()
    }

    override fun onAttach(context: Context?) {
        Log.e("TAG_RANKING_LIST", "$rankingType -- onAttach")
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("TAG_RANKING_LIST", "$rankingType -- onCreateView")
        return inflater.inflate(R.layout.dk_fragment_ranking_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e("TAG_RANKING_LIST", "$rankingType -- onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        recycler_view_ranking.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onResume() {
        Log.e("TAG_RANKING_LIST", "$rankingType -- onResume")
        super.onResume()
        updateRanking()
    }

    fun updateRanking(rankingPeriod: RankingPeriod = RankingPeriod.LEGACY) {
        Log.e("TAG_RANKING_LIST", "$rankingType -- updateRanking")
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