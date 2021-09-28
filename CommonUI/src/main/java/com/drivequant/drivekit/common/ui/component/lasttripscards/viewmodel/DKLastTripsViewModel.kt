package com.drivequant.drivekit.common.ui.component.lasttripscards.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.common.ui.extension.capitalizeFirstLetter
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.utils.DKDatePattern

internal class DKLastTripsViewModel(
    val tripData: TripData,
    val headerDay: HeaderDay,
    val trip: DKTripListItem) {

    fun getTripCardTitle(context: Context) =
        "${trip.getEndDate().formatDate(DKDatePattern.WEEK_LETTER).capitalizeFirstLetter()} | ${
            headerDay.text(
                context,
                trip
            )
        }"
}