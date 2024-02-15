package com.drivequant.drivekit.driverachievement.ui.badges.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.databaseutils.entity.BadgeCharacteristics
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.badges.commons.views.BadgeItemView
import com.drivequant.drivekit.driverachievement.ui.badges.extension.*
import com.drivequant.drivekit.driverachievement.ui.badges.viewmodel.BadgesData


internal class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context
    private val title = itemView.findViewById<TextView>(R.id.text_view_badge_title)
    private val bronzeBadge = itemView.findViewById<BadgeItemView>(R.id.badge_bronze)
    private val silverBadge = itemView.findViewById<BadgeItemView>(R.id.badge_silver)
    private val goldBadge = itemView.findViewById<BadgeItemView>(R.id.badge_gold)

    fun bind(badgesData: BadgesData) {
        title.text = badgesData.getBadgeTitle(context)

        setBadgeConfiguration(badgesData.getBronzeBadgeCharacteristics(), bronzeBadge)
        setBadgeConfiguration(badgesData.getSilverBadgeCharacteristics(), silverBadge)
        setBadgeConfiguration(badgesData.getGoldBadgeCharacteristics(), goldBadge)
        setStyle()
    }

    private fun setBadgeConfiguration(badgeCharacteristics: BadgeCharacteristics, badgeItemView: BadgeItemView) {
        badgeCharacteristics.getIcon(context)?.let {
            badgeItemView.configureBadge(
                badgeCharacteristics.computePercent(),
                it,
                badgeCharacteristics.getColor(),
                badgeCharacteristics.getName(context)
            )
        }

        badgeItemView.setBadgeDescription(badgeCharacteristics.getDescription(context))
        badgeItemView.setBadgeTitle(badgeCharacteristics.getName(context))
        badgeItemView.setBadgeProgressCongrats(badgeCharacteristics.getProgressCongrats(context))
    }

    private fun setStyle() {
        title.headLine2()
    }
}