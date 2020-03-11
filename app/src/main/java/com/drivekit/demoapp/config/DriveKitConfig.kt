package com.drivekit.demoapp.config

import android.graphics.Color
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.graphical.DKFonts

/**
 * Created by Mohamed on 2020-03-11.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class DriveKitConfig : DKColors, DKFonts {

    override fun secondaryColor(): Int = Color.GREEN

    override fun primaryFont(): Int = R.font.sketchy

    //override fun backgroundViewColor(): Int = Color.RED
}