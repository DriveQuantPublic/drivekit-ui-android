package com.drivekit.demoapp

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.drivekit.demoapp.config.DriveKitConfig
import com.drivekit.drivekitdemoapp.R
import java.util.Random

class DriveKitDemoApplication : Application() {

    companion object {
        fun showNotification(context: Context, message: String) {
            val builder = NotificationCompat.Builder(context, "notif_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(Random().nextInt(Integer.MAX_VALUE), builder.build())
        }
    }

    override fun onCreate() {
        super.onCreate()
        DriveKitConfig.configure(this)
    }
}
