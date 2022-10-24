package com.drivequant.drivekit.timeline.ui.timeline

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.access.AccessType
import com.drivequant.drivekit.core.access.DriveKitAccess
import com.drivequant.drivekit.databaseutils.entity.*
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.driverdata.timeline.TimelineQueryListener
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.timeline.ui.DriverDataTimelineUI
import java.util.*

internal class TimelineViewModel : ViewModel() {

    companion object {
        const val MONTHS_NUMBER_PER_YEAR = 12
    }

    private var selectedTimelineScoreType: DKTimelineScore
    private var selectedTimelinePeriod: DKTimelinePeriod

    var timelineScoreTypes = mutableListOf<DKTimelineScore>()
    var timelinePeriodTypes = mutableListOf<DKTimelinePeriod>()

    val timelinesDataLiveData: MutableLiveData<List<TimelineData>> = MutableLiveData()
    val syncStatus: MutableLiveData<TimelineSyncStatus> = MutableLiveData()

    init {
        timelineScoreTypes.addAll(DriverDataTimelineUI.scoresType)
        timelinePeriodTypes.addAll(DKTimelinePeriod.values().toList())

        selectedTimelineScoreType = timelineScoreTypes.first()
        selectedTimelinePeriod = timelinePeriodTypes.first()
    }

    fun fetchTimeline() {
        DriveKitDriverData.getTimelines(
            periods = DKTimelinePeriod.values().toList(),
            listener = object : TimelineQueryListener {
                override fun onResponse(
                    timelineSyncStatus: TimelineSyncStatus,
                    timelines: List<Timeline>
                ) {
                    syncStatus.postValue(timelineSyncStatus)
                    updateTimeline()
                }
            },
            synchronizationType = SynchronizationType.DEFAULT
        )
    }

    private fun updateTimeline() {
        val periods = listOf(selectedTimelinePeriod)
        DriveKitDriverData.getTimelines(
            periods = periods,
            listener = object : TimelineQueryListener {
                override fun onResponse(
                    timelineSyncStatus: TimelineSyncStatus,
                    timelines: List<Timeline>
                ) {
                    val timelinesData = timelines.map { it.toTimelineData() }
                    timelinesDataLiveData.postValue(timelinesData)
                }
            },
            synchronizationType = SynchronizationType.CACHE
        )
    }

    fun updateTimelinePeriod(period: DKTimelinePeriod) {
        selectedTimelinePeriod = period
        Log.e("TEST", selectedTimelinePeriod.name)
        updateTimeline()
    }

    fun updateTimelineScore(position: Int) {
        selectedTimelineScoreType = timelineScoreTypes[position]
        Log.e("TEST", selectedTimelineScoreType.name)
        updateTimeline()
    }

    //TODO WIP : complete missing month method
    //transformTimelineData(timelines[0].toTimelineData().allContext.date)
    private fun transformTimelineData(dates :List<String>): List<String> {
        var fromIndex = 0
        var toIndex: Int
        val monthsPerYear = mutableListOf<List<Int>>()
        val datesPerYear = mutableListOf<List<String>>()

        val years = dates.map { it.substringBefore("-") }
        val months = dates.map { it.substringAfter("-").substringBefore("-").toInt() }

        computeYearsOccurrence(years).entries.forEach {
            toIndex = fromIndex + it.value
            monthsPerYear.add(months.subList(fromIndex, toIndex))
            datesPerYear.add(dates.subList(fromIndex, toIndex))
            fromIndex = toIndex
        }

        return addMissingMonths(
            datesPerYear,
            computeMissingMonthsPerYear(monthsPerYear),
            years.distinct())
    }

    private fun computeYearsOccurrence(years: List<String>): Map<String,Int> {
        val map = mutableMapOf<String, Int>()
        years.distinct().forEach { year ->
            map[year] = Collections.frequency(years, year)
        }
        return map
    }

    private fun addMissingMonths(
        datesPerYear: List<List<String>>,
        missingMonthsPerYear: List<List<Int>>,
        years: List<String>,
    ) : List<String> {
        val dates = mutableListOf<MutableList<String>>()
        dates.addAll(datesPerYear.map { it.toMutableList() })

        for (i in dates.indices) {
            missingMonthsPerYear[i].forEach {
                var str = "$it"
                if (it < 10) {
                    str = "0$it"
                }
                dates[i].add(it, "${years[i]}-$str-01T12:00:00.000+00:00")
            }
        }
        return dates.flatten()
    }

    private fun findMissingNumbers(numbers: List<Int>, size: Int): List<Int> {
        val temp = IntArray(size + 1)
        val missingNumbers = mutableListOf<Int>()
        for (i in 0 until size) {
            temp[i] = 0
        }
        for (element in numbers) {
            temp[element - 1] = 1
        }
        for (i in 0 until size) {
            if (temp[i] == 0) {
                missingNumbers.add(i + 1)
            }
        }
        return missingNumbers
    }

    private fun computeMissingMonthsPerYear(monthsPerYear: List<List<Int>>): List<List<Int>> {
        val missingMonths = mutableListOf<List<Int>>()
        monthsPerYear.forEach {
            missingMonths.add(findMissingNumbers(it, MONTHS_NUMBER_PER_YEAR))
        }
        return missingMonths
    }
}

data class RoadContextItemData(
    val type: RoadContext,
    val distance: List<Double>,
)

data class AllContextItemData(
    val date: List<String>,
    val numberTripScored: List<Int>,
    val numberTripTotal: List<Int>,
    val distance: List<Double>,
    val duration: List<Int>,
    val efficiency: List<Double>,
    val safety: List<Double>,
    val acceleration: List<Int>,
    val braking: List<Int>,
    val adherence: List<Int>,
    val phoneDistraction: List<Double>,
    val speeding: List<Double>,
    val co2Mass: List<Double>,
    val fuelVolume: List<Double>,
    val unlock: List<Int>,
    val lock: List<Int>,
    val callAuthorized: List<Int>,
    val callForbidden: List<Int>,
    val callAuthorizedDuration: List<Int>,
    val callForbiddenDuration: List<Int>,
    val numberTripWithForbiddenCall: List<Int>,
    val speedingDuration: List<Int>,
    val speedingDistance: List<Double>,
    val efficiencyBrake: List<Double>,
    val efficiencyAcceleration: List<Double>,
    val efficiencySpeedMaintain: List<Double>
)

data class TimelineData(
    val period: DKTimelinePeriod,
    val allContext: AllContextItemData,
    val roadContexts: List<RoadContextItemData>
)

fun RoadContextItem.toRoadContextItemData() = RoadContextItemData(
    type,
    distance,
)

fun AllContextItem.toAllContextItemData() = AllContextItemData(
    date,
    numberTripScored,
    numberTripTotal,
    distance,
    duration,
    efficiency,
    safety,
    acceleration,
    braking,
    adherence,
    phoneDistraction,
    speeding,
    co2Mass,
    fuelVolume,
    unlock,
    lock,
    callAuthorized,
    callForbidden,
    callAuthorizedDuration,
    callForbiddenDuration,
    numberTripWithForbiddenCall,
    speedingDuration,
    speedingDistance,
    efficiencyBrake,
    efficiencyAcceleration,
    efficiencySpeedMaintain
)

fun Timeline.toTimelineData() = TimelineData(
    period = period.toDKTimelinePeriod(),
    allContext = allContext.toAllContextItemData(),
    roadContexts = roadContexts.map { it.toRoadContextItemData() }
)

fun TimelinePeriod.toDKTimelinePeriod() = when (this) {
    TimelinePeriod.WEEK -> DKTimelinePeriod.WEEK
    TimelinePeriod.MONTH -> DKTimelinePeriod.MONTH
}

//TODO (Replace with title resId)
fun DKTimelinePeriod.getTitleResId() = when(this) {
    DKTimelinePeriod.WEEK -> "Par semaine"
    DKTimelinePeriod.MONTH -> "Par mois"
}

enum class DKTimelineScore {
    SAFETY, ECO_DRIVING, DISTRACTION, SPEEDING;

    fun getIconResId() = when (this) {
        SAFETY -> "dk_common_safety_flat"
        DISTRACTION -> "dk_common_distraction_flat"
        ECO_DRIVING -> "dk_common_ecodriving_flat"
        SPEEDING -> "dk_common_speeding_flat"
    }

    fun hasAccess() = when (this) {
        SAFETY -> AccessType.SAFETY
        ECO_DRIVING -> AccessType.ECODRIVING
        DISTRACTION -> AccessType.PHONE_DISTRACTION
        SPEEDING -> AccessType.SPEEDING
    }.let {
        DriveKitAccess.hasAccess(it)
    }
}