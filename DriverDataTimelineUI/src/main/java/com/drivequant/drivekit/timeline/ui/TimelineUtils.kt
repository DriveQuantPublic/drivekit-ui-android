package com.drivequant.drivekit.timeline.ui

import com.drivequant.drivekit.common.ui.component.DKScoreType
import com.drivequant.drivekit.core.common.DKPeriod
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.startingFrom
import com.drivequant.drivekit.databaseutils.entity.DKRawTimeline
import com.drivequant.drivekit.databaseutils.entity.TimelinePeriod
import com.drivequant.drivekit.timeline.ui.component.graph.TimelineScoreItemType
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

internal fun String.toTimelineDate(): Date? = TimelineUtils.getBackendDateFormat().parse(this)
internal fun Date.toTimelineString(): String = TimelineUtils.getBackendDateFormat().format(this)

internal fun DKPeriod.getTitleResId() = when(this) {
    DKPeriod.WEEK -> "dk_common_period_selector_week"
    DKPeriod.MONTH -> "dk_common_period_selector_month"
    DKPeriod.YEAR -> "dk_common_period_selector_year"
}

internal object TimelineUtils {
    fun getBackendDateFormat(): DateFormat {
        val backendDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        backendDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return backendDateFormat
    }

    fun updateSelectedDateForNewPeriod(period: DKPeriod, previousSelectedDate: Date?, weekTimeline: DKRawTimeline?, monthTimeline: DKRawTimeline?): Date? {
        if (previousSelectedDate != null && weekTimeline != null && monthTimeline != null) {
            if (weekTimeline.period != TimelinePeriod.WEEK || monthTimeline.period != TimelinePeriod.MONTH) {
                throw IllegalArgumentException("Given timeline period are invalid, please check your parameters")
            }
            val timeline: DKRawTimeline
            val compareDate: Date
            when (period) {
                DKPeriod.WEEK -> {
                    compareDate = previousSelectedDate
                    timeline = weekTimeline
                }
                DKPeriod.MONTH -> {
                    compareDate = previousSelectedDate.startingFrom(CalendarField.MONTH)
                    timeline = monthTimeline
                }
                DKPeriod.YEAR -> throw IllegalAccessException("Not managed in Timeline")
            }
            var newSelectedDate = previousSelectedDate
            for (dateString in timeline.allContext.date) {
                val date = dateString.toTimelineDate()!!
                if (date >= compareDate) {
                    newSelectedDate = date
                    break
                }
            }
            return newSelectedDate
        } else {
            return null
        }
    }
}


internal fun <E> List<E>.getSafe(index: Int): E? {
    return if (index < 0 || index >= this.size) {
        null
    } else {
        this[index]
    }
}

internal operator fun <E> List<E>.get(index: Int, default: E): E = getSafe(index) ?: default

internal fun <E> List<E>.addValueIfNotEmpty(index: Int, list: MutableList<E>) {
    if (this.isNotEmpty()) {
        list.add(this[index])
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
