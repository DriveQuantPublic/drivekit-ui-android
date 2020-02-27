package com.drivequant.drivekit.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import com.drivequant.drivekit.ui.trips.viewmodel.TripData
import java.io.Serializable

class TripsViewConfig(
    context: Context,
    val tripData: TripData = TripData.SAFETY,
    val dayTripDescendingOrder: Boolean = true,
    val viewTitleText: String =  context.getString(R.string.dk_trips_list_title),
    val noTripsRecordedText: String = context.getString(R.string.dk_no_trips_recorded),
    val noTripsRecordedDrawable: Int = R.drawable.dk_no_trips_recorded,
    val failedToSyncTrips: String = context.getString(R.string.dk_failed_to_sync_trips),
    val enableAdviceFeedback: Boolean = false
    ): Serializable
