package com.drivequant.drivekit.timeline.ui.component.roadcontext

import android.content.Context
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.timeline.ui.R

data class DistanceByRoadContext(
    val roadContextType: RoadContextType,
    val distance :Double
)

internal class RoadContextViewModel: ViewModel() {

    private val distanceByRoadContextType: List<DistanceByRoadContext> = mutableListOf()
    private val distance: Double = 0.0

    fun getRoadContextList() = listOf(
        RoadContextType.SUBURBAN,
        RoadContextType.HEAVY_URBAN_TRAFFIC,
        RoadContextType.EXPRESSWAY,
        RoadContextType.CITY
    )

    fun update(distanceByContext: Pair<RoadContext, Double>, distance: Double) {
        TODO()
    }

    fun totalCalculatedDistance(): Double {
        var totalDistance = 0.0
        for (item in distanceByRoadContextType) {
            if (item.distance > 0) {
                totalDistance += item.distance
            }
        }
        return totalDistance
    }

    fun getActiveContextNumber(): Int {
        var total = 0
        for (item in distanceByRoadContextType) {
            if (item.distance > 0) {
                total += 1
            }
        }
        return total
    }

    fun getRoadContextPercent(roadContext: RoadContextType) = when (roadContext) {
        RoadContextType.HEAVY_URBAN_TRAFFIC -> getHeavyUrbanTrafficPercent()
        RoadContextType.CITY -> getCityPercent()
        RoadContextType.SUBURBAN -> getSuburbanPercent()
        RoadContextType.EXPRESSWAY -> getExpressWaysPercent()
    }

    private fun getHeavyUrbanTrafficPercent(): Double {
        var percentage = 0.0
        for (item in distanceByRoadContextType) {
            if (item.roadContextType == RoadContextType.HEAVY_URBAN_TRAFFIC) {
               percentage = item.distance / totalCalculatedDistance()
            }
        }
        return 40.0
    }

    private fun getCityPercent(): Double {
        var percentage = 0.0
        for (item in distanceByRoadContextType) {
            if (item.roadContextType == RoadContextType.CITY) {
                percentage = item.distance / totalCalculatedDistance()
            }
        }
        return 40.0
    }

    private fun getSuburbanPercent(): Double {
        var percentage = 0.0
        for (item in distanceByRoadContextType) {
            if (item.roadContextType == RoadContextType.SUBURBAN) {
                percentage = item.distance / totalCalculatedDistance()
            }
        }
        return 10.0
    }

    private fun getExpressWaysPercent(): Double {
        var percentage = 0.0
        for (item in distanceByRoadContextType) {
            if (item.roadContextType == RoadContextType.EXPRESSWAY) {
                percentage = item.distance / totalCalculatedDistance()
            }
        }
        return 10.0
    }
}

enum class RoadContextType {
    HEAVY_URBAN_TRAFFIC, CITY, SUBURBAN, EXPRESSWAY;

    fun getColorResId() = when (this) {
        HEAVY_URBAN_TRAFFIC -> R.color.dkRoadContextUrbainDenseColor
        CITY -> R.color.dkRoadContextUrbainFluidColor
        SUBURBAN -> R.color.dkRoadContextSubUrbainColor
        EXPRESSWAY -> R.color.dkRoadContextHighwayColor
    }

    fun getTitleResId() = when (this) {
        HEAVY_URBAN_TRAFFIC -> "dk_road_context_urbain_dense"
        CITY -> "dk_road_context_urbain_fluid"
        SUBURBAN -> "dk_road_context_extra_urbain"
        EXPRESSWAY -> "dk_road_context_highway"
    }
}