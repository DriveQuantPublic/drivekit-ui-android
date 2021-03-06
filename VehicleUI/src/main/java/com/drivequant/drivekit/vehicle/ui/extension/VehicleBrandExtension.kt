package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.*
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleBrandItem

fun VehicleBrand.getTitle(): String {
    return this.name
}

fun VehicleBrand.getIcon(context: Context) : Drawable? {
    val identifier = when (this){
        ALFA_ROMEO -> "dk_alfa_romeo"
        AUDI -> "dk_audi"
        BMW -> "dk_bmw"
        CITROEN -> "dk_citroen"
        DACIA -> "dk_dacia"
        DAF_TRUCK -> "dk_daf_truck"
        FIAT -> "dk_fiat"
        FORD -> "dk_ford"
        HYUNDAI -> "dk_hyundai"
        IVECO_TRUCK -> "dk_iveco_truck"
        KIA -> "dk_kia"
        MAN_TRUCK -> "dk_man_truck"
        MERCEDES -> "dk_mercedes"
        MERCEDES_TRUCK -> "dk_mercedes_truck"
        MINI -> "dk_mini"
        NISSAN -> "dk_nissan"
        OPEL -> "dk_opel"
        PEUGEOT -> "dk_peugeot"
        RENAULT -> "dk_renault"
        RENAULT_TRUCK -> "dk_renault_truck"
        SCANIA_TRUCK -> "dk_scania_truck"
        SEAT -> "dk_seat"
        SKODA -> "dk_skoda"
        TOYOTA -> "dk_toyota"
        VOLKSWAGEN -> "dk_volkswagen"
        VOLVO -> "dk_volvo"
        VOLVO_TRUCK -> "dk_volvo_truck"
        else -> ""
    }
    return DKResource.convertToDrawable(context, identifier)
}

fun VehicleBrand.hasIcon(context: Context) : Boolean {
    return this.getIcon(context) != null
}

fun VehicleBrand.buildBrandItem(context: Context) : VehicleBrandItem {
    return VehicleBrandItem(
        this,
        icon = getIcon(context)
    )
}

fun VehicleBrand.getVehicleType() : VehicleType? {
    return when {
        this.isCar -> {
            VehicleType.CAR
        }
        this.isTruck -> {
            VehicleType.TRUCK
        }
        else -> {
            null
        }
    }
}