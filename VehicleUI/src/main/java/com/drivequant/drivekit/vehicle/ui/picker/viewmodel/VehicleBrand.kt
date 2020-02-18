package com.drivequant.drivekit.vehicle.ui.picker.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.Keep
import android.support.v4.content.ContextCompat
import com.drivequant.drivekit.vehicle.enum.VehicleBrand


@Keep
data class VehicleBrandItem(
    val brand: VehicleBrand,
    val isCar: Boolean,
    val isMotorbike: Boolean,
    val isTruck: Boolean,
    val icon: Drawable?)

fun buildFromCSV(context: Context, source: Array<String>): VehicleBrandItem {
    return VehicleBrandItem(
        VehicleBrand.getEnumByValue(source[0]),
        source[1].toBoolean(),
        source[2].toBoolean(),
        source[3].toBoolean(),
        convertToDrawable(context, source[4])
    )
}

fun convertToDrawable(context: Context, identifier: String): Drawable? {
    val id = context.resources.getIdentifier(identifier, "drawable", context.packageName)
    val drawable = ContextCompat.getDrawable(context, id)
    return drawable
}