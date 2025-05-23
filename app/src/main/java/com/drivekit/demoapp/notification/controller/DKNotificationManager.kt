package com.drivekit.demoapp.notification.controller

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import com.drivekit.demoapp.DriveKitDemoApplication
import com.drivekit.demoapp.config.DriveKitConfig
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.notification.enum.DKNotificationChannel
import com.drivekit.demoapp.notification.enum.NotificationType
import com.drivekit.demoapp.notification.enum.TripCancelationReason
import com.drivekit.demoapp.notification.enum.TripResponseErrorNotification
import com.drivekit.demoapp.utils.WorkerManager
import com.drivekit.demoapp.utils.isAlternativeNotificationManaged
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.deviceconfiguration.DKDeviceConfigurationEvent
import com.drivequant.drivekit.core.deviceconfiguration.DKDeviceConfigurationListener
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.databaseutils.entity.TripAdvice
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.model.triplistener.DKTripCancelationReason
import com.drivequant.drivekit.tripanalysis.model.triplistener.DKTripRecordingCanceledState
import com.drivequant.drivekit.tripanalysis.utils.TripResult
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import java.util.Random

internal object DKNotificationManager : TripListener, DKDeviceConfigurationListener {

    private const val TRIP_ANALYSIS_NOTIFICATION_KEY = "dk_tripanalysis_notification"
    private const val TRIP_DETAIL_NOTIFICATION_KEY = "dk_trip_detail_notification"
    private const val APP_DIAGNOSIS_NOTIFICATION_KEY = "dk_app_diagnosis_notification"

    fun configure() {
        DriveKit.addDeviceConfigurationListener(this)
        DriveKitTripAnalysis.addTripListener(this)
    }

    fun createChannels(context: Context) {
        DKNotificationChannel.values().forEach { channel ->
            if (channel.isEnabled(context)) {
                createChannel(context, channel)
            }
        }
    }

    fun createChannel(context: Context, channel: DKNotificationChannel) {
        if (channel.canCreate()) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                channel.getChannelId(),
                context.getString(channel.getChannelNameResId()),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(notificationChannel)
        }
    }

    private fun deleteChannels(context: Context) {
        DKNotificationChannel.values().forEach {
            deleteChannel(context, it)
        }
    }

    fun deleteChannel(context: Context, channel: DKNotificationChannel) {
        if (channel.canDelete()) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.deleteNotificationChannel(channel.getChannelId())
        }
    }

    private fun sendNotification(
        context: Context,
        notificationType: NotificationType,
        contentIntent: PendingIntent? = null,
        additionalBody: String? = null
    ) {
        notificationType.createNotification(context, contentIntent, additionalBody).let { notification ->
            if (notificationType.getChannel().isEnabled(context)) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(notificationType.getNotificationId(), notification.build())
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

    override fun tripRecordingCanceled(state: DKTripRecordingCanceledState) {
        manageTripCanceled(DriveKit.applicationContext, state.cancelationReason)
    }

    override fun tripFinished(result: TripResult) {
        manageTripFinished(DriveKit.applicationContext, result)
    }

    override fun tripSavedForRepost() {
        sendNotification(DriveKit.applicationContext, NotificationType.NoNetwork)
    }

    override fun onDeviceConfigurationChanged(event: DKDeviceConfigurationEvent) {
        manageDeviceConfigurationEventNotification()
    }

    private fun manageTripFinished(context: Context, tripResult: TripResult) {
        when (tripResult) {
            is TripResult.TripValid -> {
                tripResult.info.forEach {
                    DriveKitLog.i("Application", "Trip response info: ${it.comment}")
                }
                if (tripResult.hasSafetyAndEcoDrivingScore) {
                    manageTripFinishedAndValid(context, tripResult)
                } else {
                    sendTripTooShortNotification(context, tripResult)
                }
            }
            is TripResult.TripError -> {
                DriveKitLog.i("Application", "Trip response error: ${tripResult.tripResponseError.name}")
                sendTripErrorNotification(context, tripResult)
            }
        }
    }

    private fun manageTripCanceled(context: Context, cancelationReason: DKTripCancelationReason) {
        when (cancelationReason) {
            DKTripCancelationReason.NO_LOCATION_DATA -> R.string.notif_trip_cancelled_no_gps_data
            DKTripCancelationReason.NO_BEACON -> R.string.notif_trip_cancelled_no_beacon
            DKTripCancelationReason.NO_BLUETOOTH_DEVICE -> R.string.notif_trip_cancelled_no_bluetooth_device
            else -> R.string.trip_cancelled_reset
        }.let { messageResId ->
            DriveKitDemoApplication.showNotification(context, context.getString(messageResId))
        }
        when (cancelationReason) {
            DKTripCancelationReason.HIGH_SPEED -> TripCancelationReason.HIGH_SPEED
            DKTripCancelationReason.NO_BEACON -> TripCancelationReason.NO_BEACON
            DKTripCancelationReason.NO_LOCATION_DATA -> TripCancelationReason.NO_GPS_POINT
            DKTripCancelationReason.USER,
            DKTripCancelationReason.NO_SPEED,
            DKTripCancelationReason.MISSING_CONFIGURATION,
            DKTripCancelationReason.RESET,
            DKTripCancelationReason.BEACON_NO_SPEED,
            DKTripCancelationReason.BLUETOOTH_DEVICE_NO_SPEED -> null
            DKTripCancelationReason.NO_BLUETOOTH_DEVICE -> TripCancelationReason.NO_BLUETOOTH_DEVICE
            DKTripCancelationReason.APP_KILLED -> null
        }?.let {
            sendNotification(context, NotificationType.TripCanceled(it))
        }
    }

    private fun manageTripFinishedAndValid(context: Context, tripResult: TripResult.TripValid) {
        tripResult.getTrip()?.let {
            val contentIntent = buildTripFinishedContentIntent(context, it.transportationMode, it.tripAdvices, it.itinId)
            if (it.transportationMode.isAlternative() && it.transportationMode.isAlternativeNotificationManaged()) {
                sendNotification(context, NotificationType.TripEnded(it.transportationMode, it.tripAdvices.size), contentIntent)
            } else {
                val additionalBody = when (DriverDataUI.tripData) {
                    TripData.SAFETY -> "${context.getString(R.string.notif_trip_finished_safety)} : ${it.safety!!.safetyScore.removeZeroDecimal()}/10"
                    TripData.ECO_DRIVING -> "${context.getString(R.string.notif_trip_finished_efficiency)} : ${it.ecoDriving!!.score.removeZeroDecimal()}/10"
                    TripData.DISTRACTION -> "${context.getString(R.string.notif_trip_finished_distraction)} : ${it.driverDistraction!!.score.removeZeroDecimal()}/10"
                    TripData.SPEEDING -> null
                    TripData.DURATION -> null
                    TripData.DISTANCE -> null
                }
                sendNotification(context, NotificationType.TripEnded(it.transportationMode, it.tripAdvices.size), contentIntent, additionalBody)
            }
        }
    }

    private fun sendTripTooShortNotification(context: Context, tripResult: TripResult.TripValid) {
        val contentIntent = buildTripFinishedContentIntent(context, null, null, tripResult.itinId)
        sendNotification(context, NotificationType.TripTooShort, contentIntent)
    }

    private fun sendTripErrorNotification(context: Context, error: TripResult.TripError) {
        val errorNotification = TripResponseErrorNotification.fromTripResponseError(error.tripResponseError)
        if (errorNotification != null) {
            val contentIntent = buildTripFinishedContentIntent(context, null, null, null)
            sendNotification(context, NotificationType.TripAnalysisError(errorNotification), contentIntent)
        }
    }

    fun manageDeviceConfigurationEventNotification() {
        val context = DriveKit.applicationContext
        if (DriveKitConfig.isUserOnboarded(context)) {
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
    }

    private fun buildTripFinishedContentIntent(
        context: Context,
        transportationMode: TransportationMode?,
        tripAdvices: List<TripAdvice>?,
        itinId: String?
    ): PendingIntent? {
        val intent = Intent(context, DashboardActivity::class.java)
        if (!itinId.isNullOrEmpty()) {
            val hasTripAdvices = !tripAdvices.isNullOrEmpty()
            intent.putExtra(TRIP_DETAIL_NOTIFICATION_KEY, true)
            intent.putExtra(TripDetailActivity.ITINID_EXTRA, itinId)
            intent.putExtra(TripDetailActivity.OPEN_ADVICE_EXTRA, hasTripAdvices)
            if (transportationMode != null && transportationMode.isAlternative()) {
                if (DriverDataUI.enableAlternativeTrips) {
                    intent.putExtra(TripDetailActivity.TRIP_LIST_CONFIGURATION_TYPE_EXTRA, TripListConfigurationType.ALTERNATIVE)
                }
            } else {
                intent.putExtra(TripDetailActivity.TRIP_LIST_CONFIGURATION_TYPE_EXTRA, TripListConfigurationType.MOTORIZED)
            }
        }
        return TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(intent)
            .getPendingIntent(Random().nextInt(Int.MAX_VALUE), PendingIntent.FLAG_IMMUTABLE)
    }

    private fun buildAppDiagnosisContentIntent(context: Context): PendingIntent? {
        val intent = Intent(context, DashboardActivity::class.java)
        intent.putExtra(APP_DIAGNOSIS_NOTIFICATION_KEY, true)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    fun reset(context: Context) {
        deleteChannels(context) // Cancel all Demo App notifications
        createChannels(context)
        WorkerManager.reset(context)
    }
}
