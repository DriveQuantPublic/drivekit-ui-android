package com.drivequant.drivekit.timeline.ui.component.graph.viewmodel

import com.drivequant.drivekit.timeline.ui.component.graph.GraphAxisConfig
import com.drivequant.drivekit.timeline.ui.component.graph.GraphPoint
import com.drivequant.drivekit.timeline.ui.component.graph.GraphType

internal interface GraphViewModel {
    var graphViewModelDidUpdate: (() -> Unit)?
    val type: GraphType
    val points: List<GraphPoint?>
    val selectedIndex: Int?
    val xAxisConfig: GraphAxisConfig?
    val yAxisConfig: GraphAxisConfig?
}