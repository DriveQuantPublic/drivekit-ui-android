package com.drivequant.drivekit.timeline.ui.component.graph.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Rect
import android.graphics.RectF
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.timeline.ui.component.graph.GraphAxisConfig
import com.drivequant.drivekit.timeline.ui.component.graph.GraphConstants
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.GraphViewModel
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler


internal abstract class GraphViewBase(context: Context, val viewModel: GraphViewModel): LinearLayout(context) {

    var listener: GraphViewListener? = null

    init {
        initChartView()
        addView(getChartView(), ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
        this.viewModel.graphViewModelDidUpdate = {
            setupData()
        }
        setupData()
    }

    abstract fun initChartView()

    abstract fun getChartView(): BarLineChartBase<*>

    abstract fun setupData()
}

internal class GraphAxisFormatter(val config: GraphAxisConfig): ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        this.config.labels.getTitles()?.let { labels ->
            val index = (value - this.config.min).toInt()
            if (index >= 0 && index < labels.size) {
                return labels[index]
            }
        }
        return value.toDouble().removeZeroDecimal()
    }
}

internal class DKXAxisRenderer(
    val context: Context,
    val config: GraphAxisConfig,
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    transformer: Transformer,
) : XAxisRenderer(viewPortHandler, xAxis, transformer) {

    companion object {
        private val fontMetrics = Paint.FontMetrics()
        private val paintRenderer = Paint().also {
            it.color = DriveKitUI.colors.secondaryColor()
            it.style = Paint.Style.FILL
            it.alpha = 255 / 2
        }

        fun from(context: Context, chartView: BarLineChartBase<*>, config: GraphAxisConfig): DKXAxisRenderer {
            return DKXAxisRenderer(context, config, chartView.viewPortHandler, chartView.xAxis, chartView.getTransformer(YAxis.AxisDependency.LEFT))
        }
    }

    var selectedIndex: Int? = null

    override fun drawLabel(
        canvas: Canvas?,
        text: String?,
        x: Float,
        y: Float,
        anchor: MPPointF?,
        angleDegrees: Float
    ) {
        val index = this.config.labels.getTitles()?.indexOfFirst { it == text }
        if (index != null && index == selectedIndex && text != null && canvas != null) {
            var drawOffsetX = 0f
            var drawOffsetY = 0f
            val paint = mAxisLabelPaint
            val lineHeight: Float = paint.getFontMetrics(fontMetrics)
            val rect = Rect()
            paint.getTextBounds(text, 0, text.length, rect)
            val horizontalPadding = 3.convertDpToPx()
            val verticalPadding = 2.convertDpToPx()
            drawOffsetX -= rect.left.toFloat()
            drawOffsetY += -fontMetrics.ascent
            val originalTextAlign: Align = paint.textAlign
            paint.textAlign = Align.LEFT
            if (anchor != null && (anchor.x != 0f || anchor.y != 0f)) {
                drawOffsetX -= rect.width() * anchor.x
                drawOffsetY -= lineHeight * anchor.y
            }
            drawOffsetX += x
            drawOffsetY += y

            val radius = (rect.height() + verticalPadding * 2f)
            canvas.drawRoundRect(drawOffsetX - horizontalPadding, drawOffsetY - rect.height() - verticalPadding, drawOffsetX + rect.width() + 2 * horizontalPadding, drawOffsetY + 2 * verticalPadding, radius, radius, paintRenderer)

            paint.textAlign = originalTextAlign
        }
        super.drawLabel(canvas, text, x, y, anchor, angleDegrees)
    }

}

internal class CustomViewPortHandler : ViewPortHandler() {
    override fun getContentRect(): RectF {
        val contentRect = super.getContentRect()
        val newContentRect = RectF(contentRect)
        newContentRect.inset(0f, -(GraphConstants.GRAPH_LINE_WIDTH / 2f).convertDpToPx().toFloat())
        return newContentRect
    }
}
