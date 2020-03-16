package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
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

    override fun getValue(context: Context, vehicle: Vehicle, allVehicles: List<Vehicle>): String? {
        return when (this){
            MOTOR -> vehicle.getEngineTypeName(context)
            CONSUMPTION -> "${String.format("%.1f", vehicle.consumption)} ${context.getString(R.string.dk_common_unit_l_per_100km)}"
        }
    }

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return true
    }
}