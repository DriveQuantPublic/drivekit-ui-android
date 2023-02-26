@file:JvmName("DKTextViewUtils")
package com.drivequant.drivekit.common.ui.extension

import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.ColorInt
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.graphical.DKStyle

fun TextView.pixelToSp(size: Float) = this.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)

@JvmOverloads
fun TextView.headLine1(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    setStyle(DKStyle.HEADLINE1, textColor)
}

@JvmOverloads
fun TextView.headLine2(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    setStyle(DKStyle.HEADLINE2, textColor)
}

@JvmOverloads
fun TextView.bigText(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    setStyle(DKStyle.BIG_TEXT, textColor)
}

@JvmOverloads
fun TextView.normalText(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    setStyle(DKStyle.NORMAL_TEXT, textColor)
}

@JvmOverloads
fun TextView.smallText(textColor: Int = DriveKitUI.colors.mainFontColor(), isTypeFaceBold: Boolean = false) {
    setStyle(DKStyle.SMALL_TEXT, textColor, if (isTypeFaceBold) Typeface.BOLD else Typeface.NORMAL)
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

@JvmOverloads
fun TextView.highlightSmall(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    setStyle(DKStyle.HIGHLIGHT_SMALL, textColor)
}

@JvmOverloads
fun TextView.highlightMedium(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    setStyle(DKStyle.HIGHLIGHT_NORMAL, textColor)
}

@JvmOverloads
fun TextView.highlightBig(textColor: Int = DriveKitUI.colors.mainFontColor()) {
    setStyle(DKStyle.HIGHLIGHT_BIG, textColor)
}

private fun TextView.setStyle(style: DKStyle, @ColorInt textColor: Int, forceTypeFaceStyle: Int? = null) {
    this.pixelToSp(context.resources.getDimension(style.dimensionId()))
    this.setTextColor(textColor)
    this.setTypeface(DriveKitUI.primaryFont(context), forceTypeFaceStyle ?: style.typefaceStyle())
}
