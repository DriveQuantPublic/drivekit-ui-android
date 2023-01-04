package com.drivequant.drivekit.common.ui.component

import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.core.access.AccessType
import com.drivequant.drivekit.core.access.DriveKitAccess

enum class DKScoreType {
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

    @StringRes
    fun getTitleId(): Int = when (this) {
        ECO_DRIVING -> R.string.dk_common_ecodriving
        DISTRACTION -> R.string.dk_common_distraction
        SAFETY -> R.string.dk_common_safety
        SPEEDING -> R.string.dk_common_speed
    }
}
