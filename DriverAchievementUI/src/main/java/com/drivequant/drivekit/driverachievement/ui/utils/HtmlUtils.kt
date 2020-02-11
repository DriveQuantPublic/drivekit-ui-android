package com.drivequant.drivekit.driverachievement.ui.utils

import android.content.Context
import android.support.v4.content.ContextCompat

import com.drivequant.drivekit.driverachievement.ui.R

/**
 * Created by Mohamed on 11/02/2020.
 */

object HtmlUtils {
    fun getTextHighlight(text: String, context: Context): String {
        return "<span style=\"color:" + ContextCompat.getColor(
            context,
            R.color.dk_primary_color
        ) + "\">" + text + "</span>"
    }
}
