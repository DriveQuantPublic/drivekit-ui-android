package com.drivequant.drivekit.driverachievement.ui.leaderboard.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
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
        val button = LayoutInflater.from(context).inflate(R.layout.dk_selector_item, parent, false) as Button
        FontUtils.overrideFonts(parent.context, button)
        return ViewHolderSelector(button)
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
                holder.button.text = when (rankingPeriod) {
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

        holder.button.setBackgroundResource(R.drawable.button_selector)
        holder.button.setOnClickListener {
            isChecked = true
            holder.button.setBackgroundColor(DriveKitUI.colors.secondaryColor())
            holder.button.setTextColor(DriveKitUI.colors.fontColorOnSecondaryColor())
        }

        if(isChecked) {
            holder.button.setBackgroundColor(DriveKitUI.colors.secondaryColor())
            holder.button.setTextColor(DriveKitUI.colors.fontColorOnSecondaryColor())
        }
    }

    class ViewHolderSelector(val button: Button) : RecyclerView.ViewHolder(button)

    interface RankingSelectorListener {
        fun onClickSelector(rankingPeriod: RankingPeriod)
    }
}

