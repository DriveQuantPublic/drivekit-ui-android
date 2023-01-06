package com.drivequant.drivekit.timeline.ui.component.periodselector

import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod

internal interface PeriodSelectorItemListener {
    fun onPeriodSelected(period: DKTimelinePeriod)
}