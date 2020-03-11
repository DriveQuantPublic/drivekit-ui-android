package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.Keep
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.enums.VehicleBrand

@Keep
data class VehicleBrandItem(
    val brand: VehicleBrand,
    val isCar: Boolean,
    val isMotorbike: Boolean,
    val isTruck: Boolean,
    val icon: Drawable?)

fun buildVehicleBrandItem(context: Context, source: Array<String>): VehicleBrandItem {
    return VehicleBrandItem(
        VehicleBrand.getEnumByName(source[0]),
        source[1].toBoolean(),
        source[2].toBoolean(),
        source[3].toBoolean(),
        DKResource.convertToDrawable(context, source[4])
    )
}
