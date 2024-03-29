package com.drivekit.demoapp.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.drivekit.demoapp.worker.DiagnosisNotificationWorker
import java.util.concurrent.TimeUnit

internal object WorkerManager {
    private enum class WorkName {
        DIAGNOSIS_NOTIFICATION
    }

    fun startAppDiagnosisWorker(context: Context) {
        val worker = PeriodicWorkRequestBuilder<DiagnosisNotificationWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(WorkName.DIAGNOSIS_NOTIFICATION.name, ExistingPeriodicWorkPolicy.UPDATE, worker)
    }

    fun stopAppDiagnosisWorker(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WorkName.DIAGNOSIS_NOTIFICATION.name)
    }

    fun reset(context: Context) {
        stopAppDiagnosisWorker(context)
    }
}