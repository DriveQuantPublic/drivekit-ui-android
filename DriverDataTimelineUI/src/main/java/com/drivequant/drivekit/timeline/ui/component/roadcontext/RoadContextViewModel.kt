package com.drivequant.drivekit.timeline.ui.component.roadcontext

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.timeline.ui.R

internal class RoadContextViewModel : ViewModel() {

    private var distance: Double = 0.0
    private var distanceByContext: Pair<RoadContext, Double> = Pair(RoadContext.CITY, 0.0)

    fun getRoadContextList() = listOf<RoadContextType>(
        RoadContextType.SUBURBAN,
        RoadContextType.HEAVY_URBAN_TRAFFIC,
        RoadContextType.EXPRESSWAY
    )

    fun update(distanceByContext: Pair<RoadContext, Double>, distance: Double) {
        TODO()
    }

    fun getTotalDistance(): Int {
        TODO()
    }

    fun getActiveContextNumber(): Int {
        TODO()
    }

    fun getRoadContextPercent(roadContext: RoadContextType) = when (roadContext) {
        RoadContextType.HEAVY_URBAN_TRAFFIC -> getHeavyUrbanTrafficPercent()
        RoadContextType.CITY -> getCityPercent()
        RoadContextType.SUBURBAN -> getSuburbanPercent()
        RoadContextType.EXPRESSWAY -> getExpressWaysPercent()
    }

    private fun getHeavyUrbanTrafficPercent(): Double {
        TODO()
    }

    private fun getCityPercent(): Double {
        TODO()
    }

    private fun getSuburbanPercent(): Double {
        TODO()
    }

    private fun getExpressWaysPercent(): Double {
        TODO()
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