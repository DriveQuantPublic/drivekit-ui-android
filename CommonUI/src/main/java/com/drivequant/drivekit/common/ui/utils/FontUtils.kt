package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI

object FontUtils {

    fun overrideFonts(context: Context?, v: View) {
        try {
            if (v is ViewGroup) {
                for (i in 0 until v.childCount) {
                    val child = v.getChildAt(i)
                    overrideFonts(context, child)
                }
            } else if (v is TextView) {
                context?.let {
                    v.typeface = DriveKitUI.primaryFont(it)
                }
            }
        } catch (e: Exception) {
        }
    }
}