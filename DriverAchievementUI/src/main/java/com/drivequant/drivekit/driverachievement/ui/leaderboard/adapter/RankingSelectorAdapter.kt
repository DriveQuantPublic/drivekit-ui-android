package com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.R

class RankingSelectorAdapter(
    val context: Context?,
    private val rankingPeriods: List<RankingPeriod>,
    private val rankingSelectorListener: RankingSelectorListener
) : RecyclerView.Adapter<RankingSelectorAdapter.ViewHolderSelector>() {


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolderSelector {
        val button = LayoutInflater.from(context).inflate(R.layout.dk_selector_item, parent, false) as Button
        FontUtils.overrideFonts(parent.context, button)
        return ViewHolderSelector(button)
    }

    override fun getItemCount(): Int = rankingPeriods.size

    override fun onBindViewHolder(holder: ViewHolderSelector, position: Int) {
        val rankingPeriod = rankingPeriods[position]
        holder.button.setBackgroundResource(R.drawable.button_selector)
        holder.button.text = when (rankingPeriod) {
            RankingPeriod.LEGACY -> "legacy"
            RankingPeriod.MONTHLY ->"monthly"
            RankingPeriod.ALL_TIME -> "all time"
            RankingPeriod.WEEKLY -> "weekly"
        }
        holder.itemView.setOnClickListener {
            rankingSelectorListener.onClickSelector(rankingPeriod)
        }
    }

    class ViewHolderSelector(val button: Button) : RecyclerView.ViewHolder(button)

    interface RankingSelectorListener {
        fun onClickSelector(rankingPeriod: RankingPeriod)
    }
}

