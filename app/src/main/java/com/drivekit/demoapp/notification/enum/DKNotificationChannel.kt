package com.drivekit.demoapp.notification.enum

import android.content.Context
import androidx.preference.PreferenceManager
import com.drivekit.drivekitdemoapp.R

internal enum class DKNotificationChannel {
    TRIP_STARTED,
    TRIP_CANCELLED,
    TRIP_ENDED;

    fun isEnabled(context: Context): Boolean {
        return getSharedPreferencesKey()?.let {
            PreferenceManager.getDefaultSharedPreferences(context).getBoolean(it, true)
        } ?: run {
            true
        }
    }

    fun getSharedPreferencesKey() = when (this) {
        TRIP_STARTED -> null // This channel is already managed in TripAnalysis
        TRIP_CANCELLED -> "drivekit_channel_trip_cancelled_pref"
        TRIP_ENDED -> "drivekit_channel_trip_ended_pref"
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