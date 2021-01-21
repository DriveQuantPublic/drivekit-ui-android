package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.extension.computeCeilDuration
import com.drivequant.drivekit.ui.extension.computeTotalDistance

enum class HeaderDay {
    NONE, DISTANCE, DURATION, DURATION_DISTANCE, DISTANCE_DURATION;

    fun text(context: Context, trip: Trip): String? = text(context, listOf(trip))

    fun text(context: Context, trips: List<Trip>?): String? {
        val separator = " | "
        return trips?.let {
            when (this) {
                DISTANCE -> DKDataFormatter.formatMeterDistanceInKm(context, it.computeTotalDistance())
                DURATION -> DKDataFormatter.formatDuration(context, it.computeCeilDuration())
                DURATION_DISTANCE -> DKDataFormatter.formatDuration(
                    context,
                    it.computeCeilDuration()
                )
                    .plus(separator)
                    .plus(DKDataFormatter.formatMeterDistanceInKm(context, it.computeTotalDistance()))
                DISTANCE_DURATION -> DKDataFormatter.formatMeterDistanceInKm(
                    context,
                    it.computeTotalDistance()
                )
                    .plus(separator)
                    .plus(DKDataFormatter.formatDuration(context, it.computeCeilDuration()))
                NONE -> null
            }
        } ?: run {
            null
        }
    }
}

interface DKHeader {
    fun customTripDetailHeader(context: Context, trip: Trip): String? = null
    fun customTripListHeader(context: Context, trips: List<Trip>?): String? = null
    fun tripDetailHeader(): HeaderDay = HeaderDay.DURATION_DISTANCE
    fun tripListHeader(): HeaderDay = HeaderDay.DURATION_DISTANCE
}