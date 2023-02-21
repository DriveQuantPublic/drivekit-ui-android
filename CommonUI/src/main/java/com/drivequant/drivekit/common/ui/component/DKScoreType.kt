package com.drivequant.drivekit.common.ui.component

import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.R

// Beware that it also exists a DKScoreType enum on DriveKit Core
enum class DKScoreType {
    SAFETY, ECO_DRIVING, DISTRACTION, SPEEDING;

    fun getIconResId() = when (this) {
        SAFETY -> "dk_common_safety_flat"
        DISTRACTION -> "dk_common_distraction_flat"
        ECO_DRIVING -> "dk_common_ecodriving_flat"
        SPEEDING -> "dk_common_speeding_flat"
    }
    fun hasAccess() = when (this) {
        SAFETY -> com.drivequant.drivekit.core.scoreslevels.DKScoreType.SAFETY
        ECO_DRIVING -> com.drivequant.drivekit.core.scoreslevels.DKScoreType.ECO_DRIVING
        DISTRACTION -> com.drivequant.drivekit.core.scoreslevels.DKScoreType.DISTRACTION
        SPEEDING -> com.drivequant.drivekit.core.scoreslevels.DKScoreType.SPEEDING
    }.hasAccess()

    @StringRes
    fun getTitleId(): Int = when (this) {
        ECO_DRIVING -> R.string.dk_common_ecodriving
        DISTRACTION -> R.string.dk_common_distraction
        SAFETY -> R.string.dk_common_safety
        SPEEDING -> R.string.dk_common_speed
    }
}
