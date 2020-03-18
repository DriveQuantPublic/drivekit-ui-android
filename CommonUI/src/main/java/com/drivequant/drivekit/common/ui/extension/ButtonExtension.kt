@file:JvmName("DKButtonUtils")
package com.drivequant.drivekit.common.ui.extension

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.Button
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