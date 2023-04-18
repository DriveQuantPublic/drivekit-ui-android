package com.drivequant.drivekit.ui.drivingconditions.component.context.card

import android.content.Context
import androidx.annotation.ColorRes
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCard
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCardItem
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline

internal abstract class BaseContextCard : DKContextCard {

    companion object {
        const val LESS_THAN_BOUND = 0.45 / (1 - 0.45)
        const val GREATER_THAN_BOUND = 0.55 / (1 - 0.55)
    }

    override fun getEmptyDataDescription(context: Context): String = ""

    fun getContextCardItem(title: String, @ColorRes color: Int, distance: Double, totalDistance: Double): DKContextCardItem {
        return object : DKContextCardItem {
            override fun getColorResId(): Int = color
            override fun getTitle(context: Context): String = title
            override fun getSubtitle(context: Context): String = DKDataFormatter.formatMeterDistanceInKm(context, distance * 1000, true, 100.0).convertToString()
            override fun getPercent(): Double = distance / totalDistance * 100
        }
    }

    fun getDistance(
        roadContext: RoadContext,
        roadContexts: Map<RoadContext, DKDriverTimeline.DKRoadContextItem>
    ): Double = roadContexts[roadContext]?.distance ?: 0.0

    fun max(vararg values: Double?): Double? {
        var max: Double? = null
        for (value in values) {
            if (value != null && (max == null || value > max)) {
                max = value
            }
        }
        return max
    }

    fun <T> getMaxKey(map: Map<T, Double>): T? {
        var maxValue: Double? = null
        var maxKey: T? = null
        for ((key, value) in map) {
            if (value > 0 && (maxValue == null || value > maxValue)) {
                maxValue = value
                maxKey = key
            }
        }
        return maxKey
    }

}
