package com.drivequant.drivekit.common.ui.extension

import android.graphics.Typeface
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI

@JvmOverloads
fun TextView.headLine1(textColor: Int = DriveKitUI.colors.complementaryFontColor()) {
    this.textSize = 16f
    this.typeface = Typeface.DEFAULT_BOLD
    this.setTextColor(textColor)
}

@JvmOverloads
fun TextView.headLine2(textColor: Int = DriveKitUI.colors.complementaryFontColor()) {
    this.textSize = 14f
    this.typeface = Typeface.DEFAULT_BOLD
    this.setTextColor(textColor)
}

@JvmOverloads
fun TextView.bigText(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    this.textSize = 16f
    this.setTextColor(textColor)
}

@JvmOverloads
fun TextView.smallText(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    this.textSize = 12f
    this.setTextColor(textColor)
}
