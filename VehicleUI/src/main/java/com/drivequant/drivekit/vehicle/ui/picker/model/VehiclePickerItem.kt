package com.drivequant.drivekit.vehicle.ui.picker.model

import android.graphics.drawable.Drawable
import java.io.Serializable

data class VehiclePickerItem(
    val id: Int,
    val text: String?,
    val value: String,
    val icon1: Drawable? = null,
    val icon2: Drawable? = null
) : Serializable