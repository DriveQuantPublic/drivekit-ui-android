package com.drivequant.drivekit.timeline.ui.component.roadcontext

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.timeline.ui.DKTimelineScoreType

import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.EmptyRoadContextType
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.TimelineRoadContext

internal class RoadContextViewModel : ViewModel() {

    val changeObserver: MutableLiveData<Any> = MutableLiveData()

    var distanceByContext = mapOf<TimelineRoadContext, Double>()
        private set(value) {
            field = value.filterNot { it.value <= 0.0 }
        }

    private var distance = 0.0
    private var hasData: Boolean = false
    private var selectedScore: DKTimelineScoreType = DKTimelineScoreType.SAFETY
    var emptyRoadContextType: EmptyRoadContextType? = null
        private set

    fun configure(selectedScore: DKTimelineScoreType, distanceByContext: Map<TimelineRoadContext, Double>, hasData: Boolean) {
        this.selectedScore = selectedScore
        this.distanceByContext = distanceByContext
        this.hasData = hasData
        distance = 0.0
        distanceByContext.forEach {
            distance += it.value
        }

        emptyRoadContextType = if (!hasData) {
            EmptyRoadContextType.EMPTY_DATA
        } else if (distanceByContext.isEmpty()) {
            when (selectedScore) {
                DKTimelineScoreType.DISTRACTION, DKTimelineScoreType.SPEEDING -> EmptyRoadContextType.NO_DATA
                DKTimelineScoreType.SAFETY -> EmptyRoadContextType.NO_DATA_SAFETY
                DKTimelineScoreType.ECO_DRIVING -> EmptyRoadContextType.NO_DATA_ECODRIVING
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
            distance = distance * 1000,
            minDistanceToRemoveFractions = 10.0
        ).convertToString()
    }

    fun getPercent(roadContext: TimelineRoadContext) =
        if (distanceByContext.isEmpty()) {
            0.0
        } else {
            distanceByContext[roadContext]?.div(distance)?.let {
                it * 100
            } ?: run { 0.0 }
        }

    @Suppress("UNCHECKED_CAST")
    class RoadContextViewModelFactory : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RoadContextViewModel() as T
        }
    }
}

internal fun TimelineRoadContext.getColorResId() = when (this) {
    TimelineRoadContext.HEAVY_URBAN_TRAFFIC -> R.color.dkRoadContextUrbainDenseColor
    TimelineRoadContext.CITY -> R.color.dkRoadContextUrbainFluidColor
    TimelineRoadContext.SUBURBAN -> R.color.dkRoadContextSubUrbainColor
    TimelineRoadContext.EXPRESSWAYS -> R.color.dkRoadContextExpresswayColor
}

internal fun TimelineRoadContext.getTitleResId() = when (this) {
    TimelineRoadContext.HEAVY_URBAN_TRAFFIC -> "dk_timeline_road_context_heavy_urban_traffic"
    TimelineRoadContext.CITY -> "dk_timeline_road_context_city"
    TimelineRoadContext.SUBURBAN -> "dk_timeline_road_context_suburban"
    TimelineRoadContext.EXPRESSWAYS -> "dk_timeline_road_context_expressways"
}