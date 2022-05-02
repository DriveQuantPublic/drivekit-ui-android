package com.drivekit.demoapp.notification.controller

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.drivekit.demoapp.notification.enum.NotificationChannel
import com.drivekit.demoapp.notification.enum.NotificationType
import kotlin.random.Random

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

    fun sendNotification(
        context: Context,
        notificationType: NotificationType,
        contentIntent: PendingIntent? = null,
        additionalBody: String? = null
    ) {
       notificationType.createNotification(context, contentIntent, additionalBody).let { notification ->
           (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)?.let { manager ->
               if (notificationType.getChannel().isEnabled(context)) {
                   val notificationId = Random.nextInt(1, Integer.MAX_VALUE)
                   manager.notify(notificationId, notification.build())
               }
           }
       }
    }
}