package com.drivekit.demoapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.support.v4.content.LocalBroadcastManager
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.entity.TripNotification
import com.drivequant.drivekit.tripanalysis.entity.TripPoint
import com.drivequant.drivekit.tripanalysis.service.recorder.StartMode
import com.drivekit.demoapp.receiver.TripReceiver
import com.drivekit.drivekitdemoapp.R

class DriveKitDemoApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        configureDriveKit()
        registerReceiver()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val channel = NotificationChannel("notif_channel", name, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundNotification(): TripNotification{
        val notification = TripNotification(
            getString(R.string.app_name),
            getString(R.string.trip_started),
            R.drawable.ic_launcher_background)
        notification.enableCancel = true
        notification.cancel = getString(R.string.cancel_trip)
        notification.cancelIconId = R.drawable.ic_launcher_background
        return notification
    }

    private fun configureDriveKit(){
        DriveKit.initialize(this)
        DriveKitTripAnalysis.initialize(createForegroundNotification(), object: TripListener {
            override fun tripStarted(startMode : StartMode) {
                // Call when a trip start
            }
            override fun tripPoint(tripPoint : TripPoint) {
                // Call for each location registered during a trip
            }

            override fun tripSavedForRepost() {
            }

            override fun beaconDetected() {
            }
        })
        DriveKitDriverData.initialize()
        // TODO: Push you api key here
        //DriveKit.setApiKey("YOUR_API_KEY")
    }

    private fun registerReceiver(){
        val receiver = TripReceiver()
        val filter = IntentFilter("com.drivequant.sdk.TRIP_ANALYSED")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }
}