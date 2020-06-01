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
        FIAT -> "dk_fiat"
        FORD -> "dk_ford"
        HYUNDAI -> "dk_hyundai"
        KIA -> "dk_kia"
        MERCEDES -> "dk_mercedes"
        MINI -> "dk_mini"
        NISSAN -> "dk_nissan"
        OPEL -> "dk_opel"
        PEUGEOT -> "dk_peugeot"
        RENAULT -> "dk_renault"
        SEAT -> "dk_seat"
        SKODA -> "dk_skoda"
        TOYOTA -> "dk_toyota"
        VOLKSWAGEN -> "dk_volkswagen"
        VOLVO -> "dk_volvo"
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