package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import com.drivequant.drivekit.vehicle.enum.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.ui.picker.commons.ResourceUtils

data class VehicleEngineItem(
    val engine: VehicleEngineIndex,
    val title: String?,
    val isCar: Boolean,
    val isMotorbike: Boolean,
    val isTruck: Boolean)

fun buildVehicleEngineItem(context: Context, source: Array<String>): VehicleEngineItem {
    return VehicleEngineItem(
        VehicleEngineIndex.getEnumByValue(source[0]),
        ResourceUtils.convertToString(context, source[1]),
        source[2].toBoolean(),
        source[3].toBoolean(),
        source[4].toBoolean()
    )
}