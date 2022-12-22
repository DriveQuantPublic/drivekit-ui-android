package com.drivequant.drivekit.timeline.ui.component.graph.view

import android.content.Context
import android.graphics.Canvas
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.timeline.ui.component.graph.GraphAxisConfig
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.GraphViewModel
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.AxisBase
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
        return value.toDouble().format(1)
    }
}

internal class DKAxisRenderer(
    val config: GraphAxisConfig,
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    transformer: Transformer,
) : XAxisRenderer(viewPortHandler, xAxis, transformer) {

    companion object {
        fun from(chartView: BarLineChartBase<*>, config: GraphAxisConfig): DKAxisRenderer {
            return DKAxisRenderer(config, chartView.viewPortHandler, chartView.xAxis, chartView.getTransformer(YAxis.AxisDependency.LEFT))
        }
    }

    var selectedIndex: Int? = null

    init {
        // TODO super.init() not needed ?
    }

    override fun drawLabel(
        c: Canvas?,
        formattedLabel: String?,
        x: Float,
        y: Float,
        anchor: MPPointF?,
        angleDegrees: Float
    ) {
        super.drawLabel(c, formattedLabel, x, y, anchor, angleDegrees)

        val index = this.config.labels.getTitles()?.indexOfFirst { it == formattedLabel }
        if (index != null && index == selectedIndex) {
            // TODO draw a background with a pill shape when the label is the selected one
        }
        super.drawLabel(c, formattedLabel, x, y, anchor, angleDegrees)
    }
}
