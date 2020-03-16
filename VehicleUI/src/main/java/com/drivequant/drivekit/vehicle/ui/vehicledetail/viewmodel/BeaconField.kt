package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.Vehicle

enum class BeaconField : Field {
    UNIQUE_CODE,
    MAJOR,
    MINOR;

    override fun getTitle(context: Context, vehicle: Vehicle): String {
        return when (this){
            UNIQUE_CODE -> "Identifiant du beacon"
            MAJOR -> "Major"
            MINOR -> "Minor"
        }
    }

    override fun getValue(context: Context, vehicle: Vehicle, allVehicles: List<Vehicle>): String? {
        return when (this){
            UNIQUE_CODE -> vehicle.beacon?.code
            MAJOR -> vehicle.beacon?.major.toString()
            MINOR -> vehicle.beacon?.minor.toString()
        }
    }
}