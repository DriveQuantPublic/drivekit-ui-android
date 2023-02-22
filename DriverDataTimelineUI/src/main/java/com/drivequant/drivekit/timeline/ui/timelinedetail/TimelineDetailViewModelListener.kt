package com.drivequant.drivekit.timeline.ui.timelinedetail

import com.drivequant.drivekit.core.common.DKPeriod
import java.util.Date

internal interface TimelineDetailViewModelListener {
    fun onUpdateSelectedDate(date: Date)
    fun onUpdateSelectedPeriod(period: DKPeriod)
}
