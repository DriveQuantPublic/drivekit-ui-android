package com.drivequant.drivekit.vehicle.ui.picker.commons

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat

object ResourceUtils {
    fun convertToDrawable(context: Context, identifier: String): Drawable? {
        val id = context.resources.getIdentifier(identifier, "drawable", context.packageName)
        return if (id > 0){
            ContextCompat.getDrawable(context, id)
        } else {
            null
        }
    }

    fun convertToString(context: Context, identifier: String): String? {
        val id = context.resources.getIdentifier(identifier, "string", context.packageName)
        return if (id > 0) {
            context.getString(id)
        } else {
            null
        }
    }
}