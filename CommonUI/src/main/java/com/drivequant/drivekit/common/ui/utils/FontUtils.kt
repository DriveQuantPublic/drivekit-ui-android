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
                val vg = v
                for (i in 0 until vg.childCount) {
                    val child = vg.getChildAt(i)
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