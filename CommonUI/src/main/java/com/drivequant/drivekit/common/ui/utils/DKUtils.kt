package com.drivequant.drivekit.common.ui.utils

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable

object DKUtils {
     fun setBackgroundDrawableColor(background: GradientDrawable, color: Int) {
        when (background) {
            is ShapeDrawable -> (background as ShapeDrawable).paint.color = color
            is GradientDrawable -> background.setColor(color)
            is ColorDrawable -> (background as ColorDrawable).color = color
        }
    }
}