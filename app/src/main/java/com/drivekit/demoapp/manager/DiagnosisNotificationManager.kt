package com.drivekit.demoapp.manager

import android.app.PendingIntent
import android.content.Context
import com.drivekit.demoapp.notification.controller.DKNotificationManager
import com.drivekit.demoapp.notification.enum.NotificationType
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis

internal object DiagnosisNotificationManager {
    fun onDeviceConfigurationChanged() {
        val context = DriveKit.applicationContext
        PermissionsUtilsUI.getDeviceConfigurationEventNotification()?.let {
            if (DriveKitTripAnalysis.getConfig().autoStartActivate) {
                DKNotificationManager.sendNotification(context, NotificationType.Diagnosis(it), buildContentIntent(context))
                // TODO start periodic worker
            }
        } ?: run {
            DKNotificationManager.cancelNotification(context, NotificationType.Diagnosis(null))
            // TODO stop worker
        }
    }

    // TODO redirect to App Diagnosis screen
    private fun buildContentIntent(context: Context): PendingIntent? {
        val intent = context.applicationContext.packageManager.getLaunchIntentForPackage(context.packageName)
        var contentIntent: PendingIntent? = null
        if (intent != null) {
            contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        return contentIntent
    }
}