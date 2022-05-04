package com.drivekit.demoapp.notification.settings.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.drivekit.demoapp.notification.controller.NotificationManager
import com.drivekit.demoapp.notification.enum.NotificationChannel
import com.drivekit.drivekitdemoapp.R

internal class NotificationSettingsViewModel : ViewModel() {

    fun isChannelEnabled(context: Context, notificationChannel: NotificationChannel) = notificationChannel.isEnabled(context)

    fun getChannelTitleResId(notificationChannel: NotificationChannel) = notificationChannel.getTitleResId()

    fun getChannelDescriptionResId(notificationChannel: NotificationChannel) = notificationChannel.getDescriptionResId()

    fun getTripStartedChannelDescriptionResId(context: Context) = if (isChannelEnabled(context, NotificationChannel.TRIP_STARTED)) {
        R.string.notification_trip_in_progress_description_enabled
    } else {
        R.string.notification_trip_in_progress_description_disabled
    }

    fun manageChannel(context: Context, enable: Boolean, channel: NotificationChannel) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(channel.getSharedPreferencesKey(), enable).apply()
        if (enable) {
            NotificationManager.createChannel(context, channel)
        } else {
            NotificationManager.deleteChannel(context, channel)
        }
    }
}