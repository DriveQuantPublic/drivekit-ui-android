package com.drivequant.drivekit.timeline.ui.component.roadcontext.enum

import com.drivequant.drivekit.databaseutils.entity.RoadContext

enum class TimelineRoadContext {
    EXPRESSWAYS,
    HEAVY_URBAN_TRAFFIC,
    CITY,
    SUBURBAN;
}

internal fun RoadContext.toTimelineRoadContext() = when (this) {
    RoadContext.EXPRESSWAYS -> TimelineRoadContext.EXPRESSWAYS
    RoadContext.HEAVY_URBAN_TRAFFIC -> TimelineRoadContext.HEAVY_URBAN_TRAFFIC
    RoadContext.CITY -> TimelineRoadContext.CITY
    RoadContext.SUBURBAN -> TimelineRoadContext.SUBURBAN
    else -> throw IllegalArgumentException("$this RoadContext is not supported")
}