package com.drivequant.drivekit.common.ui.extension

import android.graphics.Typeface
import android.widget.Button
import com.drivequant.drivekit.common.ui.DriveKitUI

/**
 * Created by Mohamed on 2020-03-11.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

fun Button.button() {
    this.setTextColor(DriveKitUI.colors.fontColorOnSecondaryColor())
    this.setBackgroundColor(DriveKitUI.colors.secondaryColor())
    this.setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
    this.textSize = 14f
}