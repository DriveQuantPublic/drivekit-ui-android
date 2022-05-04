package com.drivekit.demoapp.notification.controller

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager
import com.drivekit.demoapp.notification.enum.DKNotificationChannel
import com.drivekit.demoapp.notification.enum.NotificationType

internal object NotificationManager {

    fun createChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager != null) {
            DKNotificationChannel.values().forEach { channel ->
                channel.getSharedPreferencesKey()?.let { sharedPrefKey ->
                    if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(sharedPrefKey, true)) {
                        val notificationChannel = android.app.NotificationChannel(channel.getChannelId(), context.getString(channel.getChannelNameResId()), NotificationManager.IMPORTANCE_DEFAULT)
                        manager.createNotificationChannel(notificationChannel)
                    }
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
            val notificationChannel = android.app.NotificationChannel(
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
}