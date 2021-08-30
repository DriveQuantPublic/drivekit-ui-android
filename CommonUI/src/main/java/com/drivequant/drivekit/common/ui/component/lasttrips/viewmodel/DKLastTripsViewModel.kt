package com.drivequant.drivekit.common.ui.component.lasttrips.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.utils.DKDatePattern

class DKLastTripsViewModel(
    val tripData: TripData,
    val headerDay: HeaderDay,
    val trip: DKTripListItem
) {
    fun getTripCardTitle(context: Context) =
        "${trip.getEndDate().formatDate(DKDatePattern.WEEK_LETTER)} | ${
            headerDay.text(
                context,
                trip
            )
        }"
}