package com.drivequant.drivekit.driverachievement.ui.rankings.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.rankings.viewholder.RankingListViewHolder
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingViewModel

class RankingListAdapter(
    val context: Context?,
    private val rankingViewModel: RankingViewModel) : RecyclerView.Adapter<RankingListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RankingListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_ranking_list_item, parent, false)
        FontUtils.overrideFonts(parent.context, view)
        return RankingListViewHolder(view)
    }

    override fun getItemCount(): Int = rankingViewModel.rankingDriversData.size

    override fun onBindViewHolder(parent: RankingListViewHolder, position: Int) {
        parent.bind(rankingViewModel.rankingDriversData[position])
    }
}