package com.drivequant.drivekit.common.ui.utils

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import androidx.annotation.ColorInt
import kotlin.math.roundToInt

fun circleDrawable(
    size: Int,
    @ColorInt insideColor: Int = Color.WHITE,
    @ColorInt borderColor: Int,
    borderWidth: Float = 2.convertDpToPx().toFloat()
): Drawable {
    val backgroundColor: Int = insideColor
    val extra = (borderWidth / 2f).roundToInt()
    val mainShape = ShapeDrawable().apply {
        shape = RectShape()
        intrinsicWidth = size
        intrinsicHeight = size
        setPadding(extra, extra, extra, extra)
        paint.color = Color.TRANSPARENT
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth
    }
    val borderShape = ShapeDrawable().apply {
        shape = OvalShape()
        intrinsicWidth = size
        intrinsicHeight = size
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth
    }
    val backgroundShape = ShapeDrawable().apply {
        shape = OvalShape()
        intrinsicWidth = size
        intrinsicHeight = size
        paint.color = backgroundColor
        paint.style = Paint.Style.FILL
        paint.strokeWidth = borderWidth
    }
    return LayerDrawable(arrayOf<Drawable>(mainShape, backgroundShape, borderShape))
}
