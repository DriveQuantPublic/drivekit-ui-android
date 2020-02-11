package com.drivequant.drivekit.driverachievement.ui.streaks.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.StreaksViewConfig
import com.drivequant.drivekit.driverachievement.ui.streaks.viewholder.StreakViewHolder
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksViewModel

class StreakAdapter(
    var context: Context?,
    var streaksViewModel: StreaksViewModel,
    var streaksViewConfig: StreaksViewConfig
) : RecyclerView.Adapter<StreakViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): StreakViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.streaks_list_item, parent, false)
        return StreakViewHolder(view, streaksViewConfig)
    }

    override fun getItemCount(): Int {
        return streaksViewModel.sortedStreaks.size
    }

    override fun onBindViewHolder(parent: StreakViewHolder, position: Int) {
        val streakData = streaksViewModel.sortedStreaks[position]
        parent.bind(streakData)
    }
}