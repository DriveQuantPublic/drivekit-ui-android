package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.support.annotation.Keep
import com.drivequant.drivekit.vehicle.enum.VehicleBrand

@Keep
data class VehicleBrandItem(
    val brand: VehicleBrand,
    val isCar: Boolean,
    val isMotorbike: Boolean,
    val isTruck: Boolean,
    val icon: Int?)

fun buildFromCSV(source: Array<String>): VehicleBrandItem {
    return VehicleBrandItem(
        VehicleBrand.getEnumByValue(source[0]),
        source[1].toBoolean(),
        source[2].toBoolean(),
        source[3].toBoolean(),
        source[4].toIntOrNull() // TODO
    )
}