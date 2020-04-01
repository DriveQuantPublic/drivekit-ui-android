package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle

enum class BluetoothField : Field {
    NAME,
    MAC_ADDRESS;

    override fun getTitle(context: Context, vehicle: Vehicle): String? {
        val identifier = when (this){
            NAME -> "dk_bluetooth_name"
            MAC_ADDRESS -> "dk_bluetooth_address"
        }
        return DKResource.convertToString(context, identifier)
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

    override fun onFieldUpdated(fieldType: String, fieldValue: String, vehicle: Vehicle) {
        // do nothing
    }
}