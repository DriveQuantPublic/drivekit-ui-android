package com.drivequant.drivekit.driverachievement.ui.badges.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.badges.viewholder.BadgeViewHolder
import com.drivequant.drivekit.driverachievement.ui.badges.viewmodel.BadgesListViewModel

/**
 * Created by Mohamed on 2020-05-05.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class BadgesListAdapter(
    var context: Context?,
    var badgesListViewModel: BadgesListViewModel
) : RecyclerView.Adapter<BadgeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BadgeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.badges_list_item, parent, false)
        FontUtils.overrideFonts(parent.context,view)
        return BadgeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return badgesListViewModel.filteredBadgesData.size
    }

    override fun onBindViewHolder(parent: BadgeViewHolder, position: Int) {
        val badgesData = badgesListViewModel.filteredBadgesData[position]
        parent.bind(badgesData)
    }
}