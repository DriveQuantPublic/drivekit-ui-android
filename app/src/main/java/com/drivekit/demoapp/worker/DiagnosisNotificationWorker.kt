package com.drivekit.demoapp.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.drivekit.demoapp.notification.controller.DKNotificationManager

internal class DiagnosisNotificationWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        DKNotificationManager.manageDeviceConfigurationEventNotification()
        return Result.success()
    }
}