package com.drivequant.drivekit.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import com.drivequant.drivekit.ui.tripdetail.viewmodel.HeaderDay
import com.drivequant.drivekit.ui.trips.viewmodel.TripData
import com.drivequant.drivekit.ui.trips.viewmodel.TripInfo
import java.io.Serializable

class TripsViewConfig(
    context: Context,
    val tripData: TripData = TripData.SAFETY,
    val tripInfo: TripInfo = TripInfo.SAFETY,
    val headerDay: HeaderDay = HeaderDay.DISTANCE_DURATION,
    val dayTripDescendingOrder: Boolean = true,
    val primaryFont: Int = R.font.roboto_regular,
    val viewTitleText: String =  context.getString(R.string.dk_trips_list_title),
    val noTripsRecordedText: String = context.getString(R.string.dk_no_trips_recorded),
    val noTripsRecordedDrawable: Int = R.drawable.dk_no_trips_recorded,
    val failedToSyncTrips: String = context.getString(R.string.dk_failed_to_sync_trips),
    val primaryColor : Int = ContextCompat.getColor(context, R.color.dkPrimaryColor),
    val secondaryColor: Int = ContextCompat.getColor(context, R.color.dkSecondaryColor),
    val okText: String = context.getString(R.string.dk_ok)
    ): Serializable
