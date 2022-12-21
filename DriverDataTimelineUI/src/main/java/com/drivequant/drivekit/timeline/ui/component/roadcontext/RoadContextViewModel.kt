package com.drivequant.drivekit.timeline.ui.component.roadcontext

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.Timeline
import com.drivequant.drivekit.common.ui.component.DKScoreType

import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.EmptyRoadContextType
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.TimelineRoadContext
import com.drivequant.drivekit.timeline.ui.totalDistanceForAllContexts

internal class RoadContextViewModel : ViewModel() {

    val changeObserver: MutableLiveData<Any> = MutableLiveData()

    var distanceByContext = mapOf<TimelineRoadContext, Double>()
        private set(value) {
            field = value.filterNot { it.value <= 0.0 }
        }

    private var totalDistanceForAllContext = 0.0
    private var timeline: Timeline? = null
    private var distance = 0.0
    private var hasData: Boolean = false
    private var selectedScore: DKScoreType = DKScoreType.SAFETY
    var emptyRoadContextType: EmptyRoadContextType? = null
        private set

    fun configure(timeline: Timeline?, selectedScore: DKScoreType, selectedIndex: Int?, distanceByContext: Map<TimelineRoadContext, Double>, hasData: Boolean) {
        timeline?.let { timeline
            selectedIndex?.let { selectedIndex ->
                this.timeline = timeline
                this.totalDistanceForAllContext = timeline.totalDistanceForAllContexts(selectedScore, selectedIndex)
                this.selectedScore = selectedScore
                this.distanceByContext = distanceByContext
                this.hasData = hasData
                distance = 0.0
                distanceByContext.forEach {
                    distance += it.value
                }
            }
        }
        emptyRoadContextType = if (!hasData) {
            EmptyRoadContextType.EMPTY_DATA
        } else if (distanceByContext.isEmpty()) {
            when (selectedScore) {
                DKScoreType.DISTRACTION, DKScoreType.SPEEDING -> EmptyRoadContextType.NO_DATA
                DKScoreType.SAFETY -> EmptyRoadContextType.NO_DATA_SAFETY
                DKScoreType.ECO_DRIVING -> EmptyRoadContextType.NO_DATA_ECODRIVING
            }
        } else {
            null
        }
        changeObserver.postValue(Any())
    }

    fun displayData() = emptyRoadContextType == null

    fun formatDistanceInKm(context: Context): String {
        return DKDataFormatter.formatMeterDistanceInKm(
            context = context,
            distance = totalDistanceForAllContext * 1000,
            minDistanceToRemoveFractions = 10.0
        ).convertToString()
    }

    fun getPercent(roadContext: TimelineRoadContext) =
        if (distanceByContext.isEmpty()) {
            0.0
        } else {
            distanceByContext[roadContext]?.div(totalDistanceForAllContext)?.let {
                it * 100
            } ?: run { 0.0 }
        }
}

internal fun TimelineRoadContext.getColorResId() = when (this) {
    TimelineRoadContext.HEAVY_URBAN_TRAFFIC -> R.color.dkRoadContextHeavyUrbanTrafficColor
    TimelineRoadContext.CITY -> R.color.dkRoadContextCityColor
    TimelineRoadContext.SUBURBAN -> R.color.dkRoadContextSuburbanColor
    TimelineRoadContext.EXPRESSWAYS -> R.color.dkRoadContextExpresswaysColor
}

internal fun TimelineRoadContext.getTitleResId() = when (this) {
    TimelineRoadContext.HEAVY_URBAN_TRAFFIC -> "dk_timeline_road_context_heavy_urban_traffic"
    TimelineRoadContext.CITY -> "dk_timeline_road_context_city"
    TimelineRoadContext.SUBURBAN -> "dk_timeline_road_context_suburban"
    TimelineRoadContext.EXPRESSWAYS -> "dk_timeline_road_context_expressways"
}