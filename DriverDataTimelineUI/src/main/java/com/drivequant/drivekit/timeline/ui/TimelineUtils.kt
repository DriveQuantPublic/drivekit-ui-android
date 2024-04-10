package com.drivequant.drivekit.timeline.ui

import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorViewModel
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.timeline.ui.component.graph.TimelineScoreItemType
import java.util.Date
import kotlin.math.ceil

internal object TimelineUtils {

    fun updateSelectedDate(oldPeriod: DKPeriod, previousSelectedDate: Date?, timeline: DKDriverTimeline): Date? {
        return if (previousSelectedDate != null) {
            val dates: List<Date> = timeline.allContext.map { it.date }
            DKDateSelectorViewModel.newSelectedDate(previousSelectedDate, oldPeriod, dates)
        } else {
            return null
        }
    }
}

internal fun DKScoreType.associatedScoreItemTypes(): List<TimelineScoreItemType> {
    return when (this) {
        DKScoreType.DISTRACTION -> listOf(
            TimelineScoreItemType.DISTRACTION_UNLOCK,
            TimelineScoreItemType.DISTRACTION_CALL_FORBIDDEN_DURATION,
            TimelineScoreItemType.DISTRACTION_PERCENTAGE_OF_TRIPS_WITH_FORBIDDEN_CALL
        )
        DKScoreType.ECO_DRIVING -> listOf(
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_ACCELERATION,
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_BRAKE,
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_SPEED_MAINTAIN,
            TimelineScoreItemType.ECODRIVING_FUEL_VOLUME,
            TimelineScoreItemType.ECODRIVING_FUEL_SAVINGS,
            TimelineScoreItemType.ECODRIVING_CO2MASS
        )
        DKScoreType.SAFETY -> listOf(
            TimelineScoreItemType.SAFETY_ACCELERATION,
            TimelineScoreItemType.SAFETY_BRAKING,
            TimelineScoreItemType.SAFETY_ADHERENCE
        )
        DKScoreType.SPEEDING -> listOf(
            TimelineScoreItemType.SPEEDING_DURATION,
            TimelineScoreItemType.SPEEDING_DISTANCE
        )
    }
}

internal fun Double.ceilToValueDivisibleBy100(): Double {
    val intValue = ceil(this).toInt()
    val nextValueDivisibleBy100 = ((intValue / 100).plus(if (intValue % 100 == 0) 0 else 1)) * 100
    return nextValueDivisibleBy100.toDouble()
}

internal fun Double.ceilToLowestValueWithNiceStep(): Double = when (ceil(this).toInt()) {
    in 0..10 -> 10.0
    else -> ceilToValueDivisibleBy100()
}
