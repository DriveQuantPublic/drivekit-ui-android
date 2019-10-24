package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.extension.computeTotalDistance
import com.drivequant.drivekit.ui.extension.computeTotalDuration
import com.drivequant.drivekit.ui.utils.DistanceUtils
import com.drivequant.drivekit.ui.utils.DurationUtils

enum class HeaderDay {
    DISTANCE, DURATION, DURATION_DISTANCE, DISTANCE_DURATION;

    fun text(context: Context, trips: List<Trip>?) : String? {
        val separator = " | "
        return when (this) {
            DISTANCE -> DistanceUtils().formatDistance(context, trips?.computeTotalDistance())
            DURATION -> DurationUtils().formatDuration(context, trips?.computeTotalDuration())
            DURATION_DISTANCE -> DurationUtils().formatDuration(context, trips?.computeTotalDuration())
                                    .plus(separator)
                                    .plus(DistanceUtils().formatDistance(context, trips?.computeTotalDistance()))
            DISTANCE_DURATION -> DistanceUtils().formatDistance(context, trips?.computeTotalDistance())
                                    .plus(separator)
                                    .plus(DurationUtils().formatDuration(context, trips?.computeTotalDuration()))
        }
    }
}