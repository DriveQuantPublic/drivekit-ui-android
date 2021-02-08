package com.drivekit.demoapp.config

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.graphical.DKColors

/**
 * Created by Mohamed on 2020-03-11.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class ColorConfig(private val context: Context) : DKColors() {

    override fun secondaryColor(): Int = ContextCompat.getColor(context, R.color.colorPrimaryDark)

    override fun backgroundViewColor(): Int = Color.RED
}