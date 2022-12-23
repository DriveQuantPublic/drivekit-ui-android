package com.drivequant.drivekit.timeline.ui.component.graph.view

import android.content.Context
import android.graphics.*
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.timeline.ui.component.graph.GraphAxisConfig
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.GraphViewModel
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
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

internal class DKAxisRenderer(
    val context: Context,
    val config: GraphAxisConfig,
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    transformer: Transformer,
) : XAxisRenderer(viewPortHandler, xAxis, transformer) {

    companion object {
        fun from(context: Context, chartView: BarLineChartBase<*>, config: GraphAxisConfig): DKAxisRenderer {
            return DKAxisRenderer(context, config, chartView.viewPortHandler, chartView.xAxis, chartView.getTransformer(YAxis.AxisDependency.LEFT))
        }
    }

    var selectedIndex: Int? = null

    override fun drawLabel(
        c: Canvas?,
        formattedLabel: String?,
        x: Float,
        y: Float,
        anchor: MPPointF?,
        angleDegrees: Float
    ) {
        val index = this.config.labels.getTitles()?.indexOfFirst { it == formattedLabel }
        if (index != null && index == selectedIndex) {
            c?.let { canvas ->
                val paint = Paint(mAxisLabelPaint)
                paint.color = DriveKitUI.colors.secondaryColor()
                paint.style = Paint.Style.FILL
                val width = Utils.calcTextSize(paint, mXAxis.longestLabel).width * 1.15f
                val rect = RectF(0f, 10f, width, 50f)
                val radius = 50f
                canvas.drawRoundRect(rect, radius, radius, paint)
            }
        }
        super.drawLabel(c, formattedLabel, x, y, anchor, angleDegrees)
    }
}
