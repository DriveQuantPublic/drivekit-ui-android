package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import android.text.InputType
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleManagerStatus
import com.drivequant.drivekit.vehicle.manager.VehicleRenameQueryListener
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import com.drivequant.drivekit.vehicle.ui.extension.getCategoryName

enum class GeneralField : Field {
    NAME,
    CATEGORY,
    BRAND,
    MODEL,
    VERSION;

    override fun getTitle(context: Context, vehicle: Vehicle): String? {
        val identifier = when (this){
            NAME -> "dk_name"
            CATEGORY -> "dk_category"
            BRAND -> "dk_brand"
            MODEL -> "dk_model"
            VERSION -> "dk_version"
        }
        return DKResource.convertToString(context, identifier)
    }

    override fun getValue(context: Context, vehicle: Vehicle): String? {
        return when (this){
            NAME -> {
                vehicle.buildFormattedName(context)
            }
            CATEGORY -> vehicle.getCategoryName(context)
            BRAND -> vehicle.brand
            MODEL -> vehicle.model
            VERSION -> vehicle.version
        }
    }

    override fun isEditable(): Boolean {
        return when (this){
            NAME -> true
            else -> false
        }
    }

    override fun getKeyboardType(): Int? {
        return when (this){
            NAME -> InputType.TYPE_CLASS_TEXT
            else -> null
        }
    }

    override fun isValid(value: String, vehicle: Vehicle): Boolean {
        var isValid = false
        if (this == NAME && value.length <= 50){
            isValid = true
        }
        return isValid
    }

    override fun getErrorDescription(context: Context, value: String, vehicle: Vehicle): String? {
        return if (this == NAME) {
            DKResource.convertToString(context, "dk_vehicle_field_name_error")
        } else {
            null
        }
    }

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return true
    }

    override fun onFieldUpdated(context: Context, value: String, vehicle: Vehicle, listener: FieldUpdatedListener) {
        when (this) {
            NAME -> {
                DriveKitVehicle.renameVehicle(value, vehicle, object : VehicleRenameQueryListener {
                    override fun onResponse(status: VehicleManagerStatus) {
                        if (status == VehicleManagerStatus.SUCCESS) {
                            listener.onFieldUpdated(
                                true,
                                DKResource.convertToString(context, "dk_change_success")
                            )
                        } else {
                            listener.onFieldUpdated(
                                false,
                                DKResource.convertToString(context, "dk_vehicle_error_message")
                            )
                        }
                    }
                })
            }
            else -> { }
        }
    }
}