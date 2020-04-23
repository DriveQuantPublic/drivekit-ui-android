package com.drivekit.demoapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.drivekit.demoapp.config.ColorConfig
import com.drivekit.demoapp.config.FontConfig
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.entity.TripNotification
import com.drivequant.drivekit.tripanalysis.entity.TripPoint
import com.drivequant.drivekit.tripanalysis.service.recorder.StartMode
import com.drivekit.demoapp.receiver.TripReceiver
import com.drivekit.demoapp.vehicle.DemoCustomField
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.listener.ContentMail
import com.drivequant.drivekit.core.DriveKitSharedPreferencesUtils
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.permissionsutils.PermissionUtilsUI
import com.drivequant.drivekit.permissionsutils.permissions.model.ContactType
import com.drivequant.drivekit.tripanalysis.service.recorder.State
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.listener.VehiclePickerExtraStepListener
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.GroupField
import com.facebook.stetho.Stetho
import java.security.Permission
import java.util.*

class DriveKitDemoApplication: Application(), ContentMail, VehiclePickerExtraStepListener {
    companion object {
        fun showNotification(context: Context, message: String){
            val builder = NotificationCompat.Builder(context, "notif_channel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(Random().nextInt(Integer.MAX_VALUE), builder.build())
        }
    }

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        createNotificationChannel()
        configureDriveKit()
        registerReceiver()

        DriveKitUI.initialize()
        //DriveKitUI.initialize(fonts = FontConfig(), colors = ColorConfig(this))
        DriverDataUI.initialize()
        DriverAchievementUI.initialize()
        DriveKitVehicleUI.initialize()
        PermissionUtilsUI.initialize()
        PermissionUtilsUI.configureBluetooth(true)
        PermissionUtilsUI.configureDiagnosisLogs(true)
        PermissionUtilsUI.configureContactType(ContactType.EMAIL(object : ContentMail {
            override fun getBccRecipients(): List<String> {
                return listOf("support@drivequant.com")
            }

            override fun getMailBody(): String {
               return if(true) {
                    "MAIF \n "
                } else {
                   "OTHER"
               }
            }

            override fun getRecipients(): List<String> {
                return listOf()
            }

            override fun getSubject(): String = getString(R.string.app_name) + " - " + getString(R.string.ask_for_request)
            override fun overrideMailBodyContent(): Boolean = false

        }))
        PermissionUtilsUI.configureLogPathFile("/DQ-demo-test/")
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

            override fun sdkStateChanged(state: State) { }
        })
        DriveKitDriverData.initialize()

        // TODO: Push you api key here
        DriveKit.setApiKey("Your API key here")

        DriveKitVehicleUI.addCustomFieldsToGroup(GroupField.GENERAL, listOf(DemoCustomField()))
        DriveKitVehicleUI.configureBeaconDetailEmail(this)
        DriveKitVehicleUI.configureVehiclePickerExtraStep(this)
        DriveKitTripAnalysis.setVehiclesConfigTakeover(true)
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

    override fun getRecipients(): List<String> {
        return listOf("recipient1@email.com")
    }

    override fun getBccRecipients(): List<String> {
        return listOf("bcc_test1@email.com")
    }

    override fun getSubject(): String {
        return "Mock subject"
    }

    override fun getMailBody(): String {
        return "Mock mail body in DriveKitDemoApplication.kt"
    }

    override fun overrideMailBodyContent(): Boolean = true

    override fun onVehiclePickerFinished(vehicleId: String) {
        Log.i("Vehicle Picker", "New vehicle created : $vehicleId")
    }
}