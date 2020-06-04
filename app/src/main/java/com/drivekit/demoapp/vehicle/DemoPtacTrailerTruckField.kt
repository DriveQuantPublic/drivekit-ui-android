package com.drivekit.demoapp.vehicle

import android.content.Context
import android.text.InputType
import android.text.TextUtils
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.Field
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.FieldUpdatedListener

class DemoPtacTrailerTruckField : Field {

    override fun getTitle(context: Context, vehicle: Vehicle): String? {
        return DKResource.convertToString(context, "dk_vehicle_ptac_truck_and_trailer")
    }

    override fun getDescription(context: Context, vehicle: Vehicle): String? {
        return DKResource.convertToString(context, "dk_vehicle_ptac_truck_and_trailer_info")
    }

    override fun getValue(context: Context, vehicle: Vehicle): String? {
        // TODO: retrieve the value you previously saved
        return ""
    }

    override fun isEditable(): Boolean {
        return true
    }

    override fun getKeyboardType(): Int? {
        return InputType.TYPE_CLASS_NUMBER
    }

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return VehicleType.getVehicleType(vehicle.typeIndex) == VehicleType.TRUCK
    }

    override fun isValid(value: String, vehicle: Vehicle): Boolean {
        return when {
            value.isEmpty() -> {
                true
            }
            TextUtils.isDigitsOnly(value) -> {
                val parsedValue = Integer.parseInt(value)
                (parsedValue >= vehicle.mass/1000 && parsedValue <= 44)
            }
            else -> {
                false
            }
        }
    }

    override fun getErrorDescription(context: Context, vehicle: Vehicle): String? {
        val minMass = String.format("%.0f", vehicle.mass/1000)
        val maxMass = 44
        return "Value must be between $minMass and $maxMass T"
    }

    override fun onFieldUpdated(context: Context, value: String, vehicle: Vehicle, listener: FieldUpdatedListener) {
        // TODO: save the value
    }
}