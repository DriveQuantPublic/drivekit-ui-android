package com.drivequant.drivekit.timeline.ui.component.roadcontext

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.convertToString

import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.timeline.RoadContextItemData

data class DistanceByRoadContext(
    val roadContext: RoadContext,
    val distances: List<Double>
)

class RoadContextViewModel(
    private val roadContexts: List<RoadContextItemData>
) : ViewModel() {

    private lateinit var heavyUranDistance: DistanceByRoadContext
    private lateinit var cityDistance: DistanceByRoadContext
    private lateinit var subUrbanDistance: DistanceByRoadContext
    private lateinit var expressWaysDistance: DistanceByRoadContext

    var totalDistance = 0.0

    init {
        roadContexts.forEach {
            when (it.type) {
                RoadContext.HEAVY_URBAN_TRAFFIC -> heavyUranDistance =
                    DistanceByRoadContext(it.type, it.distance)
                RoadContext.CITY -> cityDistance = DistanceByRoadContext(it.type, it.distance)
                RoadContext.EXPRESSWAYS -> expressWaysDistance =
                    DistanceByRoadContext(it.type, it.distance)
                RoadContext.SUBURBAN -> subUrbanDistance =
                    DistanceByRoadContext(it.type, it.distance)
                else -> throw IllegalArgumentException("$this road context is not supported.")
            }
        }
    }

    fun getRoadContextList() = roadContexts.map { it.type }

    fun shouldShowEmptyViewContainer() = getRoadContextList().isEmpty()

    fun totalCalculatedDistance(context: Context): String {
        roadContexts.map { it.distance }.forEach {
            totalDistance += it.sum()
        }
        return DKDataFormatter.formatMeterDistanceInKm(
            context = context,
            distance = totalDistance * 1000,
            minDistanceToRemoveFractions = 10.0
        ).convertToString()
    }

    fun getRoadContextPercent(roadContext: RoadContext) = when (roadContext) {
        RoadContext.EXPRESSWAYS -> expressWaysDistance
        RoadContext.HEAVY_URBAN_TRAFFIC -> heavyUranDistance
        RoadContext.CITY -> cityDistance
        RoadContext.SUBURBAN -> subUrbanDistance
        else -> null
    }?.let {
        return computeRoadContextPercent(it).toFloat()
    } ?: 0f

    private fun computeRoadContextPercent(distanceByRoadContext: DistanceByRoadContext): Double {
        if (totalDistance != 0.0) {
            return (distanceByRoadContext.distances.sum() / totalDistance) * 100
        }
        return 0.0
    }

    @Suppress("UNCHECKED_CAST")
    class RoadContextViewModelFactory(private val roadContexts: List<RoadContextItemData>) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RoadContextViewModel(roadContexts) as T
        }
    }
}

fun RoadContext.getColorResId(): Int = when (this) {
    RoadContext.HEAVY_URBAN_TRAFFIC -> R.color.dkRoadContextUrbainDenseColor
    RoadContext.CITY -> R.color.dkRoadContextUrbainFluidColor
    RoadContext.SUBURBAN -> R.color.dkRoadContextSubUrbainColor
    RoadContext.EXPRESSWAYS -> R.color.dkRoadContextExpresswayColor
    else -> throw IllegalArgumentException("$this road context is not supported.")
}

fun RoadContext.getTitleResId() = when (this) {
    RoadContext.HEAVY_URBAN_TRAFFIC -> "dk_timeline_road_context_heavy_urban_traffic"
    RoadContext.CITY -> "dk_timeline_road_context_city"
    RoadContext.SUBURBAN -> "dk_timeline_road_context_suburban"
    RoadContext.EXPRESSWAYS -> "dk_timeline_road_context_expressways"
    else -> throw IllegalArgumentException("$this road context is not supported.")
}