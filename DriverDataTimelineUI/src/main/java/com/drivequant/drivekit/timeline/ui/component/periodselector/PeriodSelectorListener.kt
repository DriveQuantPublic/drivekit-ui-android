package com.drivequant.drivekit.timeline.ui.component.periodselector

import com.drivequant.drivekit.timeline.ui.component.TimelinePeriod

interface PeriodSelectorListener {
    fun onSelectPeriod(period: TimelinePeriod)
}