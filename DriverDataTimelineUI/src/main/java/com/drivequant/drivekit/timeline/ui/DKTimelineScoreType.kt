package com.drivequant.drivekit.timeline.ui

import com.drivequant.drivekit.core.access.AccessType
import com.drivequant.drivekit.core.access.DriveKitAccess

enum class DKTimelineScoreType {
    SAFETY, ECO_DRIVING, DISTRACTION, SPEEDING;

    fun getIconResId() = when (this) {
        SAFETY -> "dk_common_safety_flat"
        DISTRACTION -> "dk_common_distraction_flat"
        ECO_DRIVING -> "dk_common_ecodriving_flat"
        SPEEDING -> "dk_common_speeding_flat"
    }

    fun hasAccess() = when (this) {
        SAFETY -> AccessType.SAFETY
        ECO_DRIVING -> AccessType.ECODRIVING
        DISTRACTION -> AccessType.PHONE_DISTRACTION
        SPEEDING -> AccessType.SPEEDING
    }.let {
        DriveKitAccess.hasAccess(it)
    }
}