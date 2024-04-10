package com.drivequant.drivekit.vehicle.ui

import android.content.Context
import androidx.startup.Initializer
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.autoinit.DriveKitInitializer

internal class DriveKitVehicleUIInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        if (DriveKit.isAutoInitEnabled(context)) {
            DriveKitVehicleUI.initialize()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(DriveKitInitializer::class.java)
    }
}
