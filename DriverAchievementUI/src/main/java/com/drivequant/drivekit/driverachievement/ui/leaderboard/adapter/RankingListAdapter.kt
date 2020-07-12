package com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewholder.RankingListViewHolder
import com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel.RankingViewModel

class RankingListAdapter(
    val context: Context?,
    private val rankingViewModel: RankingViewModel) : RecyclerView.Adapter<RankingListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RankingListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_ranking_list_item, parent, false)
        FontUtils.overrideFonts(parent.context, view)
        return RankingListViewHolder(view)
    }

    override fun getItemCount(): Int = rankingViewModel.rankingListData.size

    override fun onBindViewHolder(parent: RankingListViewHolder, position: Int) {
        parent.bind(rankingViewModel.rankingListData[position])
    }
}