package com.drivekit.demoapp.utils

import android.content.Intent
import android.os.Build
import java.io.Serializable

fun <T : Serializable?> Intent.getSerializableCompat(key: String, clazz: Class<T>): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) this.getSerializableExtra(
        key,
        clazz
    ) else (getSerializableExtra(key) as T?)