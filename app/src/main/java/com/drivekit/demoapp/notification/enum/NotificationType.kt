package com.drivekit.demoapp.notification.enum

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.drivekit.demoapp.utils.getAlternativeNotificationBodyResId
import com.drivekit.demoapp.utils.getAlternativeNotificationNotDisplayedBodyResId
import com.drivekit.demoapp.utils.isAlternativeNotificationManaged
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.permissionsutils.diagnosisnotification.model.DKDiagnosisNotificationInfo
import com.drivequant.drivekit.ui.DriverDataUI

internal sealed class NotificationType {
    data class TripEnded(
        val transportationMode: TransportationMode,
        val advices: Int,
        val additionalBody: String? = null
    ) : NotificationType()
    data class TripCancelled(val reason: TripCancellationReason) : NotificationType()
    data class TripAnalysisError(val error: com.drivekit.demoapp.notification.enum.TripAnalysisError) : NotificationType()
    object TripTooShort : NotificationType()

    data class DeviceConfiguration(val notificationContent: DKDiagnosisNotificationInfo?): NotificationType()

    fun getChannel() = when (this) {
        is TripAnalysisError -> when (this.error) {
            com.drivekit.demoapp.notification.enum.TripAnalysisError.DUPLICATE_TRIP,
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_NETWORK,
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_API_KEY -> DKNotificationChannel.TRIP_ENDED
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_BEACON,
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_BLUETOOTH_DEVICE -> DKNotificationChannel.TRIP_CANCELLED
        }
        is TripCancelled -> DKNotificationChannel.TRIP_CANCELLED
        is TripEnded -> {
            if (!transportationMode.isAlternative() || (transportationMode.isAlternativeNotificationManaged() && DriverDataUI.enableAlternativeTrips)) {
                DKNotificationChannel.TRIP_ENDED
            } else {
                DKNotificationChannel.TRIP_CANCELLED
            }
        }
        TripTooShort -> DKNotificationChannel.TRIP_ENDED
        is DeviceConfiguration -> DKNotificationChannel.DEMO_APP
    }

    fun getNotificationId(): Int = when (this) {
        is TripAnalysisError -> when (this.error) {
            com.drivekit.demoapp.notification.enum.TripAnalysisError.DUPLICATE_TRIP -> 203
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_NETWORK -> 205
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_API_KEY -> 204
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_BEACON -> 252
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_BLUETOOTH_DEVICE -> 253
        }
        is TripCancelled -> when (this.reason) {
            TripCancellationReason.NO_BEACON -> 252
            TripCancellationReason.NO_BLUETOOTH_DEVICE -> 253
            TripCancellationReason.HIGH_SPEED -> 250
            TripCancellationReason.NO_GPS_POINT -> 251
        }
        is TripEnded -> {
            when (transportationMode) {
                TransportationMode.TRAIN -> 300
                TransportationMode.BOAT -> 301
                TransportationMode.BIKE -> 302
                TransportationMode.SKIING -> 303
                TransportationMode.IDLE -> 304
                TransportationMode.BUS -> 305
                else -> {
                    if (advices > 0) {
                        201
                    } else {
                        200
                    }
                }
            }
        }
        TripTooShort -> 202
        is DeviceConfiguration -> 401
    }

    private fun getIconResId() = R.drawable.ic_notification

    private fun getTitleResId() = when (this) {
        is TripAnalysisError -> when (this.error) {
            com.drivekit.demoapp.notification.enum.TripAnalysisError.DUPLICATE_TRIP,
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_BEACON,
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_BLUETOOTH_DEVICE,
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_API_KEY -> R.string.app_name
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_NETWORK -> R.string.notif_trip_no_network_title
        }
        is TripCancelled -> when (this.reason) {
            TripCancellationReason.NO_BEACON,
            TripCancellationReason.NO_BLUETOOTH_DEVICE,
            TripCancellationReason.HIGH_SPEED -> R.string.notif_trip_cancelled_title
            TripCancellationReason.NO_GPS_POINT -> R.string.notif_trip_cancelled_no_gps_data_title
        }
        is TripEnded -> R.string.notif_trip_finished_title
        TripTooShort -> R.string.notif_trip_too_short_title
        is DeviceConfiguration -> R.string.app_name
    }

    private fun getDescription(context: Context, additionalBody: String?): String = when (this) {
        is TripAnalysisError -> when (this.error) {
            com.drivekit.demoapp.notification.enum.TripAnalysisError.DUPLICATE_TRIP -> R.string.notif_trip_error_duplicate_trip
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_NETWORK -> R.string.notif_trip_no_network
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_API_KEY -> R.string.notif_trip_error_unauthorized
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_BEACON -> R.string.notif_trip_finished_no_beacon
            com.drivekit.demoapp.notification.enum.TripAnalysisError.NO_BLUETOOTH_DEVICE -> R.string.notif_trip_finished_no_bluetooth_device
        }.let {
            context.getString(it)
        }
        is TripCancelled -> when (this.reason) {
            TripCancellationReason.NO_BEACON -> R.string.notif_trip_cancelled_no_beacon
            TripCancellationReason.NO_BLUETOOTH_DEVICE -> R.string.notif_trip_cancelled_no_bluetooth_device
            TripCancellationReason.HIGH_SPEED -> R.string.notif_trip_cancelled_highspeed
            TripCancellationReason.NO_GPS_POINT -> R.string.notif_trip_cancelled_no_gps_data
        }.let {
            context.getString(it)
        }
        is TripEnded -> {
            var body = context.getString(R.string.notif_trip_finished)
            if (!this.transportationMode.isAlternative()) {
                if (!additionalBody.isNullOrBlank()) {
                    body += "\n"
                    body += additionalBody
                }
                if (advices == 1) {
                    body += "\n"
                    body += context.getString(R.string.notif_trip_finished_advice)
                } else if (advices > 1) {
                    body += "\n"
                    body += context.getString(R.string.notif_trip_finished_advices)
                }
                body
            } else {
                if (DriverDataUI.enableAlternativeTrips) {
                    this.transportationMode.getAlternativeNotificationBodyResId()
                } else {
                    this.transportationMode.getAlternativeNotificationNotDisplayedBodyResId()
                }?.let {
                    body = context.getString(it)
                }
                body
            }
        }
        TripTooShort -> context.getString(R.string.notif_trip_too_short)
        is DeviceConfiguration -> context.getString(this.notificationContent!!.messageResId)
    }

    fun createNotification(
        context: Context,
        pendingIntent: PendingIntent?,
        additionalBody: String?
    ): NotificationCompat.Builder {
        val body = this.getDescription(context, additionalBody)
        return NotificationCompat.Builder(context, this.getChannel().getChannelId())
            .setSmallIcon(this.getIconResId())
            .setContentTitle(context.getString(this.getTitleResId()))
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(3)
            .setAutoCancel(true)
    }

    fun cancel(context: Context) {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)?.cancel(
            this.getNotificationId()
        )
    }
}