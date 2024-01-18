package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R

enum class BeaconField : Field {
    UNIQUE_CODE,
    MAJOR,
    MINOR;

    override fun getTitle(context: Context, vehicle: Vehicle): String? {
        return when (this) {
            UNIQUE_CODE -> R.string.dk_beacon_code
            MAJOR -> R.string.dk_vehicle_beacon_major
            MINOR -> R.string.dk_vehicle_beacon_minor
        }.let { context.getString(it) }
    }

    override fun getValue(context: Context, vehicle: Vehicle): String? {
        return when (this) {
            UNIQUE_CODE -> vehicle.beacon?.code
            MAJOR -> vehicle.beacon?.major.toString()
            MINOR -> vehicle.beacon?.minor.toString()
        }
    }

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return vehicle.detectionMode == DetectionMode.BEACON
                && vehicle.beacon != null
    }

    override fun getErrorDescription(context: Context, value: String, vehicle: Vehicle): String? {
        return null
    }

    override fun onFieldUpdated(context: Context, value: String, vehicle: Vehicle, listener: FieldUpdatedListener) {
        // do nothing
    }
}
