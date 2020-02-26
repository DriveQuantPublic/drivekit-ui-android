package com.drivequant.drivekit.common.ui

interface DKFonts {
    var primaryFont: Int
    var secondaryFont: Int
}

class DKDefaultFont :DKFonts {
    override var primaryFont: Int = R.font.roboto_regular
    override var secondaryFont: Int = R.font.roboto_regular
}