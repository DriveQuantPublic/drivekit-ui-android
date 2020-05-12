package com.drivequant.drivekit.driverachievement.ui.badges.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.BadgeCategory
import com.drivequant.drivekit.databaseutils.entity.BadgeLevel
import com.drivequant.drivekit.databaseutils.entity.DKLevel
import com.drivequant.drivekit.driverachievement.ui.R

/**
 * Created by Mohamed on 2020-05-05.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class BadgesData(
    private val themeKey: String,
    private val category: BadgeCategory,
    private val levels: List<BadgeLevel>
) {

    fun getBadgeTitle(context: Context): String = DKResource.convertToString(context, themeKey)

    fun getBadgeCategory(): BadgeCategory = category

    fun getBadgeLevel1() = levels[0]

    fun getBadgeLevel2() = levels[1]

    fun getBadgeLevel3() = levels[2]

    fun isBadgeAcquired(): Triple<Boolean, Boolean, Boolean> =
        Triple(
            getBadgeLevel1().progressValue >= getBadgeLevel1().threshold,
            getBadgeLevel2().progressValue >= getBadgeLevel2().threshold,
            getBadgeLevel3().progressValue >= getBadgeLevel3().threshold
        )


    fun computePercent(progress: Double, threshold: Int): Double =
        (progress / threshold) * 10

    fun getPercent(): Triple<Double, Double, Double> =
        Triple(
            computePercent(getBadgeLevel1().progressValue, getBadgeLevel1().threshold),
            computePercent(getBadgeLevel2().progressValue, getBadgeLevel2().threshold),
            computePercent(getBadgeLevel3().progressValue, getBadgeLevel3().threshold)
        )

    fun getIcon(context: Context): Triple<Drawable?, Drawable?, Drawable?> =
        Triple(
            DKResource.convertToDrawable(context, getBadgeLevel1().iconKey),
            DKResource.convertToDrawable(context, getBadgeLevel2().iconKey),
            DKResource.convertToDrawable(context, getBadgeLevel3().iconKey)
        )

    fun getBadgeColor(dkLevel: DKLevel, isBadgeAcquired: Boolean): Int {
        return if (isBadgeAcquired) {
            R.color.neutral
        } else {
            when (dkLevel) {
                DKLevel.GOLD -> R.color.badgeLevel3Color
                DKLevel.SILVER -> R.color.badgeLevel2Color
                DKLevel.BRONZE -> R.color.badgeLevel1Color
            }
        }
    }

    fun getBadgeName(context: Context): Triple<String, String, String> =
        Triple(
            DKResource.convertToString(context, getBadgeLevel1().nameKey),
            DKResource.convertToString(context, getBadgeLevel2().nameKey),
            DKResource.convertToString(context, getBadgeLevel3().nameKey)
        )

    fun getBadgeDescription(context: Context): Triple<String, String, String> =
        Triple(
            DKResource.convertToString(context, getBadgeLevel1().descriptionKey),
            DKResource.convertToString(context, getBadgeLevel2().descriptionKey),
            DKResource.convertToString(context, getBadgeLevel3().descriptionKey)
        )
}

