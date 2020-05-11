package com.drivequant.drivekit.driverachievement.ui.badges.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeImage
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.badges.viewmodel.BadgesData

/**
 * Created by Mohamed on 2020-05-05.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class BadgeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context
    private val title = itemView.findViewById<TextView>(R.id.text_view_badge_title)
    private val badgeBronze = itemView.findViewById<GaugeImage>(R.id.badge_bronze)
    private val badgeSilver = itemView.findViewById<GaugeImage>(R.id.badge_silver)
    private val badgeGold = itemView.findViewById<GaugeImage>(R.id.badge_gold)

    fun bind(badgesData: BadgesData) {
        title.text = badgesData.getBadgeTitle()
        //
        DKResource.convertToDrawable(context, "trip_number_0")?.let {
            badgeBronze.configure(computePercent(badgesData.getProgress(),badgesData.getThreshold()), it, R.color.dkGood)
        }

        DKResource.convertToDrawable(context, "trip_number_0")?.let {
            badgeSilver.configure(8.04 , it, R.color.dkGood)
        }

        DKResource.convertToDrawable(context, "trip_number_0")?.let {
            badgeGold.configure(5.0, it, R.color.dkGood)
        }

        setStyle()
    }

    private fun computePercent(progress: Double, threshold:Int): Double = (progress / threshold) * 10


    private fun setStyle() {
        title.headLine2()
        title.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}