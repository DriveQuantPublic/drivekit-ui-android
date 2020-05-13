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

    private fun getBadgeLevel1() = levels[0]

    private fun getBadgeLevel2() = levels[1]

    private fun getBadgeLevel3() = levels[2]

    fun isBadgeAcquired(): Triple<Boolean, Boolean, Boolean> =
        Triple(
            getBadgeLevel1().progressValue >= getBadgeLevel1().threshold,
            getBadgeLevel2().progressValue >= getBadgeLevel2().threshold,
            getBadgeLevel3().progressValue >= getBadgeLevel3().threshold
        )


    private fun computePercent(progress: Double, threshold: Int): Double =
        (progress / threshold) * 10

    fun getPercent(): Triple<Double, Double, Double> =
        Triple(
            computePercent(getBadgeLevel1().progressValue, getBadgeLevel1().threshold),
            computePercent(getBadgeLevel2().progressValue, getBadgeLevel2().threshold),
            computePercent(getBadgeLevel3().progressValue, getBadgeLevel3().threshold)
        )

    fun getIcon(context: Context): Triple<Drawable?, Drawable?, Drawable?> {
        val badgeIcon1 = if (isBadgeAcquired().first) {
            getBadgeLevel1().iconKey
        } else {
            getBadgeLevel1().defaultIconKey
        }

        val badgeIcon2 = if (isBadgeAcquired().second) {
            getBadgeLevel2().iconKey
        } else {
            getBadgeLevel2().defaultIconKey
        }

        val badgeIcon3 = if (isBadgeAcquired().third) {
            getBadgeLevel3().iconKey
        } else {
            getBadgeLevel3().defaultIconKey
        }

        return Triple(
            DKResource.convertToDrawable(context, badgeIcon1),
            DKResource.convertToDrawable(context, badgeIcon2),
            DKResource.convertToDrawable(context, badgeIcon3)
        )
    }

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

    fun getBadgeLevel(): Triple<DKLevel, DKLevel, DKLevel> =
        Triple(getBadgeLevel1().level, getBadgeLevel2().level, getBadgeLevel3().level)

    fun getBadgeProgressCongrats(context: Context) : Triple<String, String, String> {
        val textBadgeLevel1 = if (isBadgeAcquired().first) {
            DKResource.convertToString(context, getBadgeLevel1().congratsKey)
        } else {
            val remainingText = DKResource.convertToString(context, getBadgeLevel1().progressKey)
            val remainingValue = computeRemainingValue(getBadgeLevel1().progressValue, getBadgeLevel1().threshold)
            String.format(remainingText, remainingValue)
        }

        val textBadgeLevel2 = if (isBadgeAcquired().second) {
            DKResource.convertToString(context, getBadgeLevel2().congratsKey)
        } else {
            DKResource.convertToString(context, getBadgeLevel2().progressKey)
            val remainingText = DKResource.convertToString(context, getBadgeLevel2().progressKey)
            val remainingValue = computeRemainingValue(getBadgeLevel2().progressValue, getBadgeLevel2().threshold)
            String.format(remainingText, remainingValue)
        }

        val textBadgeLevel3 = if (isBadgeAcquired().third) {
            DKResource.convertToString(context, getBadgeLevel3().congratsKey)
        } else {
            DKResource.convertToString(context, getBadgeLevel3().progressKey)
            val remainingText = DKResource.convertToString(context, getBadgeLevel3().progressKey)
            val remainingValue = computeRemainingValue(getBadgeLevel3().progressValue, getBadgeLevel3().threshold)
            String.format(remainingText, remainingValue)
        }

        return Triple(textBadgeLevel1, textBadgeLevel2, textBadgeLevel3)
    }

    private fun computeRemainingValue(progress:Double, threshold: Int): Int = threshold - progress.toInt()
}

