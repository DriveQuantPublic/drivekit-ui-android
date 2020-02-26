package com.drivequant.drivekit.common.ui

import android.graphics.Color

interface DKColors {
    var primaryColor: Int
    var secondaryColor: Int
    var mainFontColor: Int
    var complementaryFontColor: Int
    var fontColorOnPrimaryColor: Int
    var fontColorOnSecondaryColor: Int
    var warningColor: Int
    var criticalColor: Int
}

class DKDefaultColors : DKColors {
    override var primaryColor: Int = Color.parseColor("#0B4D6E")
    override var secondaryColor: Int = Color.parseColor("#77E2B2")
    override var mainFontColor: Int = Color.parseColor("#616161")
    override var complementaryFontColor: Int = Color.parseColor("#9E9E9E")
    override var fontColorOnPrimaryColor: Int = Color.WHITE
    override var fontColorOnSecondaryColor: Int = Color.WHITE
    override var warningColor: Int = Color.parseColor("#FF6E57")
    override var criticalColor: Int = Color.parseColor("#E52027")
}



