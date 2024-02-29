package com.drivequant.drivekit.timeline.ui

import com.drivequant.drivekit.common.ui.component.dateselector.DKDateSelectorViewModel
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.databaseutils.entity.DKRawTimeline
import com.drivequant.drivekit.timeline.ui.component.graph.TimelineScoreItemType
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.ceil

internal fun String.toTimelineDate(): Date? = TimelineUtils.getBackendDateFormat().parse(this)
internal fun Date.toTimelineString(): String = TimelineUtils.getBackendDateFormat().format(this)

internal object TimelineUtils {
    fun getBackendDateFormat(): DateFormat {
        val backendDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        backendDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return backendDateFormat
    }

    fun updateSelectedDate(oldPeriod: DKPeriod, previousSelectedDate: Date?, timeline: DKRawTimeline): Date? {
        return if (previousSelectedDate != null) {
            val dates: MutableList<Date> = mutableListOf()
            val dateToIndex: MutableMap<Date, Int> = mutableMapOf()
            for ((index, rawDate) in timeline.allContext.date.withIndex()) {
                val date = rawDate.toTimelineDate()!!
                dates.add(date)
                dateToIndex[date] = index
            }
            DKDateSelectorViewModel.newSelectedDate(
                previousSelectedDate,
                oldPeriod,
                dates
            ) { _, date ->
                dateToIndex[date]?.let { index ->
                    timeline.hasValidTripScored(index)
                } ?: false
            }
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
