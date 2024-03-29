package com.drivequant.drivekit.driverachievement.ui.streaks.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.streaks.viewholder.StreakViewHolder
import com.drivequant.drivekit.driverachievement.ui.streaks.viewmodel.StreaksListViewModel

internal class StreaksListAdapter(
    private var context: Context?,
    private var streaksListViewModel: StreaksListViewModel) : RecyclerView.Adapter<StreakViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): StreakViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_streaks_list_item, parent, false)
        FontUtils.overrideFonts(parent.context,view)
        return StreakViewHolder(view)
    }

    override fun getItemCount(): Int {
        return streaksListViewModel.filteredStreaksData.size
    }

    override fun onBindViewHolder(parent: StreakViewHolder, position: Int) {
        val streakData = streaksListViewModel.filteredStreaksData[position]
        parent.bind(streakData)
    }
}