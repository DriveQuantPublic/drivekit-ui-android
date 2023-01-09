package com.drivekit.demoapp.utils

import android.app.PendingIntent
import android.os.Build

internal fun getMutableFlag() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    PendingIntent.FLAG_MUTABLE
else
    PendingIntent.FLAG_UPDATE_CURRENT
