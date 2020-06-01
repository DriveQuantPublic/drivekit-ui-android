package com.drivequant.drivekit.driverachievement.ui.badges.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.BadgeLevel

/**
 * Created by Mohamed on 2020-05-05.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class BadgesData(
    private val themeKey: String,
    private val levels: List<BadgeLevel>
) {

    fun getBadgeTitle(context: Context): String = DKResource.convertToString(context, themeKey)

    fun getBronzeBadgeLevel(): BadgeLevel = levels[0]

    fun getSilverBadgeLevel(): BadgeLevel = levels[1]

    fun getGoldBadgeLevel(): BadgeLevel = levels[2]
}

