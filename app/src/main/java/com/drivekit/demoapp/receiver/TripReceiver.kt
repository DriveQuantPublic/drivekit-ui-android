package com.drivekit.demoapp.receiver

import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import com.drivekit.demoapp.DriveKitDemoApplication.Companion.showNotification
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.notification.controller.NotificationManager
import com.drivekit.demoapp.notification.enum.NotificationType
import com.drivekit.demoapp.notification.enum.TripCancellationReason
import com.drivekit.demoapp.utils.getImmutableFlag
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.tripanalysis.entity.PostGeneric
import com.drivequant.drivekit.tripanalysis.entity.PostGenericResponse
import com.drivequant.drivekit.tripanalysis.receiver.TripAnalysedReceiver
import com.drivequant.drivekit.tripanalysis.service.recorder.CancelTrip
import java.util.*

internal class TripReceiver : TripAnalysedReceiver() {

    override fun onTripReceived(
        context: Context,
        post: PostGeneric,
        response: PostGenericResponse) {
        // TODO remove
        var messageResId = R.string.notif_trip_finished
        response.itineraryStatistics?.let {
            if (it.transportationMode == TransportationMode.TRAIN.value) {
                messageResId = R.string.notif_trip_train_detected
            }
        }
        showNotification(context, context.getString(messageResId))
        //TODO end remove
        var errorCode = 0
        if (response.isTripValid()) {
            if (response.safety != null && response.safety!!.safetyScore > 10
                || response.ecoDriving != null && response.ecoDriving!!.score > 10
                || response.driverDistraction != null && response.driverDistraction!!.score > 10) {
                errorCode = 1
            }
            manageTrip(context, response, errorCode)
        } else {
            val intent = Intent(context, DashboardActivity::class.java)
            val pendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(
                    Random().nextInt(Int.MAX_VALUE),
                    getImmutableFlag()
                )
            NotificationManager.sendNotification(context, NotificationType.TRIP_TOO_SHORT, pendingIntent, "", "")
        }
    }

    override fun onTripCancelled(context: Context, status: CancelTrip) {
        when (status) {
            CancelTrip.NO_GPS_DATA -> R.string.notif_trip_cancelled_no_gps_data
            CancelTrip.NO_BEACON -> R.string.notif_trip_cancelled_no_beacon
            else -> R.string.trip_cancelled_reset
        }.let { messageResId ->
            showNotification(context, context.getString(messageResId))

        }
        when (status) {
            CancelTrip.HIGHSPEED -> TripCancellationReason.HIGH_SPEED
            CancelTrip.NO_BEACON -> TripCancellationReason.NO_BEACON
            CancelTrip.NO_GPS_DATA -> TripCancellationReason.NO_GPS_POINT
            CancelTrip.USER,
            CancelTrip.NO_SPEED,
            CancelTrip.MISSING_CONFIGURATION,
            CancelTrip.RESET,
            CancelTrip.BEACON_NO_SPEED -> null
        }?.let {
            NotificationManager.sendNotification(context, NotificationType.TRIP_CANCELLED(it), null, null, null)
        }
    }

    fun manageTrip(context: Context, response: PostGenericResponse, errorCode: Int) {

    }
}

fun PostGenericResponse.isTripValid() =
    this.comments.map{ it.errorCode == 0 }.isNotEmpty()
            && this.itineraryStatistics != null
            && this.itineraryStatistics!!.distance > 0