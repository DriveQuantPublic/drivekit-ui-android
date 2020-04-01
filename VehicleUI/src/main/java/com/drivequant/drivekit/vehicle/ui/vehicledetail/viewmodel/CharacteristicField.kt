package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
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

    override fun getValue(context: Context, vehicle: Vehicle): String? {
        return when (this) {
            POWER -> DKDataFormatter.formatVehiclePower(context, vehicle.power)
            GEARBOX -> vehicle.getGearBoxName(context)
            MASS -> DKDataFormatter.formatMass(context, vehicle.mass)
        }
    }

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return true
    }

    override fun onFieldUpdated(fieldType: String, fieldValue: String, vehicle: Vehicle) {
        // do nothing
    }
}