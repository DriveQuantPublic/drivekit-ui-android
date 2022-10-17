package com.drivequant.drivekit.timeline.ui.component.graph

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.timeline.ui.component.TimelinePeriod

internal class TimelineGraphViewModel : ViewModel() {
    var type: GraphType = GraphType.BAR
    var period: TimelinePeriod = TimelinePeriod.MONTH

    var selectedIndex: Int = 0
    var title: String = ""
    var description: String = ""

    var xAxisConfig: GraphAxisConfig? = null
    var yAxisConfig: GraphAxisConfig? = null

    var listener: TimelineGraphListener? = null

    fun update() {
        TODO()
    }
}