package com.drivekit.demoapp.notification.enum

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.drivekit.drivekitdemoapp.R

internal enum class NotificationChannel {
    TRIP_STARTED,
    TRIP_CANCELLED,
    TRIP_ENDED;

    fun isEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).let { manager ->
                manager.getNotificationChannel(getChannelId())?.let { channel ->
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
            }
            false
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun getChannelId() = when (this) {
        TRIP_STARTED -> "drivekit_demo_trip_started"
        TRIP_CANCELLED -> "drivekit_demo_trip_cancelled_channel"
        TRIP_ENDED -> "drivekit_demo_trip_ended_channel"
    }

    fun getChannelNameResId() = when (this) {
        TRIP_STARTED -> R.string.notif_trip_started_title
        TRIP_CANCELLED -> R.string.notification_trip_cancelled_title
        TRIP_ENDED -> R.string.notification_trip_finished_title
    }

    fun getTitleResId() = when (this) {
        TRIP_STARTED -> R.string.notif_trip_started_title
        TRIP_CANCELLED -> R.string.notification_trip_cancelled_title
        TRIP_ENDED -> R.string.notification_trip_finished_title
    }

    fun getDescriptionResId() = when (this) {
        TRIP_STARTED -> R.string.notification_trip_in_progress_description_enabled
        TRIP_CANCELLED -> R.string.notification_trip_cancelled_description
        TRIP_ENDED -> R.string.notification_trip_finished_description
    }
}