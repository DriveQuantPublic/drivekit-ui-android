package com.drivequant.drivekit.common.ui.component.periodselector

import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.databaseutils.entity.DKPeriod

@StringRes
internal fun DKPeriod.getTitleResId() = when(this) {
    DKPeriod.WEEK -> R.string.dk_common_period_selector_week
    DKPeriod.MONTH -> R.string.dk_common_period_selector_month
    DKPeriod.YEAR -> R.string.dk_common_period_selector_year
}
