package com.drivequant.drivekit.driverachievement.ui.badges.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeImage
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.badges.viewmodel.BadgesData

/**
 * Created by Mohamed on 2020-05-05.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class BadgeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context
    private val title = itemView.findViewById<TextView>(R.id.text_view_badge_title)
    private val bronzeBadge = itemView.findViewById<GaugeImage>(R.id.badge_bronze)
    private val silverBadge = itemView.findViewById<GaugeImage>(R.id.badge_silver)
    private val goldBadge = itemView.findViewById<GaugeImage>(R.id.badge_gold)

    private val bronzeBadgeName = itemView.findViewById<TextView>(R.id.text_view_bronze_badge_name)
    private val silverBadgeName = itemView.findViewById<TextView>(R.id.text_view_silver_badge_name)
    private val goldBadgeName = itemView.findViewById<TextView>(R.id.text_view_gold_badge_name)

    fun bind(badgesData: BadgesData) {
        title.text = badgesData.getBadgeTitle(context)

        badgesData.getIcon(context).first?.let {
            bronzeBadge.configure(
                badgesData.getPercent().first,
                it,
                badgesData.getBadgeColor(
                    badgesData.getBadgeLevel1().level,
                    badgesData.isBadgeAcquired().first
                )
            )
            bronzeBadgeName.text = badgesData.getBadgeName(context).first
        }

        badgesData.getIcon(context).second?.let {
            silverBadge.configure(
                badgesData.getPercent().second,
                it,
                badgesData.getBadgeColor(
                    badgesData.getBadgeLevel2().level,
                    badgesData.isBadgeAcquired().second
                )
            )
            silverBadgeName.text = badgesData.getBadgeName(context).second
        }


        badgesData.getIcon(context).third?.let {
            goldBadge.configure(
                badgesData.getPercent().third,
                it,
                badgesData.getBadgeColor(
                    badgesData.getBadgeLevel3().level,
                    badgesData.isBadgeAcquired().third
                )
            )
            goldBadgeName.text = badgesData.getBadgeName(context).third
        }

        setStyle()
    }

    private fun setStyle() {
        title.headLine2()
        title.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}