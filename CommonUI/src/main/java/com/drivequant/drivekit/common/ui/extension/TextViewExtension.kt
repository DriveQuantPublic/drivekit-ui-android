@file:JvmName("DKTextViewUtils")
package com.drivequant.drivekit.common.ui.extension

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R

fun TextView.pixelToSp(size: Float) = this.setTextSize(TypedValue.COMPLEX_UNIT_PX,size)

@JvmOverloads
fun TextView.headLine1(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    this.pixelToSp(context.resources.getDimension(R.dimen.dk_text_medium))
    this.setTextColor(textColor)
    this.setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
}

@JvmOverloads
fun TextView.headLine2(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    this.pixelToSp(context.resources.getDimension(R.dimen.dk_text_normal))
    this.setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
    this.setTextColor(textColor)
}

@JvmOverloads
fun TextView.bigText(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    this.pixelToSp(context.resources.getDimension(R.dimen.dk_text_medium))
    this.setTextColor(textColor)
    this.typeface = DriveKitUI.primaryFont(context)
}

@JvmOverloads
fun TextView.normalText(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    this.pixelToSp(context.resources.getDimension(R.dimen.dk_text_normal))
    this.setTextColor(textColor)
    this.typeface = DriveKitUI.primaryFont(context)
}

@JvmOverloads
fun TextView.smallText(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    this.pixelToSp(context.resources.getDimension(R.dimen.dk_text_small))
    this.setTextColor(textColor)
    this.typeface = DriveKitUI.primaryFont(context)
}

@JvmOverloads
fun TextView.buttonText(textColor: Int = DriveKitUI.colors.fontColorOnSecondaryColor(), backgroundColor: Int = DriveKitUI.colors.secondaryColor()) {
    this.normalText(textColor)
    this.typeface = Typeface.DEFAULT_BOLD
    this.isAllCaps = true
    this.setBackgroundColor(backgroundColor)
    this.isClickable = true
    this.isFocusable = true
    this.gravity = Gravity.CENTER
}