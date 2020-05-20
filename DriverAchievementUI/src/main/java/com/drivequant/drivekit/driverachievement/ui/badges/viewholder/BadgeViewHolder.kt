package com.drivequant.drivekit.driverachievement.ui.badges.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.databaseutils.entity.BadgeLevel
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.badges.commons.views.BadgeItemView
import com.drivequant.drivekit.driverachievement.ui.badges.extension.*
import com.drivequant.drivekit.driverachievement.ui.badges.viewmodel.BadgesData

/**
 * Created by Mohamed on 2020-05-05.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context
    private val title = itemView.findViewById<TextView>(R.id.text_view_badge_title)
    private val bronzeBadge = itemView.findViewById<BadgeItemView>(R.id.badge_bronze)
    private val silverBadge = itemView.findViewById<BadgeItemView>(R.id.badge_silver)
    private val goldBadge = itemView.findViewById<BadgeItemView>(R.id.badge_gold)

    fun bind(badgesData: BadgesData) {
        title.text = badgesData.getBadgeTitle(context)

        setBadgeConfiguration(badgesData.getBronzeBadgeLevel(), bronzeBadge)
        setBadgeConfiguration(badgesData.getSilverBadgeLevel(), silverBadge)
        setBadgeConfiguration(badgesData.getGoldBadgeLevel(), goldBadge)
        setStyle()
    }

    private fun setBadgeConfiguration(badgeLevel: BadgeLevel, badgeItemView: BadgeItemView) {
        badgeLevel.getIcon(context)?.let {
            badgeItemView.configureBadge(
                badgeLevel.computePercent(),
                it,
                badgeLevel.getColor(),
                badgeLevel.getName(context)
            )
        }

        badgeItemView.setBadgeDescription(badgeLevel.getDescription(context))
        badgeItemView.setBadgeTitle(badgeLevel.getName(context))
        badgeItemView.setBadgeProgressCongrats(badgeLevel.getProgressCongrats(context))
    }

    private fun setStyle() {
        title.headLine2()
        title.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }
}