package com.drivekit.demoapp.vehicle

import android.content.Context
import android.text.InputType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.Field

class DemoCustomField : Field {

    override fun getTitle(context: Context, vehicle: Vehicle): String? {
        return "DemoCustomField title"
    }

    override fun getValue(context: Context, vehicle: Vehicle): String? {
        return "1234"
    }

    override fun isEditable(): Boolean {
        return true
    }

    override fun getKeyboardType(): Int? {
        return InputType.TYPE_CLASS_NUMBER
    }

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return true
    }

    override fun isValid(value: String): Boolean {
        return value.toLongOrNull() != null
    }

    override fun onFieldUpdated(fieldType: String, fieldValue: String, vehicle: Vehicle) {
        // TODO:
    }
}