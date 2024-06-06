package com.drivequant.drivekit.driverachievement.ui.badges.extension

import android.content.Context
import androidx.annotation.ColorRes
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.BadgeCharacteristics
import com.drivequant.drivekit.databaseutils.entity.Level
import com.drivequant.drivekit.driverachievement.ui.R

fun BadgeCharacteristics.isBadgeAcquired() = progressValue >= threshold

fun BadgeCharacteristics.computePercent() = progressValue.div(threshold) * 10

fun BadgeCharacteristics.getName(context: Context) = DKResource.convertToString(context, name)

fun BadgeCharacteristics.getDescription(context: Context) = DKResource.convertToString(context, descriptionValue)

fun BadgeCharacteristics.getIcon(context: Context) =
    if (isBadgeAcquired()) {
        icon
    } else {
        defaultIcon
    }.let {
        DKResource.convertToDrawable(context, it)
    }

@ColorRes
fun BadgeCharacteristics.getColor(): Int = if (isBadgeAcquired()) {
    when (level) {
        Level.BRONZE -> R.color.dkBadgeLevel1Color
        Level.SILVER -> R.color.dkBadgeLevel2Color
        Level.GOLD -> R.color.dkBadgeLevel3Color
    }
} else {
    com.drivequant.drivekit.common.ui.R.color.neutralColor
}

fun BadgeCharacteristics.getProgressCongrats(context: Context) = if (isBadgeAcquired()) {
    DKResource.convertToString(context, congrats)
} else {
    val remainingText = DKResource.convertToString(context, progress)
    String.format(remainingText, threshold - progressValue.toInt())
}
