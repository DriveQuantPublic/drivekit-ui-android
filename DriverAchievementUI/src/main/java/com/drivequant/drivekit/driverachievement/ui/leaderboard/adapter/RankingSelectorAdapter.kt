package com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.leaderboard.RankingSelectorType

class RankingSelectorAdapter(
    val context: Context?,
    private val rankingSelectorListener: RankingSelectorListener
) : RecyclerView.Adapter<RankingSelectorAdapter.ViewHolderSelector>() {


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolderSelector {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_selector_item, parent, false)
        FontUtils.overrideFonts(parent.context, view)
        return ViewHolderSelector(view)
    }

    override fun getItemCount(): Int {
        return when (val rankingSelectorType = DriverAchievementUI.rankingSelector) {
            is RankingSelectorType.NONE -> 0
            is RankingSelectorType.PERIOD -> rankingSelectorType.rankingPeriods.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolderSelector, position: Int) {
        var isChecked = false
         when (val rankingSelectorType = DriverAchievementUI.rankingSelector) {
            is RankingSelectorType.NONE -> {} //TODO
            is RankingSelectorType.PERIOD -> {
                val rankingPeriod = rankingSelectorType.rankingPeriods[position]
                holder.textView.text = when (rankingPeriod) {
                    //TODO Add strings keys
                    RankingPeriod.LEGACY -> "legacy"
                    RankingPeriod.MONTHLY -> "monthly"
                    RankingPeriod.ALL_TIME -> "all time"
                    RankingPeriod.WEEKLY -> "weekly"
                }
                holder.itemView.setOnClickListener {
                    rankingSelectorListener.onClickSelector(rankingPeriod)
                }
            }
        }

        holder.textView.setBackgroundResource(R.drawable.dk_ranking_selector_rectangle)
        holder.textView.setOnClickListener {
            isChecked = true
            holder.textView.setBackgroundColor(DriveKitUI.colors.secondaryColor())
            holder.textView.setTextColor(DriveKitUI.colors.fontColorOnSecondaryColor())
        }

        if(isChecked) {
            holder.textView.setBackgroundColor(DriveKitUI.colors.secondaryColor())
            holder.textView.setTextColor(DriveKitUI.colors.fontColorOnSecondaryColor())
        }
    }

    class ViewHolderSelector(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view_selector)
    }

    interface RankingSelectorListener {
        fun onClickSelector(rankingPeriod: RankingPeriod)
    }
}

