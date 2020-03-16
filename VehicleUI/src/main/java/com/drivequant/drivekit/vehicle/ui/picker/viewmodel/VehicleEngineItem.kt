package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex

data class VehicleEngineItem(
    val engine: VehicleEngineIndex,
    val title: String?,
    val isCar: Boolean,
    val isMotorbike: Boolean,
    val isTruck: Boolean)

fun buildVehicleEngineItem(context: Context, source: Array<String>): VehicleEngineItem {
    return VehicleEngineItem(
        VehicleEngineIndex.getEnumByName(source[0]),
        DKResource.convertToString(context, source[1]),
        source[2].toBoolean(),
        source[3].toBoolean(),
        source[4].toBoolean()
    )
}