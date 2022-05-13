package com.drivekit.demoapp.notification.settings.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.drivekit.demoapp.notification.controller.DKNotificationManager
import com.drivekit.demoapp.notification.enum.DKNotificationChannel
import com.drivekit.drivekitdemoapp.R

internal class NotificationSettingsViewModel : ViewModel() {

    fun isChannelEnabled(context: Context, notificationChannel: DKNotificationChannel) = notificationChannel.isEnabled(context)

    fun getChannelTitleResId(notificationChannel: DKNotificationChannel) = notificationChannel.getTitleResId()

    fun getChannelDescriptionResId(notificationChannel: DKNotificationChannel) = notificationChannel.getDescriptionResId()

    fun getTripStartedChannelDescriptionResId(context: Context) = if (isChannelEnabled(context, DKNotificationChannel.TRIP_STARTED)) {
        R.string.notification_trip_in_progress_description_enabled
    } else {
        R.string.notification_trip_in_progress_description_disabled
    }

    fun manageChannel(context: Context, enable: Boolean, channel: DKNotificationChannel) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(channel.getSharedPreferencesKey(), enable).apply()
        if (enable) {
            DKNotificationManager.createChannel(context, channel)
        } else {
            DKNotificationManager.deleteChannel(context, channel)
        }
    }
}