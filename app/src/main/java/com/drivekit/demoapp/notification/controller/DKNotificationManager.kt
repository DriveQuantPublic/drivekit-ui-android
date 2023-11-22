package com.drivekit.demoapp.notification.controller

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.notification.enum.DKNotificationChannel
import com.drivekit.demoapp.notification.enum.NotificationType
import com.drivekit.demoapp.notification.enum.TripAnalysisError
import com.drivekit.demoapp.receiver.TripReceiver
import com.drivekit.demoapp.utils.WorkerManager
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.deviceconfiguration.DKDeviceConfigurationEvent
import com.drivequant.drivekit.core.deviceconfiguration.DKDeviceConfigurationListener
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.entity.PostGeneric
import com.drivequant.drivekit.tripanalysis.entity.PostGenericResponse
import com.drivequant.drivekit.tripanalysis.service.recorder.CancelTrip
import com.drivequant.drivekit.tripanalysis.service.recorder.StartMode

internal object DKNotificationManager : TripListener, DKDeviceConfigurationListener {

    private const val TRIP_ANALYSIS_NOTIFICATION_KEY = "dk_tripanalysis_notification"
    const val TRIP_DETAIL_NOTIFICATION_KEY = "dk_trip_detail_notification"
    private const val APP_DIAGNOSIS_NOTIFICATION_KEY = "dk_app_diagnosis_notification"

    fun configure() {
        DriveKit.addDeviceConfigurationListener(this)
        DriveKitTripAnalysis.addTripListener(this)
    }

    fun createChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager != null) {
            DKNotificationChannel.values().forEach { channel ->
                if (channel.create() && channel.isEnabled(context)) {
                    createChannel(context, channel)
                }
            }
        }
    }

    fun createChannel(context: Context, channel: DKNotificationChannel) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager != null) {
            if (manager.getNotificationChannel(channel.getChannelId()) != null) {
                deleteChannel(context, channel)
            }
            val notificationChannel = NotificationChannel(
                channel.getChannelId(),
                context.getString(channel.getChannelNameResId()),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(notificationChannel)
        }
    }

    fun deleteChannel(context: Context, channel: DKNotificationChannel) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager != null) {
            manager.deleteNotificationChannel(channel.getChannelId())
        }
    }

    fun sendNotification(
        context: Context,
        notificationType: NotificationType,
        contentIntent: PendingIntent? = null,
        additionalBody: String? = null
    ) {
        notificationType.createNotification(context, contentIntent, additionalBody).let { notification ->
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)?.let { manager ->
                if (notificationType.getChannel().isEnabled(context)) {
                    manager.notify(notificationType.getNotificationId(), notification.build())
                }
            }
        }
    }

    fun configureTripAnalysisNotificationIntent(intent: Intent) {
        intent.putExtra(TRIP_ANALYSIS_NOTIFICATION_KEY, true)
    }

    fun isTripAnalysisNotificationIntent(intent: Intent): Boolean =
        intent.getBooleanExtra(TRIP_ANALYSIS_NOTIFICATION_KEY, false)

    fun isTripDetailNotificationIntent(intent: Intent): Boolean =
        intent.getBooleanExtra(TRIP_DETAIL_NOTIFICATION_KEY, false)

    fun isAppDiagnosisNotificationIntent(intent: Intent): Boolean =
         intent.getBooleanExtra(APP_DIAGNOSIS_NOTIFICATION_KEY, false)

    private fun cancelNotification(context: Context, notificationType: NotificationType) {
        notificationType.cancel(context)
    }

    override fun tripCancelled(cancelTrip: CancelTrip) {
        TripReceiver.tripCancelled(DriveKit.applicationContext, cancelTrip)
    }

    override fun tripFinished(post: PostGeneric, response: PostGenericResponse) {
        TripReceiver.tripFinished(DriveKit.applicationContext, response)
    }

    override fun tripSavedForRepost() {
        sendNotification(DriveKit.applicationContext, NotificationType.TripAnalysisError(TripAnalysisError.NO_NETWORK))
    }

    override fun tripStarted(startMode: StartMode) {
        // Nothing to do.
    }

    override fun onDeviceConfigurationChanged(event: DKDeviceConfigurationEvent) {
        manageDeviceConfigurationEventNotification()
    }

    fun manageDeviceConfigurationEventNotification() {
        val context = DriveKit.applicationContext
        val notificationInfo = PermissionsUtilsUI.getDeviceConfigurationEventNotification()
        if (notificationInfo != null) {
            if (DriveKitTripAnalysis.getConfig().autoStartActivate) {
                sendNotification(context, NotificationType.DeviceConfiguration(notificationInfo), buildAppDiagnosisContentIntent(context))
            }
            WorkerManager.startAppDiagnosisWorker(context)
        } else {
            cancelNotification(context, NotificationType.DeviceConfiguration(null))
            WorkerManager.stopAppDiagnosisWorker(context)
        }
    }

    private fun buildAppDiagnosisContentIntent(context: Context): PendingIntent? {
        val intent = Intent(context, DashboardActivity::class.java)
        intent.putExtra(APP_DIAGNOSIS_NOTIFICATION_KEY, true)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
    }
}
