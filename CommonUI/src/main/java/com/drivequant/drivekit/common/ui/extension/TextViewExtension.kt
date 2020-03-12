package com.drivequant.drivekit.common.ui.extension

import android.graphics.Typeface
import android.view.Gravity
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI

@JvmOverloads
fun TextView.headLine1(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    this.textSize = 16f
    this.setTextColor(textColor)
    this.setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
}

@JvmOverloads
fun TextView.headLine2(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    this.textSize = 14f
    this.setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
    this.setTextColor(textColor)
}

@JvmOverloads
fun TextView.bigText(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    this.textSize = 16f
    this.setTextColor(textColor)
    this.typeface = DriveKitUI.primaryFont(context)
}

@JvmOverloads
fun TextView.normalText(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    this.textSize = 14f
    this.setTextColor(textColor)
    this.typeface = DriveKitUI.primaryFont(context)
}

@JvmOverloads
fun TextView.smallText(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    this.textSize = 12f
    this.setTextColor(textColor)
    this.typeface = DriveKitUI.primaryFont(context)
}

fun TextView.buttonText(textColor: Int = DriveKitUI.colors.fontColorOnSecondaryColor()) {
    this.bigText(textColor)
    this.setBackgroundColor(DriveKitUI.colors.secondaryColor())
    this.isClickable = true
    this.isFocusable = true
    this.gravity = Gravity.CENTER
}

