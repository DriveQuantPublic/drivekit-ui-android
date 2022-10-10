package com.drivekit.demoapp.utils

import android.app.Activity
import android.content.Intent

fun Activity.restartApplication() {
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        val mainIntent = Intent.makeRestartActivityTask(intent.component)
        startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
}