package com.drivequant.drivekit.ui.drivingconditions.component.context.card

import android.content.Context
import androidx.annotation.ColorRes
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCardItem
import com.drivequant.drivekit.common.ui.extension.capitalizeFirstLetter
import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.ui.R

internal class RoadContextCard(
    private val context: Context,
    private val roadContexts: Map<RoadContext, DKDriverTimeline.DKRoadContextItem>
) : BaseContextCard() {

    private var items: List<DKContextCardItem>? = null

    override fun getItems(): List<DKContextCardItem> {
        this.items?.let {
            return it
        }
        val totalDistance = roadContexts.map { (roadContext, roadContextItem) ->
            if (roadContext != RoadContext.TRAFFIC_JAM) {
                roadContextItem.distance
            } else {
                0.0
            }
        }.sum()
        val cards = listOf(RoadContext.HEAVY_URBAN_TRAFFIC, RoadContext.CITY, RoadContext.SUBURBAN, RoadContext.EXPRESSWAYS).mapNotNull { roadContext ->
            roadContexts[roadContext]?.distance?.let { distance ->
                if (distance > 0) {
                    getContextCardItem(
                        roadContext.getTitle(context),
                        roadContext.getColor(),
                        distance,
                        totalDistance
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
        val cityDistance = getDistance(RoadContext.CITY, roadContexts) + getDistance(
            RoadContext.HEAVY_URBAN_TRAFFIC, roadContexts)
        val expresswaysDistance = getDistance(RoadContext.EXPRESSWAYS, roadContexts)
        val suburbanDistance = getDistance(RoadContext.SUBURBAN, roadContexts)
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
