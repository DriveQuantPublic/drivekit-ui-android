package com.drivequant.drivekit.vehicle.ui.picker.model

import java.io.Serializable

data class VehiclePickerItem(
    val id: Int,
    val text: String,
    val value: String,
    val drawableId: Int? = null
) : Serializable