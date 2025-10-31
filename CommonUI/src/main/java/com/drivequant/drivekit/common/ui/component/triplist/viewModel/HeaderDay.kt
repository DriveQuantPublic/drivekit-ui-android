package com.drivequant.drivekit.common.ui.component.triplist.viewModel

import android.content.Context
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.extension.computeCeilDuration
import com.drivequant.drivekit.common.ui.component.triplist.extension.computeTotalDistance
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.Meter
import com.drivequant.drivekit.common.ui.utils.convertToString

enum class HeaderDay {
    NONE, DISTANCE, DURATION, DURATION_DISTANCE, DISTANCE_DURATION;

    fun text(context: Context, trip: DKTripListItem): String? = text(context, listOf(trip))

    fun text(context: Context, trips: List<DKTripListItem>?): String? {
        val separator = " | "
        return trips?.let {
            when (this) {
                DISTANCE -> DKDataFormatter.formatInKmOrMile(context, Meter(it.computeTotalDistance())).convertToString()
                DURATION -> DKDataFormatter.formatDuration(context, it.computeCeilDuration())
                    .convertToString()
                DURATION_DISTANCE -> DKDataFormatter.formatDuration(context, it.computeCeilDuration()).convertToString()
                    .plus(separator)
                    .plus(DKDataFormatter.formatInKmOrMile(context,
                        Meter(it.computeTotalDistance())).convertToString())
                DISTANCE_DURATION -> DKDataFormatter.formatInKmOrMile(context, Meter(it.computeTotalDistance())).convertToString()
                    .plus(separator)
                    .plus(DKDataFormatter.formatDuration(context, it.computeCeilDuration())
                        .convertToString())
                NONE -> null
            }
        } ?: run {
            null
        }
    }
}

interface DKHeader {
    fun customTripDetailHeader(context: Context, trip: DKTripListItem): String? = null
    fun customTripListHeader(context: Context, trips: List<DKTripListItem>?): String? = null
    fun tripDetailHeader(): HeaderDay =
        HeaderDay.DURATION_DISTANCE
    fun tripListHeader(): HeaderDay =
        HeaderDay.DURATION_DISTANCE
}