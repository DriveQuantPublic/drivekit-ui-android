package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.extension.getGearBoxName

enum class CharacteristicField : Field {
    POWER,
    GEARBOX,
    MASS;

    override fun getTitle(context: Context, vehicle: Vehicle): String? {
        val identifier = when (this) {
            POWER -> "dk_power"
            GEARBOX -> "dk_gearbox"
            MASS -> "dk_mass"
        }
        return DKResource.convertToString(context, identifier)
    }

    override fun getValue(context: Context, vehicle: Vehicle, allVehicles: List<Vehicle>): String? {
        return when (this) {
            POWER -> String.format("%.0f ", vehicle.power) + DKResource.buildString(context, "dk_common_power_unit")
            GEARBOX -> vehicle.getGearBoxName(context)
            MASS -> String.format("%.0f ", vehicle.mass) + DKResource.buildString(context, "dk_common_unit_kg")
        }
    }

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return true
    }
}