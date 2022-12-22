package com.drivequant.drivekit.timeline.ui.component.graph

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.timeline.ui.R
import java.util.Date

internal data class PointData(
    val date: Date,
    val interpolatedPoint: Boolean
)

internal data class GraphPoint(
    val x: Double,
    val y: Double,
    val data: PointData?
)

internal object GraphConstants {
    const val DEFAULT_NUMBER_OF_INTERVAL_IN_Y_AXIS = 10
    const val DEFAULT_MAX_VALUE_IN_Y_AXIS = 10
    const val NOT_ENOUGH_DATA_IN_GRAPH_THRESHOLD = 10.0
    const val MAX_VALUE_IN_Y_AXIS_WHEN_NOT_ENOUGH_DATA_IN_GRAPH = 10.0
    private val iconSize = 14.convertDpToPx()

    fun circleIcon(context: Context, insideColor: Int = Color.WHITE): Drawable {
        val borderColor: Int = ContextCompat.getColor(context, R.color.dkChartStrokeColor)
        val backgroundColor: Int = insideColor
        val borderWidth = 2.convertDpToPx().toFloat()
        val borderShape = ShapeDrawable().apply {
            shape = OvalShape()
            intrinsicWidth = iconSize
            intrinsicHeight = iconSize
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderWidth
        }
        val backgroundShape = ShapeDrawable().apply {
            shape = OvalShape()
            intrinsicWidth = iconSize
            intrinsicHeight = iconSize
            paint.color = backgroundColor
            paint.style = Paint.Style.FILL_AND_STROKE;
        }
        return LayerDrawable(arrayOf<Drawable>(backgroundShape, borderShape))
    }

    fun invisibleIcon(): Drawable {
        val backgroundColor: Int = Color.TRANSPARENT
        val backgroundShape = ShapeDrawable().apply {
            shape = OvalShape()
            paint.color = backgroundColor
            paint.style = Paint.Style.FILL;
        }
        return backgroundShape
    }
}
