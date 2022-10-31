package com.drivequant.drivekit.timeline.ui.component.roadcontext

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.convertToString

import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.roadcontext.enum.TimelineRoadContext

class RoadContextViewModel : ViewModel() {

    lateinit var distanceByContext: Map<TimelineRoadContext, Double>

    private var distance = 0.0

    fun configure(distanceByContext: Map<TimelineRoadContext, Double>) {
        this.distanceByContext = distanceByContext
        distanceByContext.forEach {
            distance += it.value
        }
    }

    fun shouldShowEmptyViewContainer() = distanceByContext.isEmpty()

    fun totalCalculatedDistance(context: Context): String { // TODO
        return DKDataFormatter.formatMeterDistanceInKm(
            context = context,
            distance = distance * 1000,
            minDistanceToRemoveFractions = 10.0
        ).convertToString()
    }

    fun getRoadContextPercent(roadContext: TimelineRoadContext) =
        computeRoadContextPercent(distanceByContext[roadContext]).toFloat()

    private fun computeRoadContextPercent(distanceByRoadContext: DistanceByRoadContext): Double {
        if (distance != 0.0) {
            return (distanceByRoadContext.distances.sum() / distance) * 100
        }
        return 0.0
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