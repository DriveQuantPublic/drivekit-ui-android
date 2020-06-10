package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import android.text.InputType
import com.drivequant.drivekit.databaseutils.entity.Vehicle

interface Field {
    fun getTitle(context: Context, vehicle: Vehicle): String?
    fun getDescription(context: Context, vehicle: Vehicle): String? = null
    fun getValue(context: Context, vehicle: Vehicle): String?
    fun isEditable(): Boolean = false
    fun getKeyboardType(): Int? = InputType.TYPE_CLASS_TEXT
    fun alwaysDisplayable(vehicle: Vehicle): Boolean = false
    fun isValid(value: String, vehicle: Vehicle): Boolean = true
    fun getErrorDescription(context: Context, value: String, vehicle: Vehicle): String?
    fun onFieldUpdated(context: Context, value: String, vehicle: Vehicle, listener: FieldUpdatedListener)
}

interface FieldUpdatedListener {
    fun onFieldUpdated(success: Boolean, message: String)
}