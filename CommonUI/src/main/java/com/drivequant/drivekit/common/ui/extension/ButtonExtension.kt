@file:JvmName("DKButtonUtils")
package com.drivequant.drivekit.common.ui.extension

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.TypedValue
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R

/**
 * Created by Mohamed on 2020-03-11.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

fun Button.button(textColor: Int = DriveKitUI.colors.fontColorOnSecondaryColor(), backgroundColor: Int = DriveKitUI.colors.secondaryColor()) {
    this.setTextColor(textColor)
    this.setBackgroundColor(backgroundColor)
    this.setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
    this.isAllCaps = true
    this.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.dk_text_normal))
    this.isClickable = true
    this.isFocusable = true
}

fun Button.selectorButton(context: Context, color: Int) {
    val drawableClicked = ContextCompat.getDrawable(context, R.drawable.dk_button_clicked)?.apply {
        (this as GradientDrawable).setColor(ColorUtils.setAlphaComponent(color,40))
        setStroke(8, color)
    }

    val drawableFocused = ContextCompat.getDrawable(context, R.drawable.dk_button_clicked)?.apply {
        (this as GradientDrawable).setColor(ColorUtils.setAlphaComponent(color,40))
        setStroke(8, color)
    }
    val drawableBackground = ContextCompat.getDrawable(context, R.drawable.dk_button_background)?.apply {
        tintDrawable(color)
    }
    this.background = StateListDrawable().apply {
        addState(intArrayOf(android.R.attr.state_pressed), drawableClicked)
        addState(intArrayOf(android.R.attr.state_focused), drawableFocused)
        addState(intArrayOf(), drawableBackground)
    }
}