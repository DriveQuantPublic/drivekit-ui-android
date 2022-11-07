package com.drivequant.drivekit.timeline.ui.component.roadcontext

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.convertToString

import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.TimelineRoadContext

class RoadContextViewModel : ViewModel() {

    var changeObserver: MutableLiveData<Any> = MutableLiveData()

    var distanceByContext = linkedMapOf<TimelineRoadContext, Double>()

    private var distance = 0.0

    fun configure(distanceByContext: LinkedHashMap<TimelineRoadContext, Double>) {
        this.distanceByContext = distanceByContext
        distance = 0.0
        distanceByContext.forEach {
            distance += it.value
        }
        changeObserver.postValue(Any())
    }

    fun shouldShowEmptyViewContainer() = distanceByContext.isEmpty()

    fun formatDistanceInKm(context: Context): String {
        return DKDataFormatter.formatMeterDistanceInKm(
            context = context,
            distance = distance * 1000,
            minDistanceToRemoveFractions = 10.0
        ).convertToString()
    }

    fun getPercent(roadContext: TimelineRoadContext): Double {
        val percent = if (distanceByContext.isEmpty()) {
            0.0
        } else {
            (distanceByContext[roadContext]?.div(distance)!!*1000) ?: 0.0 // TODO
        }
        return percent
    }

    @Suppress("UNCHECKED_CAST")
    class RoadContextViewModelFactory : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RoadContextViewModel() as T
        }
    }
}

fun TimelineRoadContext.getColorResId() = when (this) {
    TimelineRoadContext.HEAVY_URBAN_TRAFFIC -> R.color.dkRoadContextUrbainDenseColor
    TimelineRoadContext.CITY -> R.color.dkRoadContextUrbainFluidColor
    TimelineRoadContext.SUBURBAN -> R.color.dkRoadContextSubUrbainColor
    TimelineRoadContext.EXPRESSWAYS -> R.color.dkRoadContextExpresswayColor
}

fun TimelineRoadContext.getTitleResId() = when (this) {
    TimelineRoadContext.HEAVY_URBAN_TRAFFIC -> "dk_timeline_road_context_heavy_urban_traffic"
    TimelineRoadContext.CITY -> "dk_timeline_road_context_city"
    TimelineRoadContext.SUBURBAN -> "dk_timeline_road_context_suburban"
    TimelineRoadContext.EXPRESSWAYS -> "dk_timeline_road_context_expressways"
}