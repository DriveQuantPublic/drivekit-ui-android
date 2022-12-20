package com.drivequant.drivekit.timeline.ui.component.graph.view

import android.content.Context
import android.graphics.Canvas
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.timeline.ui.component.graph.GraphAxisConfig
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.GraphViewModel
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

internal abstract class GraphViewBase(context: Context): LinearLayout(context) {

    var listener: GraphViewListener? = null
    lateinit var viewModel: GraphViewModel
        private set

    fun init(viewModel: GraphViewModel) {
        this.viewModel = viewModel
        addView(getChartView())
        this.viewModel.graphViewModelDidUpdate = {
            setupData()
        }
        setupData()
    }

    abstract fun getChartView(): BarLineChartBase<*>

    abstract fun setupData()
}

internal class GraphAxisFormatter: ValueFormatter() {
    private lateinit var config: GraphAxisConfig

    fun init(config: GraphAxisConfig) {
        this.config = config
    }

    fun stringForValue(value: Double, axis: AxisBase?): String {
        this.config.labels.titles?.let { labels ->
            val index = (value - this.config.min).toInt()
            if (index >= 0 && index < labels.size) {
                return labels[index]
            }
        }
        return value.format(1)
    }
}

internal class DKAxisRenderer(
    val config: GraphAxisConfig,
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    transformer: Transformer,
) : XAxisRenderer(viewPortHandler, xAxis, transformer) {

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

        val index = this.config.labels.titles?.indexOfFirst { it == formattedLabel }
        if (index != null && index == selectedIndex) {
            // TODO draw a background with a pill shape when the label is the selected one
        }
        super.drawLabel(c, formattedLabel, x, y, anchor, angleDegrees)
    }
}