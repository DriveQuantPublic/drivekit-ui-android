package com.drivequant.drivekit.common.ui.component.periodselector

import com.drivequant.drivekit.core.common.DKPeriod

interface DKPeriodSelectorItemListener {
    fun onPeriodSelected(period: DKPeriod)
}
