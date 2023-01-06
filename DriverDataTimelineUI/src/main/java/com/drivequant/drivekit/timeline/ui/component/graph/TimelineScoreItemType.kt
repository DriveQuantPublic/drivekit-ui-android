package com.drivequant.drivekit.timeline.ui.component.graph

internal enum class TimelineScoreItemType {
    SAFETY_ACCELERATION,
    SAFETY_BRAKING,
    SAFETY_ADHERENCE,
    ECODRIVING_EFFICIENCY_ACCELERATION,
    ECODRIVING_EFFICIENCY_BRAKE,
    ECODRIVING_EFFICIENCY_SPEED_MAINTAIN,
    ECODRIVING_FUEL_VOLUME,
    ECODRIVING_FUEL_SAVINGS,
    ECODRIVING_CO2MASS,
    DISTRACTION_UNLOCK,
    DISTRACTION_CALL_FORBIDDEN_DURATION,
    DISTRACTION_PERCENTAGE_OF_TRIPS_WITH_FORBIDDEN_CALL,
    SPEEDING_DURATION,
    SPEEDING_DISTANCE
}
