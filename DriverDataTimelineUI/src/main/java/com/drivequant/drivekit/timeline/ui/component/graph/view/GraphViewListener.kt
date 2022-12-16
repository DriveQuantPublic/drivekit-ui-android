package com.drivequant.drivekit.timeline.ui.component.graph.view

import com.drivequant.drivekit.timeline.ui.component.graph.GraphPoint

internal interface GraphViewListener {
    fun onSelectPoint(point: GraphPoint)
}