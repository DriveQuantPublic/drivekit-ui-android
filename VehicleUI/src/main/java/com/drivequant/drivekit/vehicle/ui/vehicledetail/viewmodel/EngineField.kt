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
        return when (this){
            MOTOR -> vehicle.getEngineTypeName(context)
            CONSUMPTION -> DKDataFormatter.formatConsumption(context, vehicle.consumption)
        }
    }

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return true
    }

    override fun onFieldUpdated(fieldType: String, fieldValue: String, vehicle: Vehicle) {
        // do nothing
    }
}