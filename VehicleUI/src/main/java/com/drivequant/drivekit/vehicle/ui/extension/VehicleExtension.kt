package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.R

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
    return if (this.liteConfig){ // TODO verify why liteConfig is always false
        " véhicule simplifié "
    } else {
        defaultName
    }
}

private fun getDefaultTitle(vehicle: Vehicle): String {
    return "${vehicle.brand} ${vehicle.model} ${vehicle.version}"
}