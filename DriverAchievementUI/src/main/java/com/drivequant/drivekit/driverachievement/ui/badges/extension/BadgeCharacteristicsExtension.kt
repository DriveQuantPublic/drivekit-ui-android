package com.drivequant.drivekit.driverachievement.ui.badges.extension

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.BadgeCharacteristics
import com.drivequant.drivekit.databaseutils.entity.Level
import com.drivequant.drivekit.driverachievement.ui.R


fun BadgeCharacteristics.isBadgeAcquired(): Boolean = progressValue >= threshold

fun BadgeCharacteristics.computePercent(): Double = (progressValue / threshold) * 10

fun BadgeCharacteristics.getIcon(context: Context): Drawable? {
    val badgeIcon = if (isBadgeAcquired()) icon else defaultIcon
    return DKResource.convertToDrawable(context, badgeIcon)
}

fun BadgeCharacteristics.getColor(): Int {
    return if (isBadgeAcquired()) {
        when (level) {
            Level.BRONZE -> R.color.dkBadgeLevel1Color
            Level.SILVER -> R.color.dkBadgeLevel2Color
            Level.GOLD -> R.color.dkBadgeLevel3Color
        }
    } else {
        DriveKitUI.colors.neutralColor()
    }
}

fun BadgeCharacteristics.getName(context: Context): String = DKResource.convertToString(context, name)

fun BadgeCharacteristics.getDescription(context: Context): String =
    DKResource.convertToString(context, descriptionValue)

fun BadgeCharacteristics.getProgressCongrats(context: Context): String {
    return if (isBadgeAcquired()) {
        DKResource.convertToString(context, congrats)
    } else {
        val remainingText = DKResource.convertToString(context, progress)
        String.format(remainingText, threshold - progressValue.toInt())
    }
}

