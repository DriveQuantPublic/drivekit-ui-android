package com.drivekit.demoapp.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.drivekit.demoapp.worker.DiagnosisNotificationWorker
import java.util.concurrent.TimeUnit

internal object WorkerManager {
    private enum class WorkerTag {
        APP_DIAGNOSIS
    }

    fun startAppDiagnosisWorker(context: Context) {
        val worker = PeriodicWorkRequestBuilder<DiagnosisNotificationWorker>(2, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(WorkerTag.APP_DIAGNOSIS.name, ExistingPeriodicWorkPolicy.KEEP, worker)
    }

    fun stopAppDiagnosisWorker(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WorkerTag.APP_DIAGNOSIS.name)
    }

    fun reset(context: Context) {
        stopAppDiagnosisWorker(context)
    }
}