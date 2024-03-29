package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import androidx.annotation.Keep
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.extension.getGearBoxName

enum class CharacteristicField : Field {
    POWER,
    GEARBOX,
    CAR_MASS,
    TRUCK_MASS,
    PTAC;

    @Keep
    companion object {
        fun getFields(vehicle: Vehicle): List<CharacteristicField> {
            val fields = mutableListOf<CharacteristicField>()
            VehicleType.getVehicleType(vehicle.typeIndex)?.let { vehicleType ->
                val list = when (vehicleType){
                    VehicleType.CAR -> listOf(POWER, GEARBOX, CAR_MASS)
                    VehicleType.TRUCK -> listOf(TRUCK_MASS, PTAC)
                }
                fields.addAll(list)
            }
            return fields
        }
    }

    override fun getTitle(context: Context, vehicle: Vehicle): String? {
        return when (this) {
            POWER -> R.string.dk_power
            GEARBOX -> R.string.dk_gearbox
            CAR_MASS -> R.string.dk_mass
            TRUCK_MASS -> R.string.dk_vehicle_curbweight
            PTAC -> R.string.dk_vehicle_ptac
        }.let { context.getString(it) }
    }

    override fun getValue(context: Context, vehicle: Vehicle): String? {
        return when (this) {
            POWER -> DKDataFormatter.formatVehiclePower(context, vehicle.power)
            GEARBOX -> vehicle.getGearBoxName(context)
            CAR_MASS -> DKDataFormatter.formatMass(context, vehicle.mass)
            TRUCK_MASS -> DKDataFormatter.formatMassInTon(context, vehicle.mass)
            PTAC -> {
                vehicle.ptac?.let {
                    DKDataFormatter.formatMassInTon(context, it)
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
