package com.drivekit.demoapp.notification.enum

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.databaseutils.entity.TransportationMode

internal sealed class NotificationType {
    object TRIP_STARTED : NotificationType()
    data class TRIP_ENDED(
        val message: String,
        val transportationMode: TransportationMode,
        val hasAdvices: Boolean
    ) : NotificationType()

    data class TRIP_CANCELLED(val reason: TripCancellationReason) : NotificationType()
    data class TRIP_ANALYSIS_ERROR(val error: TripAnalysisError) : NotificationType()
    object TRIP_TOO_SHORT : NotificationType()

    val tripEndedError = "drivekit_demo_app_trip_ended_error"

    fun getIdentifier() = when (this) {
        is TRIP_ANALYSIS_ERROR -> {
            when (this.error) {
                TripAnalysisError.DUPLICATE_TRIP -> "drivekit_demo_app_trip_error_duplicate"
                TripAnalysisError.NO_NETWORK -> "drivekit_demo_app_trip_error_no_network"
                TripAnalysisError.NO_API_KEY -> "drivekit_demo_app_trip_error_no_api_key"
                TripAnalysisError.NO_BEACON -> "drivekit_demo_app_trip_error_no_beacon"
            }
        }
        is TRIP_CANCELLED -> tripEndedError
        is TRIP_ENDED -> {
            // TODO check alternative trips
            "drivekit_demo_app_trip_ended"
        }
        TRIP_STARTED -> "drivekit_demo_app_trip_started"
        TRIP_TOO_SHORT -> "drivekit_demo_app_trip_too_short"
    }

    fun getChannel() = when (this) {
        is TRIP_ANALYSIS_ERROR -> when (this.error) {
            TripAnalysisError.DUPLICATE_TRIP,
            TripAnalysisError.NO_NETWORK,
            TripAnalysisError.NO_API_KEY -> NotificationChannel.TRIP_ENDED
            TripAnalysisError.NO_BEACON -> NotificationChannel.TRIP_CANCELLED
        }
        is TRIP_CANCELLED -> NotificationChannel.TRIP_CANCELLED
        is TRIP_ENDED -> NotificationChannel.TRIP_ENDED // TODO check alternative mode
        TRIP_STARTED -> NotificationChannel.TRIP_STARTED
        TRIP_TOO_SHORT -> NotificationChannel.TRIP_ENDED
    }

    fun getIconResId() = R.mipmap.ic_launcher

    fun getTitleResId() = when (this) {
        is TRIP_ANALYSIS_ERROR -> when (this.error) {
            TripAnalysisError.DUPLICATE_TRIP,
            TripAnalysisError.NO_BEACON,
            TripAnalysisError.NO_API_KEY -> R.string.notif_trip_cancelled_title // TODO vérifier
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

    fun getDescriptionResId() = when (this) {
        is TRIP_ANALYSIS_ERROR -> when (this.error) {
            TripAnalysisError.DUPLICATE_TRIP -> R.string.notif_trip_error_duplicate_trip
            TripAnalysisError.NO_NETWORK -> R.string.notif_trip_no_network
            TripAnalysisError.NO_API_KEY -> R.string.notif_trip_error_unauthorized
            TripAnalysisError.NO_BEACON -> R.string.notif_trip_finished_no_beacon
        }
        is TRIP_CANCELLED -> when (this.reason) {
            TripCancellationReason.NO_BEACON -> R.string.notif_trip_cancelled_no_beacon
            TripCancellationReason.HIGH_SPEED -> R.string.notif_trip_cancelled_highspeed
            TripCancellationReason.NO_GPS_POINT -> R.string.notif_trip_cancelled_no_gps_data
        }
        is TRIP_ENDED -> {
            when (this.transportationMode) { // TODO check alternative
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
            }
        }
        TRIP_STARTED -> R.string.notif_trip_started
        TRIP_TOO_SHORT -> R.string.notif_trip_too_short
    }

    fun createNotification(
        context: Context,
        pendingIntent: PendingIntent?,
        title: String?,
        content: String?
    ): NotificationCompat.Builder {
        val body = this.getDescriptionResId()?.let { context.getString(it) } ?: run { null }
        return NotificationCompat.Builder(context, this.getChannel().getChannelId())
            .setSmallIcon(this.getIconResId())
            .setContentTitle(context.getString(this.getTitleResId()))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(3)
            .setAutoCancel(true)
    }
}