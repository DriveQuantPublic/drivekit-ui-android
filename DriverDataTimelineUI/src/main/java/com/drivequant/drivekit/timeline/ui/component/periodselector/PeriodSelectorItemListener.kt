package com.drivequant.drivekit.timeline.ui.component.periodselector

import com.drivequant.drivekit.core.common.DKPeriod

internal interface PeriodSelectorItemListener {
    fun onPeriodSelected(period: DKPeriod)
}
