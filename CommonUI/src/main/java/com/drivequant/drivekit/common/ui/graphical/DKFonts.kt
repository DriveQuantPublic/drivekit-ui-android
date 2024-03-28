package com.drivequant.drivekit.common.ui.graphical

import androidx.annotation.FontRes
import com.drivequant.drivekit.common.ui.R

open class DKFonts {
    @FontRes
    open fun primaryFont(): Int = R.font.roboto
    @FontRes
    open fun secondaryFont(): Int = R.font.roboto
}
