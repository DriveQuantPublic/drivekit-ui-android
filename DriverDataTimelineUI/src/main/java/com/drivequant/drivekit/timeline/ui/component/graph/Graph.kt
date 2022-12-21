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

internal object GraphConstants {
    const val DEFAULT_NUMBER_OF_INTERVAL_IN_Y_AXIS = 10
    const val DEFAULT_MAX_VALUE_IN_Y_AXIS = 10
    const val NOT_ENOUGH_DATA_IN_GRAPH_THRESHOLD = 10.0
    const val MAX_VALUE_IN_Y_AXIS_WHEN_NOT_ENOUGH_DATA_IN_GRAPH = 10.0
}
