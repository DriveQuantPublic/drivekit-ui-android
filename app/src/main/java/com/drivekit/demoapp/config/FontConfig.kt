package com.drivekit.demoapp.config

import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.graphical.DKFonts

/**
 * Created by Mohamed on 2020-03-13.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class FontConfig : DKFonts() {
    override fun primaryFont(): Int = R.font.sketchy
    override fun secondaryFont(): Int = R.font.stocky
}