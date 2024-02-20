package com.drivequant.drivekit.ui.tripdetail.viewmodel

import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.extension.getOrComputeStartDate
import java.io.Serializable
import java.util.*

class UnscoredTripViewModel(
    private val trip: Trip?) : Serializable {

    fun getDuration(): Double? = trip?.tripStatistics?.duration

    fun getStartDate(): Date? = trip?.getOrComputeStartDate()

    fun getEndDate(): Date? = trip?.endDate
}