package com.drivequant.drivekit.vehicle.ui.picker.model

import android.graphics.drawable.Drawable
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import java.io.Serializable

data class VehiclePickerItem(
    val id: Int,
    val text: String?,
    val value: String,
    val icon1: Drawable? = null,
    val icon2: Drawable? = null
) : Serializable

data class VehicleCategoryItem(
    val category: String,
    val title: String?,
    val icon1: Drawable?,
    val icon2: Drawable?,
    val description: String?,
    val liteConfigDqIndex: String,
    val isCar: Boolean,
    val isMotorbike: Boolean,
    val isTruck: Boolean
) : Serializable

data class VehicleBrandItem(
    val brand: VehicleBrand,
    val isCar: Boolean,
    val isMotorbike: Boolean,
    val isTruck: Boolean,
    val icon: Drawable?
) : Serializable

data class VehicleEngineItem(
    val engine: VehicleEngineIndex,
    val title: String?,
    val isCar: Boolean,
    val isMotorbike: Boolean,
    val isTruck: Boolean
) : Serializable

