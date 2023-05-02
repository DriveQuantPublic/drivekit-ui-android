package com.drivequant.drivekit.ui.drivingconditions.component.context.card

import android.content.Context
import androidx.annotation.ColorRes
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCardItem
import com.drivequant.drivekit.common.ui.extension.capitalizeFirstLetter
import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.ui.R
import kotlin.math.roundToInt

internal class RoadContextCard(
    private val context: Context,
    private val totalDistance: Double,
    private val roadContexts: Map<RoadContext, DKDriverTimeline.DKRoadContextItem>
) : BaseContextCard() {

    private var items: List<DKContextCardItem>? = null

    override fun getItems(): List<DKContextCardItem> {
        this.items?.let {
            return it
        }
        val factor = if (totalDistance < 10) 10.0 else 1.0
        val heavyUrbanDistance = ((totalDistance * factor).roundToInt() - roadContexts.map { (roadContext, roadContextItem) ->
            if (roadContext != RoadContext.HEAVY_URBAN_TRAFFIC) {
                (roadContextItem.distance * factor).roundToInt()
            } else {
                0
            }
        }.sum()) / factor
        val cards = listOf(RoadContext.HEAVY_URBAN_TRAFFIC, RoadContext.CITY, RoadContext.SUBURBAN, RoadContext.EXPRESSWAYS).mapNotNull { roadContext ->
            val distance: Double? = if (roadContext == RoadContext.HEAVY_URBAN_TRAFFIC) {
                heavyUrbanDistance
            } else {
                roadContexts[roadContext]?.distance
            }
            distance?.let {
                if (it > 0) {
                    getContextCardItem(
                        roadContext.getTitle(context),
                        roadContext.getColor(),
                        it,
                        totalDistance,
                        UnitKind.KILOMETER
                    )
                } else {
                    null
                }
            }
        }
        return cards.also {
            this.items = it
        }
    }

    override fun getTitle(context: Context): String {
        val expresswaysDistance = getDistance(RoadContext.EXPRESSWAYS, roadContexts)
        val suburbanDistance = getDistance(RoadContext.SUBURBAN, roadContexts)
        val cityDistance = this.totalDistance - expresswaysDistance - suburbanDistance
        return when (max(cityDistance, expresswaysDistance, suburbanDistance)) {
            cityDistance -> R.string.dk_driverdata_drivingconditions_main_city
            expresswaysDistance -> R.string.dk_driverdata_drivingconditions_main_expressways
            suburbanDistance -> R.string.dk_driverdata_drivingconditions_main_suburban
            else -> 0
        }.let {
            if (it == 0) {
                ""
            } else {
                context.getString(it)
            }
        }
    }
}

private fun RoadContext.getTitle(context: Context): String = when (this) {
    RoadContext.CITY -> com.drivequant.drivekit.common.ui.R.string.dk_common_driving_context_city
    RoadContext.EXPRESSWAYS -> com.drivequant.drivekit.common.ui.R.string.dk_common_driving_context_fastlane
    RoadContext.HEAVY_URBAN_TRAFFIC -> com.drivequant.drivekit.common.ui.R.string.dk_common_driving_context_city_dense
    RoadContext.SUBURBAN -> com.drivequant.drivekit.common.ui.R.string.dk_common_driving_context_external
    RoadContext.TRAFFIC_JAM -> null
}?.let {
    context.getString(it).capitalizeFirstLetter()
} ?: ""

@ColorRes
private fun RoadContext.getColor(): Int = when (this) {
    RoadContext.HEAVY_URBAN_TRAFFIC -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor1
    RoadContext.CITY -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor2
    RoadContext.SUBURBAN -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor3
    RoadContext.EXPRESSWAYS -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor4
    RoadContext.TRAFFIC_JAM -> throw IllegalArgumentException()
}
