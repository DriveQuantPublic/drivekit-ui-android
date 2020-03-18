package com.drivekit.demoapp.vehicle

import android.content.Context
import android.text.InputType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.Field

class DemoCustomField : Field {

    override fun getTitle(context: Context, vehicle: Vehicle): String? {
        return "DemoCustomField title"
    }

    override fun getValue(context: Context, vehicle: Vehicle, allVehicles: List<Vehicle>): String? {
        return "DemoCustomField value"
    }

    override fun isEditable(): Boolean {
        return true
    }

    override fun getKeyboardType(): Int? {
        return InputType.TYPE_CLASS_TEXT
    }

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return true
    }

    override fun isValid(): Boolean {
        return true // TOD: send input value
    }

    override fun onFieldUpdated(vehicle: Vehicle) {
        // TODO:
    }
}