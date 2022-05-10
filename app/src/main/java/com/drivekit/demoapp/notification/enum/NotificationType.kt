package com.drivekit.demoapp.notification.enum

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.drivekit.demoapp.utils.getAlternativeNotificationBodyResId
import com.drivekit.demoapp.utils.isAlternativeNotificationManaged
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.ui.DriverDataUI

internal sealed class NotificationType {
    data class TRIP_ENDED(
        val transportationMode: TransportationMode,
        val advices: Int,
        val additionalBody: String? = null,
    ) : NotificationType()
    data class TRIP_CANCELLED(val reason: TripCancellationReason) : NotificationType()
    data class TRIP_ANALYSIS_ERROR(val error: TripAnalysisError) : NotificationType()
    object TRIP_TOO_SHORT : NotificationType()

    fun getChannel() = when (this) {
        is TRIP_ANALYSIS_ERROR -> when (this.error) {
            TripAnalysisError.DUPLICATE_TRIP,
            TripAnalysisError.NO_NETWORK,
            TripAnalysisError.NO_API_KEY -> DKNotificationChannel.TRIP_ENDED
            TripAnalysisError.NO_BEACON -> DKNotificationChannel.TRIP_CANCELLED
        }
        is TRIP_CANCELLED -> DKNotificationChannel.TRIP_CANCELLED
        is TRIP_ENDED -> {
            if (!transportationMode.isAlternative() || (transportationMode.isAlternativeNotificationManaged() && DriverDataUI.enableAlternativeTrips)) {
                DKNotificationChannel.TRIP_ENDED
            } else {
                DKNotificationChannel.TRIP_CANCELLED
            }
        }
        TRIP_TOO_SHORT -> DKNotificationChannel.TRIP_ENDED
    }

    fun getNotificationId(): Int = when (this) {
        is TRIP_ANALYSIS_ERROR -> when (this.error) {
            TripAnalysisError.DUPLICATE_TRIP -> 203
            TripAnalysisError.NO_NETWORK -> 205
            TripAnalysisError.NO_API_KEY -> 204
            TripAnalysisError.NO_BEACON -> 252
        }
        is TRIP_CANCELLED -> when (this.reason) {
            TripCancellationReason.NO_BEACON -> 252
            TripCancellationReason.HIGH_SPEED -> 250
            TripCancellationReason.NO_GPS_POINT -> 251
        }
        is TRIP_ENDED -> {
            when (transportationMode) {
                TransportationMode.TRAIN -> 300
                TransportationMode.BOAT -> 301
                TransportationMode.BIKE -> 302
                TransportationMode.SKIING -> 303
                TransportationMode.IDLE -> 304
                else -> {
                    if (advices > 0) {
                        201
                    } else {
                        200
                    }
                }
            }
        }
        TRIP_TOO_SHORT -> 202
    }

    private fun getIconResId() = R.drawable.ic_notification

    private fun getTitleResId() = when (this) {
        is TRIP_ANALYSIS_ERROR -> when (this.error) {
            TripAnalysisError.DUPLICATE_TRIP,
            TripAnalysisError.NO_BEACON,
            TripAnalysisError.NO_API_KEY -> R.string.app_name
            TripAnalysisError.NO_NETWORK -> R.string.notif_trip_no_network_title
        }
        is TRIP_CANCELLED -> when (this.reason) {
            TripCancellationReason.NO_BEACON,
            TripCancellationReason.HIGH_SPEED -> R.string.notif_trip_cancelled_title
            TripCancellationReason.NO_GPS_POINT -> R.string.notif_trip_cancelled_no_gps_data_title
        }
        is TRIP_ENDED -> R.string.notif_trip_finished_title
        TRIP_TOO_SHORT -> R.string.notif_trip_too_short_title
    }

    private fun getDescription(context: Context, additionalBody: String?): String = when (this) {
        is TRIP_ANALYSIS_ERROR -> when (this.error) {
            TripAnalysisError.DUPLICATE_TRIP -> R.string.notif_trip_error_duplicate_trip
            TripAnalysisError.NO_NETWORK -> R.string.notif_trip_no_network
            TripAnalysisError.NO_API_KEY -> R.string.notif_trip_error_unauthorized
            TripAnalysisError.NO_BEACON -> R.string.notif_trip_finished_no_beacon
        }.let {
            context.getString(it)
        }
        is TRIP_CANCELLED -> when (this.reason) {
            TripCancellationReason.NO_BEACON -> R.string.notif_trip_cancelled_no_beacon
            TripCancellationReason.HIGH_SPEED -> R.string.notif_trip_cancelled_highspeed
            TripCancellationReason.NO_GPS_POINT -> R.string.notif_trip_cancelled_no_gps_data
        }.let {
            context.getString(it)
        }
        is TRIP_ENDED -> {
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
                this.transportationMode.getAlternativeNotificationBodyResId()?.let {
                    body = context.getString(it)
                }
                body
            }
        }
        TRIP_TOO_SHORT -> context.getString(R.string.notif_trip_too_short)
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
}