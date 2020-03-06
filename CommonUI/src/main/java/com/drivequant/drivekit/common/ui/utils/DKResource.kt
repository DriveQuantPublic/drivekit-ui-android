package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat

object DKResource {

    fun convertToDrawable(context: Context, identifier: String): Drawable? {
        val id = context.resources.getIdentifier(identifier, "drawable", context.packageName)
        return ContextCompat.getDrawable(context, id)
    }

    fun convertToString(context: Context, identifier: String): String? {
        val id = context.resources.getIdentifier(identifier, "string", context.packageName)
        return context.getString(id)
    }
}
