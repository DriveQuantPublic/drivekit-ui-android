package com.drivequant.drivekit.timeline.ui.component.graph

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod

internal class TimelineGraphViewModel : ViewModel() {
    var type: GraphType = GraphType.BAR
    var period = DKTimelinePeriod.MONTH

    var selectedIndex: Int = 0
    var title: String = ""
    var description: String = ""

    //var xAxisConfig = GraphAxisConfig()
    //var yAxisConfig = GraphAxisConfig()

    var listener: TimelineGraphListener? = null

    fun update() {
        TODO()
    }
}