package com.drivequant.drivekit.timeline.ui.component.graph.view

import android.content.Context
import android.graphics.*
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.graph.GraphAxisConfig
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
                anchor?.let {
                    /*
                    val drawable = ContextCompat.getDrawable(context, R.drawable.dk_test_rectangle_rounded) // TODO create drawable programatically with drivekit colors
                    val width = (mViewPortHandler.chartWidth - x).toInt()
                    val height = (mViewPortHandler.chartHeight - y).toInt()
                    Utils.drawImage(c, drawable, x.toInt(), y.toInt(), 100, 30) //TODO FINISH

                     */
                    // TODO WIP
                    val rectPaint = Paint()
                    rectPaint.color = DriveKitUI.colors.secondaryColor()
                    rectPaint.style = Paint.Style.FILL
                    c.drawRect(
                        0f,
                        mViewPortHandler.contentBottom().toInt().toFloat(),
                        mViewPortHandler.chartWidth,
                        mViewPortHandler.chartHeight,
                        rectPaint
                    )
                }
            }
        }
        super.drawLabel(c, formattedLabel, x, y, anchor, angleDegrees)
    }
}
