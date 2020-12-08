package com.drivequant.drivekit.ui.extension

import com.drivequant.drivekit.databaseutils.entity.TransportationMode

fun TransportationMode.isAlternative(): Boolean{
    return when (this){
        TransportationMode.UNKNOWN,
        TransportationMode.CAR,
        TransportationMode.MOTO -> false
        TransportationMode.TRUCK ,
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