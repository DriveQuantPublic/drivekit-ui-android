package com.drivequant.drivekit.vehicle.ui.vehicledetail.viewmodel

import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.ui.extension.computeSubtitle

enum class GeneralField : Field {
    NAME,
    CATEGORY;

    override fun getTitle(context: Context, vehicle: Vehicle): String {
        return when (this){
            NAME -> "Nom du véhicule"
            CATEGORY -> "Catégorie"
        }
    }

    override fun getValue(context: Context, vehicle: Vehicle, allVehicles: List<Vehicle>): String? {
        return when (this){
            NAME -> vehicle.computeSubtitle(context, allVehicles)
            CATEGORY -> "HC Category"// TODO mock
        }
    }
}