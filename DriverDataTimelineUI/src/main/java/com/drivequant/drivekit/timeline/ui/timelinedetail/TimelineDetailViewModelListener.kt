package com.drivequant.drivekit.timeline.ui.timelinedetail

import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import java.util.Date

internal interface TimelineDetailViewModelListener {
    fun onUpdateSelectedDate(date: Date)
    fun onUpdateSelectedPeriod(period: DKTimelinePeriod)
}
