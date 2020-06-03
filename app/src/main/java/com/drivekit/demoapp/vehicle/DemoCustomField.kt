package com.drivekit.demoapp.vehicle

import android.content.Context
import android.text.InputType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.Field
import com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel.FieldUpdatedListener

class DemoCustomField : Field {

    override fun getTitle(context: Context, vehicle: Vehicle): String? {
        return "DemoCustomField example"
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

    override fun isValid(value: String, vehicle: Vehicle): Boolean {
        return (value.toLongOrNull() != null && value.toLong() <= 9999)
    }

    override fun getErrorDescription(context: Context): String? {
        return "Value must be below 999"
    }

    override fun onFieldUpdated(context: Context, value: String, vehicle: Vehicle, listener: FieldUpdatedListener) {

    }
}