package com.drivekit.demoapp.config

import android.app.Application
import android.graphics.Color
import android.support.v4.content.ContextCompat
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.graphical.DKFonts

/**
 * Created by Mohamed on 2020-03-11.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class ColorConfig(private val application: Application) : DKColors() {

    override fun secondaryColor(): Int = ContextCompat.getColor(application, R.color.colorPrimaryDark)

    override fun backgroundViewColor(): Int = Color.RED
}