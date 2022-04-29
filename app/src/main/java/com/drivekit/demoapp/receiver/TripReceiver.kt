package com.drivekit.demoapp.receiver

import android.content.Context
import com.drivekit.demoapp.DriveKitDemoApplication.Companion.showNotification
import com.drivequant.drivekit.tripanalysis.entity.PostGeneric
import com.drivequant.drivekit.tripanalysis.entity.PostGenericResponse
import com.drivequant.drivekit.tripanalysis.receiver.TripAnalysedReceiver
import com.drivequant.drivekit.tripanalysis.service.recorder.CancelTrip
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.databaseutils.entity.TransportationMode

class TripReceiver : TripAnalysedReceiver() {

    override fun onTripReceived(
        context: Context,
        post: PostGeneric,
        response: PostGenericResponse) {
        var messageResId = R.string.notif_trip_finished
        response.itineraryStatistics?.let {
            if (it.transportationMode == TransportationMode.TRAIN.value) {
                messageResId = R.string.notif_trip_train_detected
            }
        }
        showNotification(context, context.getString(messageResId))
    }

    override fun onTripCancelled(context: Context, status: CancelTrip) {
        when (status) {
            CancelTrip.NO_GPS_DATA -> R.string.notif_trip_cancelled_no_gps_data
            CancelTrip.NO_BEACON -> R.string.notif_trip_cancelled_no_beacon
            else -> R.string.trip_cancelled_reset
        }.let { messageResId ->
            showNotification(context, context.getString(messageResId))
        }
    }
}