package com.drivekit.demoapp.notification.controller

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.drivekit.demoapp.notification.enum.NotificationChannel
import com.drivekit.demoapp.notification.enum.NotificationType

internal object NotificationManager {

    fun createChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager != null) {
            NotificationChannel.values().forEach { channel ->
                if (channel.isEnabled(context)) {
                    val notificationChannel = android.app.NotificationChannel(channel.getChannelId(), context.getString(channel.getChannelNameResId()), NotificationManager.IMPORTANCE_DEFAULT)
                    manager.createNotificationChannel(notificationChannel)
                }
            }
        }
    }

    fun createChannel(context: Context, channel: NotificationChannel) {
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

    fun deleteChannel(context: Context, channel: NotificationChannel) {
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