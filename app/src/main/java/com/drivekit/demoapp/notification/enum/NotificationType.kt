package com.drivekit.demoapp.notification.enum

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.ui.DriverDataUI

internal sealed class NotificationType {
    object TRIP_STARTED : NotificationType()
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
            TripAnalysisError.NO_API_KEY -> NotificationChannel.TRIP_ENDED
            TripAnalysisError.NO_BEACON -> NotificationChannel.TRIP_CANCELLED
        }
        is TRIP_CANCELLED -> NotificationChannel.TRIP_CANCELLED
        is TRIP_ENDED -> {
            if (transportationMode.isAlternative() && DriverDataUI.enableAlternativeTrips) {
                NotificationChannel.TRIP_ENDED
            } else {
                NotificationChannel.TRIP_CANCELLED
            }
        }
        TRIP_STARTED -> NotificationChannel.TRIP_STARTED
        TRIP_TOO_SHORT -> NotificationChannel.TRIP_ENDED
    }

    private fun getIconResId() = R.mipmap.ic_launcher

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
        TRIP_STARTED -> R.string.notif_trip_started_title
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
                when (this.transportationMode) {
                    TransportationMode.TRAIN -> R.string.notif_trip_train_detected
                    TransportationMode.BOAT -> R.string.notif_trip_boat_detected
                    TransportationMode.BIKE -> R.string.notif_trip_bike_detected
                    TransportationMode.SKIING -> R.string.notif_trip_skiing_detected
                    TransportationMode.IDLE -> R.string.notif_trip_idle_detected
                    TransportationMode.UNKNOWN,
                    TransportationMode.CAR,
                    TransportationMode.MOTO,
                    TransportationMode.TRUCK,
                    TransportationMode.BUS,
                    TransportationMode.FLIGHT,
                    TransportationMode.ON_FOOT,
                    TransportationMode.OTHER -> null
                }?.let {
                    body = context.getString(it)
                }
                body
            }
        }
        TRIP_STARTED -> context.getString(R.string.notif_trip_started)
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