package com.drivequant.drivekit.driverachievement.ui.leaderboard.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.drivequant.drivekit.databaseutils.entity.DriverRanked
import com.drivequant.drivekit.driverachievement.ui.R

class RankingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val driverDistance = itemView.findViewById<TextView>(R.id.driver_distance)
    private val driverNickname = itemView.findViewById<TextView>(R.id.driver_nickname)
    private val driverPosition = itemView.findViewById<TextView>(R.id.driver_position)
    private val driverScore = itemView.findViewById<TextView>(R.id.driver_score)

    fun bind(driverRanked: DriverRanked) {
           driverDistance.text = driverRanked.distance.toString()
           driverScore.text = driverRanked.score.toString()
           driverNickname.text = driverRanked.nickname
           driverPosition.text = driverRanked.rank.toString()
    }
}