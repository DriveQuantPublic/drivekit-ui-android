package com.drivequant.drivekit.timeline.ui.component.roadcontext

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.databaseutils.entity.RoadContext

internal class RoadContextViewModel : ViewModel() {

    private var distance: Double = 0.0
    private var distanceByContext: Pair<RoadContext, Double> = Pair(RoadContext.CITY, 0.0)

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
    HEAVY_URBAN_TRAFFIC, CITY, SUBURBAN, EXPRESSWAY
}