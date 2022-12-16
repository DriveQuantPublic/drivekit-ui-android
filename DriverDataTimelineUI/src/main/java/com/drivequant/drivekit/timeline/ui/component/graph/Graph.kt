package com.drivequant.drivekit.timeline.ui.component.graph

import java.util.Date

internal data class PointData(
    val date: Date,
    val interpolatedPoint: Boolean
)

internal data class GraphPoint(
    val x: Double,
    val y: Double,
    val data: PointData?
)
