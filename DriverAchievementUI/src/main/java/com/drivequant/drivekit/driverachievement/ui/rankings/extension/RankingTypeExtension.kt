package com.drivequant.drivekit.driverachievement.ui.rankings.extension

import com.drivequant.drivekit.core.access.AccessType
import com.drivequant.drivekit.core.access.DriveKitAccess
import com.drivequant.drivekit.databaseutils.entity.RankingType

fun List<RankingType>.sanitize(): List<RankingType> {
    val newItems = this.toMutableList()
    val iterator = newItems.iterator()
    while (iterator.hasNext()) {
        if (!iterator.next().hasAccess()) {
            iterator.remove()
        }
    }
    return newItems.distinct()
}

fun RankingType.hasAccess(): Boolean {
    val accessType = when (this) {
        RankingType.SAFETY -> AccessType.SAFETY
        RankingType.ECO_DRIVING -> AccessType.ECODRIVING
        RankingType.DISTRACTION -> AccessType.PHONE_DISTRACTION
        RankingType.SPEEDING -> AccessType.SPEEDING
    }
    return DriveKitAccess.hasAccess(accessType)
}