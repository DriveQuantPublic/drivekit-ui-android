package com.drivequant.drivekit.ui.trips.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.extension.computeTotalDistance
import com.drivequant.drivekit.ui.extension.computeTotalDuration

enum class HeaderSummary{
    NONE, DISTANCE, DURATION, DURATION_DISTANCE, DISTANCE_DURATION;
    //TODO Verify
    fun text(context: Context, trips: List<Trip>?) : String? {
        val separator = " | "
        return when (this) {
            DISTANCE -> DKDataFormatter.formatDistance(context, trips?.computeTotalDistance())
            DURATION -> DKDataFormatter.formatDuration(context, trips?.computeTotalDuration()!!)
            DURATION_DISTANCE -> DKDataFormatter.formatDuration(context, trips?.computeTotalDuration()!!)
                .plus(separator)
                .plus(DKDataFormatter.formatDistance(context, trips?.computeTotalDistance()))
            DISTANCE_DURATION -> DKDataFormatter.formatDistance(context, trips?.computeTotalDistance())
                .plus(separator)
                .plus(DKDataFormatter.formatDuration(context, trips?.computeTotalDuration()!!))
            NONE -> null
        }
    }

    fun text(context: Context, trip: Trip) : String?{
        val list = mutableListOf(trip)
        return text(context, list)
    }
}