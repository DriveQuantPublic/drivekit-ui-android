package com.drivekit.demoapp.utils

import android.app.PendingIntent
import android.os.Build

internal fun getImmutableFlag() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    PendingIntent.FLAG_IMMUTABLE
else
    PendingIntent.FLAG_UPDATE_CURRENT

internal fun getMutableFlag() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    PendingIntent.FLAG_MUTABLE
else
    PendingIntent.FLAG_UPDATE_CURRENT