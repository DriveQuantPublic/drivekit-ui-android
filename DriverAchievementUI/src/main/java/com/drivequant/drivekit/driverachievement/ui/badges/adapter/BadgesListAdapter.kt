package com.drivequant.drivekit.driverachievement.ui.badges.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.badges.viewholder.BadgeViewHolder
import com.drivequant.drivekit.driverachievement.ui.badges.viewmodel.BadgesListViewModel


internal class BadgesListAdapter(
    var context: Context?,
    private var badgesListViewModel: BadgesListViewModel
) : RecyclerView.Adapter<BadgeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_badges_list_item, parent, false)
        FontUtils.overrideFonts(parent.context, view)
        return BadgeViewHolder(view)
    }

    override fun getItemCount() = badgesListViewModel.filteredBadgesData.size

    override fun onBindViewHolder(parent: BadgeViewHolder, position: Int) {
        val badgesData = badgesListViewModel.filteredBadgesData[position]
        parent.bind(badgesData)
    }
}
