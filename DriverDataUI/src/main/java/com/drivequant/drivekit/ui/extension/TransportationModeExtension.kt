package com.drivequant.drivekit.ui.extension

import com.drivequant.drivekit.databaseutils.entity.TransportationMode

fun TransportationMode.isAlternative(): Boolean{
    return when (this){
        TransportationMode.UNKNOWN,
        TransportationMode.CAR,
        TransportationMode.MOTO,
        TransportationMode.TRUCK -> false
        TransportationMode.BUS,
        TransportationMode.TRAIN,
        TransportationMode.BOAT,
        TransportationMode.BIKE,
        TransportationMode.FLIGHT,
        TransportationMode.SKIING,
        TransportationMode.ON_FOOT,
        TransportationMode.IDLE,
        TransportationMode.OTHER -> true
    }
}

// TODO
fun TransportationMode.image(): String {
    return "dk_common_distraction_filled"
}

// TODO
fun TransportationMode.text(): String {
    return this.name
}