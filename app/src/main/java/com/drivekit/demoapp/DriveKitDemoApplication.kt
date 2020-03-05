package com.drivekit.demoapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.support.v4.app.NotificationCompat
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
import com.drivequant.drivekit.core.DriveKitSharedPreferencesUtils
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import java.util.*

class DriveKitDemoApplication: Application() {
    companion object {
        fun showNotification(context: Context, message: String){
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
                showNotification(applicationContext, getString(R.string.trip_save_for_repost))
            }

            override fun beaconDetected() {
            }
        })
        DriveKitDriverData.initialize()
        DriveKitVehicle.initialize()
        // TODO: Push you api key here
        DriveKit.setApiKey("Your API key here")

        initFirstLaunch()
    }

    private fun initFirstLaunch(){
        val firstLaunch = DriveKitSharedPreferencesUtils.getBoolean("dk_demo_firstLaunch", true)
        if (firstLaunch){
            DriveKitTripAnalysis.activateAutoStart(true)
            DriveKit.enableLogging("/DriveKit")
            DriveKitTripAnalysis.setStopTimeOut(4*60)
            DriveKitSharedPreferencesUtils.setBoolean("dk_demo_firstLaunch", false)
        }
    }

    private fun registerReceiver(){
        val receiver = TripReceiver()
        val filter = IntentFilter("com.drivequant.sdk.TRIP_ANALYSED")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }
}