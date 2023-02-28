package com.drivequant.drivekit.common.ui.utils

import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.core.scoreslevels.*

fun DKScoreType.getIconResId() = when (this) {
    DKScoreType.SAFETY -> "dk_common_safety_flat"
    DKScoreType.DISTRACTION -> "dk_common_distraction_flat"
    DKScoreType.ECO_DRIVING -> "dk_common_ecodriving_flat"
    DKScoreType.SPEEDING -> "dk_common_speeding_flat"
}

@StringRes
fun DKScoreType.getTitleId(): Int = when (this) {
    DKScoreType.ECO_DRIVING -> R.string.dk_common_ecodriving
    DKScoreType.DISTRACTION -> R.string.dk_common_distraction
    DKScoreType.SAFETY -> R.string.dk_common_safety
    DKScoreType.SPEEDING -> R.string.dk_common_speed
}