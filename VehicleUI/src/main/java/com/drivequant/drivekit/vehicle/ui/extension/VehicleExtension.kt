package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.DetectionMode.*
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehicleTypeItem

fun Vehicle.computeTitle(context: Context): String {
    val defaultName = getDefaultTitle(this)
    return if (this.name == defaultName){
        context.getString(R.string.dk_vehicle_my_vehicle) + " - position" // TODO compute position in list
    } else {
        this.name?.let {
            it
        }?: run {
            defaultName
        }
    }
}

fun Vehicle.computeSubtitle(context: Context): String {
    val defaultName = getDefaultTitle(this)
    return if (this.liteConfig){
        VehicleTypeItem.CAR.getCategories(context).first { it.liteConfigDqIndex == this.dqIndex}.title?.let {
            it
        }?: run {
            defaultName
        }
    } else {
        defaultName
    }
}

fun Vehicle.isConfigured(): Boolean {
    return when (this.detectionMode){
        DISABLED, GPS -> true
        BEACON -> this.beacon != null
        BLUETOOTH -> this.bluetooth != null
    }
}

fun Vehicle.getDeviceDisplayIdentifier(): String {
    return when (this.detectionMode){
        DISABLED, GPS -> ""
        BEACON -> this.beacon?.code ?: run { "" }
        BLUETOOTH -> this.bluetooth?.name ?: run { "" }
    }
}

private fun getDefaultTitle(vehicle: Vehicle): String {
    return "${vehicle.brand} ${vehicle.model} ${vehicle.version}"
}