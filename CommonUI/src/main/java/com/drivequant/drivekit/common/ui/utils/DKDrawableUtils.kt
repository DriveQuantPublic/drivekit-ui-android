package com.drivequant.drivekit.common.ui.utils

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.PathShape
import android.graphics.drawable.shapes.RectShape
import androidx.annotation.ColorInt
import kotlin.math.roundToInt

object DKDrawableUtils {
    fun circleDrawable(
        size: Int,
        @ColorInt insideColor: Int = Color.WHITE,
        @ColorInt borderColor: Int,
        borderWidth: Float = 2.convertDpToPx().toFloat()
    ): Drawable {
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
            paint.color = insideColor
            paint.style = Paint.Style.FILL
            paint.strokeWidth = borderWidth
        }
        return LayerDrawable(arrayOf<Drawable>(mainShape, backgroundShape, borderShape))
    }

    fun circleDrawable(
        size: Int,
        @ColorInt insideColor: Int
    ): Drawable = ShapeDrawable().apply {
        shape = OvalShape()
        intrinsicWidth = size
        intrinsicHeight = size
        paint.color = insideColor
        paint.style = Paint.Style.FILL
    }

    fun roundedRectDrawable(
        width: Float,
        height: Float,
        topLeftRadius: Float,
        topRightRadius: Float,
        bottomRightRadius: Float,
        bottomLeftRadius: Float,
        @ColorInt insideColor: Int
    ): Drawable = ShapeDrawable().apply {
        val corners = floatArrayOf(
            topLeftRadius,
            topLeftRadius,
            topRightRadius,
            topRightRadius,
            bottomRightRadius,
            bottomRightRadius,
            bottomLeftRadius,
            bottomLeftRadius
        )
        val rect = RectF(0f, 0f, width, height)
        val path = Path()
        path.addRoundRect(rect, corners, Path.Direction.CW)
        shape = PathShape(path, width, height)
        intrinsicWidth = width.toInt()
        intrinsicHeight = height.toInt()
        paint.color = insideColor
        paint.style = Paint.Style.FILL
    }

}
