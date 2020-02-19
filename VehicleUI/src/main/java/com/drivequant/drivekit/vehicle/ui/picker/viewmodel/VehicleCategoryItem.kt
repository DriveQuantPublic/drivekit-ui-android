package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import com.drivequant.drivekit.vehicle.ui.picker.commons.ResourceUtils

data class VehicleCategoryItem(
    val category: String,
    val title: String?,
    val icon1: Drawable?,
    val icon2: Drawable?,
    val description: String?,
    val liteConfigDqIndex: String,
    val isCar: Boolean,
    val isMotorbike: Boolean,
    val isTruck: Boolean)

fun buildVehicleCategoryItem(context: Context, source: Array<String>): VehicleCategoryItem {
    return VehicleCategoryItem(
        source[0],
        ResourceUtils.convertToString(context, source[1]),
        ResourceUtils.convertToDrawable(context, source[2]),
        ResourceUtils.convertToDrawable(context, source[3]),
        source[4],
        source[5],
        source[6].toBoolean(),
        source[7].toBoolean(),
        source[8].toBoolean()
    )
}
