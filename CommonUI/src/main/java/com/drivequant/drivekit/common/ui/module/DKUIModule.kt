package com.drivequant.drivekit.common.ui.module

internal enum class DKUIModule {
    VEHICLE;

    fun getClassForModule() = try {
        Class.forName(this.getClassName())
    } catch (e: Exception) {
        null
    }

    private fun getClassName(): String {
        val basePackageName = "com.drivequant.drivekit."
        when (this) {
            VEHICLE -> "vehicle.ui.DriveKitVehicleUI"
        }.let {
            return basePackageName + it
        }
    }
}