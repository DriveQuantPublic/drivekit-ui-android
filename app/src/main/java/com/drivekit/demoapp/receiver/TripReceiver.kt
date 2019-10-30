package com.drivekit.demoapp.receiver

import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.drivequant.drivekit.tripanalysis.entity.PostGeneric
import com.drivequant.drivekit.tripanalysis.entity.PostGenericResponse
import com.drivequant.drivekit.tripanalysis.receiver.TripAnalysedReceiver
import com.drivequant.drivekit.tripanalysis.service.recorder.CancelTrip
import com.drivekit.drivekitdemoapp.R
import java.util.*

class TripReceiver : TripAnalysedReceiver() {

    override fun onTripReceived(context: Context, post: PostGeneric, response: PostGenericResponse) {
        var status = false
        for (comment in response.comments){
            if (comment.errorCode == 0) {
                status = true
            }
            Log.i(context.getString(R.string.app_name), comment.comment)
        }
        var message = "Failed to analyzed trip"
        if (status){
            message = context.getString(R.string.trip_finished)
        }
        showNotification(context, message)
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

    private fun showNotification(context: Context, message: String){
        val builder = NotificationCompat.Builder(context, "notif_channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(Integer.MAX_VALUE), builder.build())
    }
}