package com.drivequant.drivekit.common.ui.extension

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.core.scoreslevels.DKScoreType

@DrawableRes
fun DKScoreType.getIconResId() = when (this) {
    DKScoreType.SAFETY -> R.drawable.dk_common_safety_flat
    DKScoreType.DISTRACTION -> R.drawable.dk_common_distraction_flat
    DKScoreType.ECO_DRIVING -> R.drawable.dk_common_ecodriving_flat
    DKScoreType.SPEEDING -> R.drawable.dk_common_speeding_flat
}

@StringRes
fun DKScoreType.getTitleId(): Int = when (this) {
    DKScoreType.ECO_DRIVING -> R.string.dk_common_ecodriving
    DKScoreType.DISTRACTION -> R.string.dk_common_distraction
    DKScoreType.SAFETY -> R.string.dk_common_safety
    DKScoreType.SPEEDING -> R.string.dk_common_speed
}
