package com.drivequant.drivekit.common.ui.component.ranking.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRankingItem
import com.drivequant.drivekit.common.ui.component.ranking.viewholder.RankingListViewHolder
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DKRankingViewModel
import com.drivequant.drivekit.common.ui.utils.FontUtils

class RankingListAdapter(
    val context: Context?,
    private val rankingViewModel: DKRankingViewModel
) : RecyclerView.Adapter<RankingListViewHolder>() {

    private var driverRankingList: List<DKDriverRankingItem> = getDriverRankingList()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RankingListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_ranking_list_item, parent, false)
        FontUtils.overrideFonts(parent.context, view)
        return RankingListViewHolder(view)
    }

    override fun getItemCount(): Int = this.driverRankingList.size

    override fun onBindViewHolder(parent: RankingListViewHolder, position: Int) {
        parent.bind(this.driverRankingList[position])
    }

    fun update() {
        this.driverRankingList = getDriverRankingList()
        notifyDataSetChanged()
    }

    private fun getDriverRankingList(): List<DKDriverRankingItem> = rankingViewModel.getDriverRankingList()
}
