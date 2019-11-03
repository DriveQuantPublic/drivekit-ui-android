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

    override fun onTripReceived(context: Context, post: PostGeneric, response: PostGenericResponse) {
        var messageResId = R.string.trip_finished
        response.itineraryStatistics?.let {
            if (it.transportationMode == TransportationMode.TRAIN.value){
                messageResId = R.string.train_trip
            }
        }
        showNotification(context, context.getString(messageResId))
    }

    override fun onTripCancelled(context: Context, status: CancelTrip) {
        val messageResId = when (status){
            CancelTrip.USER -> R.string.trip_cancelled_user
            CancelTrip.HIGHSPEED -> R.string.trip_cancelled_highspeed
            CancelTrip.NO_SPEED -> R.string.trip_cancelled_no_speed
            CancelTrip.NO_BEACON -> R.string.trip_cancelled_no_beacon
            CancelTrip.MISSING_CONFIGURATION -> R.string.trip_cancelled_missing_config
            CancelTrip.NO_GPS_DATA -> R.string.trip_cancelled_no_gps_data
            CancelTrip.RESET -> R.string.trip_cancelled_reset
            CancelTrip.BEACON_NO_SPEED -> R.string.trip_cancelled_beacon_no_speed
        }
        showNotification(context, context.getString(messageResId))
    }
}