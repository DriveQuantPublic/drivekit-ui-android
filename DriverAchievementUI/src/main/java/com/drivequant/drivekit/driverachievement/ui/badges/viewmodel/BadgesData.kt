package com.drivequant.drivekit.driverachievement.ui.badges.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.BadgeCharacteristics

/**
 * Created by Mohamed on 2020-05-05.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class BadgesData(
    private val theme: String,
    private val badgesCharacteristics: List<BadgeCharacteristics>
) {

    fun getBadgeTitle(context: Context): String = DKResource.convertToString(context, theme)

    fun getBronzeBadgeCharacteristics(): BadgeCharacteristics = badgesCharacteristics[0]

    fun getSilverBadgeCharacteristics(): BadgeCharacteristics = badgesCharacteristics[1]

    fun getGoldBadgeCharacteristics(): BadgeCharacteristics = badgesCharacteristics[2]
}

