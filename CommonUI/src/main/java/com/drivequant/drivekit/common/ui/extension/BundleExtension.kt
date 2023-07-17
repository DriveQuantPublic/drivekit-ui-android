package com.drivequant.drivekit.common.ui.extension

import android.os.Build
import android.os.Bundle
import java.io.Serializable

fun <T: Serializable> Bundle.getSerializableCompat(key: String, objectClass: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, objectClass)
    } else {
        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        getSerializable(key) as? T
    }
}
