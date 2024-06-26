package com.drivequant.drivekit.timeline.ui.component.graph

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.annotation.ColorInt
import com.drivequant.drivekit.common.ui.extension.intColor
import com.drivequant.drivekit.common.ui.utils.DKDrawableUtils
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
    const val GRAPH_POINT_NUMBER: Int = 8
    const val GRAPH_LINE_WIDTH = 2f
    const val GRAPH_LABEL_TEXT_SIZE = 11f
    private val iconSize = 11.convertDpToPx()

    fun circleIcon(context: Context, @ColorInt insideColor: Int = Color.WHITE): Drawable {
        val borderColor: Int = R.color.dkChartStrokeColor.intColor(context)
        return DKDrawableUtils.circleDrawable(iconSize, insideColor, borderColor, GRAPH_LINE_WIDTH.convertDpToPx().toFloat())
    }

    fun invisibleIcon(): Drawable {
        val backgroundColor: Int = Color.TRANSPARENT
        val backgroundShape = ShapeDrawable().apply {
            shape = OvalShape()
            paint.color = backgroundColor
            paint.style = Paint.Style.FILL
        }
        return backgroundShape
    }
}
