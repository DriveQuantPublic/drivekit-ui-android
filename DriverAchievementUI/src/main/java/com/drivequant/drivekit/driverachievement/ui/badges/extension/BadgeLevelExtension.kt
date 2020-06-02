package com.drivequant.drivekit.driverachievement.ui.badges.extension

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.BadgeLevel
import com.drivequant.drivekit.databaseutils.entity.DKLevel
import com.drivequant.drivekit.driverachievement.ui.R


fun BadgeLevel.isBadgeAcquired(): Boolean = progressValue >= threshold

fun BadgeLevel.computePercent(): Double = (progressValue / threshold) * 10

fun BadgeLevel.getIcon(context: Context): Drawable? {
    val badgeIcon = if (isBadgeAcquired()) iconKey else defaultIconKey
    return DKResource.convertToDrawable(context, badgeIcon)
}

fun BadgeLevel.getColor(): Int {
    return if (isBadgeAcquired()) {
        when (level) {
            DKLevel.BRONZE -> R.color.dkBadgeLevel1Color
            DKLevel.SILVER -> R.color.dkBadgeLevel2Color
            DKLevel.GOLD -> R.color.dkBadgeLevel3Color
        }
    } else {
        DriveKitUI.colors.neutralColor()
    }
}

fun BadgeLevel.getName(context: Context): String = DKResource.convertToString(context, nameKey)

fun BadgeLevel.getDescription(context: Context): String =
    DKResource.convertToString(context, descriptionKey)

fun BadgeLevel.getProgressCongrats(context: Context): String {
    return if (isBadgeAcquired()) {
        DKResource.convertToString(context, congratsKey)
    } else {
        val remainingText = DKResource.convertToString(context, progressKey)
        String.format(remainingText, threshold - progressValue.toInt())
    }
}

