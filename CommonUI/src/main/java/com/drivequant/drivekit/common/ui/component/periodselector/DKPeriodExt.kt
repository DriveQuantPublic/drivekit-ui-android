package com.drivequant.drivekit.common.ui.component.periodselector

import com.drivequant.drivekit.core.common.DKPeriod

internal fun DKPeriod.getTitleResId() = when(this) {
    DKPeriod.WEEK -> "dk_common_period_selector_week"
    DKPeriod.MONTH -> "dk_common_period_selector_month"
    DKPeriod.YEAR -> "dk_common_period_selector_year"
}
