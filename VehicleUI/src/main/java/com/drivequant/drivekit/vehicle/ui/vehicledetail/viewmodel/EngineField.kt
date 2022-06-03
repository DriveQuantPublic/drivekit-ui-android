package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.extension.getEngineTypeName

enum class EngineField : Field {
    MOTOR,
    CONSUMPTION;

    override fun getTitle(context: Context, vehicle: Vehicle): String? {
        val identifier = when (this){
            MOTOR -> "dk_motor"
            CONSUMPTION -> "dk_consumption"
        }
        return DKResource.convertToString(context, identifier)
    }

    override fun getValue(context: Context, vehicle: Vehicle): String? {
        return when (this) {
            MOTOR -> vehicle.getEngineTypeName(context)
            CONSUMPTION -> {
                if (vehicle.engineIndex == 3) {
                    DKDataFormatter.formatEnergyUsed(context, vehicle.consumption)
                } else {
                    DKDataFormatter.formatFuelConsumption(context, vehicle.consumption)
                }
            }
        }
    }

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return true
    }

    override fun getErrorDescription(context: Context, value: String, vehicle: Vehicle): String? {
        return null
    }

    override fun onFieldUpdated(context: Context, value: String, vehicle: Vehicle, listener: FieldUpdatedListener) {
        // do nothing
    }
}