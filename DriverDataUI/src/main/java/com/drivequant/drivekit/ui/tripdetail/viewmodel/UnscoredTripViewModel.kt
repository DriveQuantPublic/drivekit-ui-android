package com.drivequant.drivekit.ui.tripdetail.viewmodel

import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.TripDetailViewConfig
import com.drivequant.drivekit.ui.extension.getOrComputeStartDate
import java.util.*

class UnscoredTripViewModel(
    private val trip: Trip?) {

    fun getDuration(): Double? = trip?.tripStatistics?.duration

    fun getStartDate(): Date? = trip?.getOrComputeStartDate()

    fun getEndDate(): Date? = trip?.endDate

    fun getNoScoreTripMessage(): Int = R.string.dk_driverdata_trip_detail_no_score
}