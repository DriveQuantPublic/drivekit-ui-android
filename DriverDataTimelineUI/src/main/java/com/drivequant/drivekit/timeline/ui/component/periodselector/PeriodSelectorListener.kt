package com.drivequant.drivekit.timeline.ui.component.periodselector

import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod

interface PeriodSelectorListener {
    fun onSelectPeriod(period: DKTimelinePeriod)
}