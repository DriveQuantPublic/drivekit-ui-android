package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import android.text.InputType
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.extension.computeTitle
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

    override fun getValue(context: Context, vehicle: Vehicle, allVehicles: List<Vehicle>): String? {
        return when (this){
            NAME -> vehicle.computeTitle(context, allVehicles)
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

    override fun alwaysDisplayable(vehicle: Vehicle): Boolean {
        return true
    }
}