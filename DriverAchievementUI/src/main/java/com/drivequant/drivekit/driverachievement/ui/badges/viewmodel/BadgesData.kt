package com.drivequant.drivekit.driverachievement.ui.badges.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.BadgeCharacteristics


internal class BadgesData(
    private val theme: String,
    private val badgesCharacteristics: List<BadgeCharacteristics>) {

    fun getBadgeTitle(context: Context): String = DKResource.convertToString(context, theme)

    fun getBronzeBadgeCharacteristics() = badgesCharacteristics[0]

    fun getSilverBadgeCharacteristics() = badgesCharacteristics[1]

    fun getGoldBadgeCharacteristics() = badgesCharacteristics[2]
}

