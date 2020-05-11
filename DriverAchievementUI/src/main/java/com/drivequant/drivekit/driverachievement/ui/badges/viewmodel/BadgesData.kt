package com.drivequant.drivekit.driverachievement.ui.badges.viewmodel

import com.drivequant.drivekit.databaseutils.entity.BadgeCategory
import com.drivequant.drivekit.databaseutils.entity.BadgeLevel

/**
 * Created by Mohamed on 2020-05-05.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class BadgesData(
    private val themeKey: String,
    private val category: BadgeCategory,
    private val levels: List<BadgeLevel>
) {

    fun getBadgeTitle(): String = themeKey

    fun getProgress(): Double = levels.first().progressValue

    fun getThreshold(): Int = levels.first().threshold
}