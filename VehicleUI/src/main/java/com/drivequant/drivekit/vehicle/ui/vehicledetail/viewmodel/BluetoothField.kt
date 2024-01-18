package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R

enum class BluetoothField : Field {
    NAME,
    MAC_ADDRESS;

    override fun getTitle(context: Context, vehicle: Vehicle): String? {
        return when (this) {
            NAME -> R.string.dk_bluetooth_name
            MAC_ADDRESS -> R.string.dk_bluetooth_address
        }.let {
            context.getString(it)
        }
    }

    override fun getValue(context: Context, vehicle: Vehicle): String? {
        return when (this) {
            NAME -> vehicle.bluetooth?.name
            MAC_ADDRESS -> vehicle.bluetooth?.macAddress
        }
    }

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return vehicle.detectionMode == DetectionMode.BLUETOOTH
                && vehicle.bluetooth != null
    }

    override fun getErrorDescription(context: Context, value: String, vehicle: Vehicle): String? {
        return null
    }

    override fun onFieldUpdated(context: Context, value: String, vehicle: Vehicle, listener: FieldUpdatedListener) {
        // do nothing
    }
}
