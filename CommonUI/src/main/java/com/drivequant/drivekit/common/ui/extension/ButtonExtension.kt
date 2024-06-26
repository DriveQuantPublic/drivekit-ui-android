@file:JvmName("DKButtonUtils")
package com.drivequant.drivekit.common.ui.extension

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.Button
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.graphical.DKColors

/**
 * Created by Mohamed on 2020-03-11.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

fun Button.button(@ColorInt textColor: Int = DKColors.fontColorOnSecondaryColor, @ColorInt backgroundColor: Int = DKColors.secondaryColor) {
    this.setTextColor(textColor)
    this.setBackgroundColor(backgroundColor)
    this.setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
    this.isAllCaps = true
    this.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.dk_text_normal))
    this.isClickable = true
    this.isFocusable = true
    with(TypedValue()) {
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
        foreground = ContextCompat.getDrawable(context, resourceId)
    }
}
