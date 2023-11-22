package com.drivekit.demoapp.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.drivekit.demoapp.worker.DiagnosisNotificationWorker
import java.util.concurrent.TimeUnit

internal object WorkerManager {
    private enum class WorkerTag {
        DIAGNOSIS_NOTIFICATION
    }

    fun startAppDiagnosisWorker(context: Context) {
        val worker = PeriodicWorkRequestBuilder<DiagnosisNotificationWorker>(2, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(WorkerTag.DIAGNOSIS_NOTIFICATION.name, ExistingPeriodicWorkPolicy.KEEP, worker)
    }

    fun stopAppDiagnosisWorker(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WorkerTag.DIAGNOSIS_NOTIFICATION.name)
    }

    fun reset(context: Context) {
        stopAppDiagnosisWorker(context)
    }
}