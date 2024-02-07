package com.drivequant.drivekit.vehicle.ui.extension

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.ALFA_ROMEO
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.AUDI
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.BMW
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.CITROEN
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.DACIA
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.DAF_TRUCK
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.FIAT
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.FORD
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.HYUNDAI
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.IVECO_TRUCK
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.KIA
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.MAN_TRUCK
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.MERCEDES
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.MERCEDES_TRUCK
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.MINI
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.NISSAN
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.OPEL
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.PEUGEOT
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.RENAULT
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.RENAULT_TRUCK
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.SCANIA_TRUCK
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.SEAT
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.SKODA
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.TOYOTA
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.VOLKSWAGEN
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.VOLVO
import com.drivequant.drivekit.vehicle.enums.VehicleBrand.VOLVO_TRUCK
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.model.VehicleBrandItem

fun VehicleBrand.getTitle(): String {
    return this.name
}

fun VehicleBrand.getIcon(context: Context) : Drawable? {
    return when (this) {
        ALFA_ROMEO -> R.drawable.dk_alfa_romeo
        AUDI -> R.drawable.dk_audi
        BMW -> R.drawable.dk_bmw
        CITROEN -> R.drawable.dk_citroen
        DACIA -> R.drawable.dk_dacia
        DAF_TRUCK -> R.drawable.dk_daf_truck
        FIAT -> R.drawable.dk_fiat
        FORD -> R.drawable.dk_ford
        HYUNDAI -> R.drawable.dk_hyundai
        IVECO_TRUCK -> R.drawable.dk_iveco_truck
        KIA -> R.drawable.dk_kia
        MAN_TRUCK -> R.drawable.dk_man_truck
        MERCEDES -> R.drawable.dk_mercedes
        MERCEDES_TRUCK -> R.drawable.dk_mercedes_truck
        MINI -> R.drawable.dk_mini
        NISSAN -> R.drawable.dk_nissan
        OPEL -> R.drawable.dk_opel
        PEUGEOT -> R.drawable.dk_peugeot
        RENAULT -> R.drawable.dk_renault
        RENAULT_TRUCK -> R.drawable.dk_renault_truck
        SCANIA_TRUCK -> R.drawable.dk_scania_truck
        SEAT -> R.drawable.dk_seat
        SKODA -> R.drawable.dk_skoda
        TOYOTA -> R.drawable.dk_toyota
        VOLKSWAGEN -> R.drawable.dk_volkswagen
        VOLVO -> R.drawable.dk_volvo
        VOLVO_TRUCK -> R.drawable.dk_volvo_truck
        else -> null
    }?.let { ContextCompat.getDrawable(context, it) }
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
