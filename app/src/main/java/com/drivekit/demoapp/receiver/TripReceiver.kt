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
            Log.i("DriveKit Demo App", comment.comment)
        }
        var message = "Failed to analyzed trip"
        if (status){
            message = "A new trip has been analyzed"
        }
        showNotification(context, message)
    }

    override fun onTripCancelled(context: Context, status: CancelTrip) {
        val message = "Trip cancelled : " + status.name
        showNotification(context, message)
    }

    private fun showNotification(context: Context, message: String){
        val builder = NotificationCompat.Builder(context, "notif_channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("DriveKit")
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