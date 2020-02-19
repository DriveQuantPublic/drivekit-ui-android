package com.drivequant.drivekit.vehicle.ui.picker.commons

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat

object ResourceUtils {
    fun convertToDrawable(context: Context, identifier: String): Drawable? {
        val id = context.resources.getIdentifier(identifier, "drawable", context.packageName)
        return ContextCompat.getDrawable(context, id)
    }

    fun convertToString(context: Context, identifier: String): String? {
        val id = context.resources.getIdentifier(identifier, "string", context.packageName)
        return context.getString(id)
    }
}