package com.drivequant.drivekit.common.ui.component.periodselector

import com.drivequant.drivekit.databaseutils.entity.DKPeriod

interface DKPeriodSelectorItemListener {
    fun onPeriodSelected(period: DKPeriod)
}
