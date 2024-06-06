@file:JvmName("DKTextViewUtils")
package com.drivequant.drivekit.common.ui.extension

import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.ColorInt
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.graphical.DKStyle

fun TextView.pixelToSp(size: Float) = this.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)

fun TextView.headLine1() {
    setStyle(DKStyle.HEADLINE1)
}

@JvmOverloads
fun TextView.headLine1WithColor(@ColorInt textColor: Int = DKColors.mainFontColor) {
    setStyleWithColor(DKStyle.HEADLINE1, textColor)
}

fun TextView.headLine2() {
    setStyle(DKStyle.HEADLINE2)
}

@JvmOverloads
fun TextView.headLine2WithColor(@ColorInt textColor: Int = DKColors.mainFontColor) {
    setStyleWithColor(DKStyle.HEADLINE2, textColor)
}

fun TextView.bigText() {
    setStyle(DKStyle.BIG_TEXT)
}

fun TextView.bigTextWithColor(@ColorInt textColor: Int = DKColors.mainFontColor) {
    setStyleWithColor(DKStyle.BIG_TEXT, textColor)
}

fun TextView.normalText() {
    setStyle(DKStyle.NORMAL_TEXT)
}

@JvmOverloads
fun TextView.normalTextWithColor(@ColorInt textColor: Int = DKColors.mainFontColor) {
    setStyleWithColor(DKStyle.NORMAL_TEXT, textColor)
}

@JvmOverloads
fun TextView.smallText(isTypeFaceBold: Boolean = false) {
    setStyle(DKStyle.SMALL_TEXT, if (isTypeFaceBold) Typeface.BOLD else Typeface.NORMAL)
}

@JvmOverloads
fun TextView.smallTextWithColor(@ColorInt textColor: Int = DKColors.mainFontColor, isTypeFaceBold: Boolean = false) {
    setStyleWithColor(DKStyle.SMALL_TEXT, textColor, if (isTypeFaceBold) Typeface.BOLD else Typeface.NORMAL)
}

@JvmOverloads
fun TextView.buttonText(@ColorInt textColor: Int = DKColors.fontColorOnSecondaryColor, @ColorInt backgroundColor: Int = DKColors.secondaryColor) {
    this.normalTextWithColor(textColor)
    this.typeface = Typeface.DEFAULT_BOLD
    this.isAllCaps = true
    this.setBackgroundColor(backgroundColor)
    this.isClickable = true
    this.isFocusable = true
    this.gravity = Gravity.CENTER
}

fun TextView.highlightSmall() {
    setStyle(DKStyle.HIGHLIGHT_SMALL)
}

@JvmOverloads
fun TextView.highlightSmallWithColor(@ColorInt textColor: Int = DKColors.mainFontColor) {
    setStyleWithColor(DKStyle.HIGHLIGHT_SMALL, textColor)
}

fun TextView.highlightMedium() {
    setStyle(DKStyle.HIGHLIGHT_NORMAL)
}

@JvmOverloads
fun TextView.highlightMediumWithColor(@ColorInt textColor: Int = DKColors.mainFontColor) {
    setStyleWithColor(DKStyle.HIGHLIGHT_NORMAL, textColor)
}

fun TextView.highlightBig() {
    setStyle(DKStyle.HIGHLIGHT_BIG)
}

@JvmOverloads
fun TextView.highlightBigWithColor(@ColorInt textColor: Int = DKColors.mainFontColor) {
    setStyleWithColor(DKStyle.HIGHLIGHT_BIG, textColor)
}

private fun TextView.setStyleWithColor(style: DKStyle, @ColorInt textColor: Int, forceTypeFaceStyle: Int? = null) {
    this.setTextColor(textColor)
    setStyle(style, forceTypeFaceStyle)
}

private fun TextView.setStyle(style: DKStyle, forceTypeFaceStyle: Int? = null) {
    this.pixelToSp(context.resources.getDimension(style.dimensionId()))
    this.setTypeface(DriveKitUI.primaryFont(context), forceTypeFaceStyle ?: style.typefaceStyle())
}
