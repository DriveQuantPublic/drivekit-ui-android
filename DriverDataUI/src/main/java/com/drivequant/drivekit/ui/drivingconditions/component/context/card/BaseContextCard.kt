package com.drivequant.drivekit.ui.drivingconditions.component.context.card

import android.content.Context
import androidx.annotation.ColorRes
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCard
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCardItem
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.Meter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline

internal abstract class BaseContextCard : DKContextCard {

    companion object {
        const val LESS_THAN_BOUND = 0.45 / (1 - 0.45)
        const val GREATER_THAN_BOUND = 0.55 / (1 - 0.55)
    }

    override fun getEmptyDataDescription(context: Context): String = ""

    fun getContextCardItem(
        title: String,
        @ColorRes color: Int,
        itemValue: Double,
        totalItemsValue: Double,
        kind: UnitKind
    ): DKContextCardItem {
        return object : DKContextCardItem {
            override fun getColorResId(): Int = color
            override fun getTitle(context: Context): String = title
            override fun getSubtitle(context: Context): String = when (kind) {
                UnitKind.TRIP -> "${DKDataFormatter.formatNumber(itemValue, 0)} ${context.resources.getQuantityString(com.drivequant.drivekit.common.ui.R.plurals.trip_plural, itemValue.toInt())}"
                UnitKind.KILOMETER -> DKDataFormatter.formatInKmOrMile(
                    context,
                    Meter(itemValue * 1000),
                    true,
                    if (totalItemsValue < 10) 10.0 else 0.0
                ).convertToString()
            }

            override fun getPercent(): Double = itemValue / totalItemsValue * 100
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

    fun <T> getMaxKey(map: Map<T, Number>): T? {
        var maxValue: Double? = null
        var maxKey: T? = null
        for ((key, value) in map) {
            val doubleValue = value.toDouble()
            if (doubleValue > 0 && (maxValue == null || doubleValue > maxValue)) {
                maxValue = doubleValue
                maxKey = key
            }
        }
        return maxKey
    }

}

internal enum class UnitKind {
    KILOMETER, TRIP
}
