package com.drivequant.drivekit.timeline.ui.component.graph.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.graph.GraphConstants
import com.drivequant.drivekit.timeline.ui.component.graph.GraphPoint
import com.drivequant.drivekit.timeline.ui.component.graph.PointData
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.GraphViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

@SuppressLint("ViewConstructor")
internal class BarGraphView(context: Context, graphViewModel: GraphViewModel) : GraphViewBase(context, graphViewModel), OnChartValueSelectedListener {

    private lateinit var chartView: BarChart
    private var selectedEntry: Entry? = null

    override fun initChartView() {
        val chartView = CustomBarChart(context)
        this.chartView = chartView
    }

    override fun getChartView(): BarLineChartBase<*> {
        return this.chartView
    }

    override fun setupData() {
        this.viewModel.xAxisConfig?.let {
            this.chartView.setXAxisRenderer(DKXAxisRenderer.from(context, this.chartView, it))
        }
        val entries = mutableListOf<BarEntry>()
        var entryToSelect: BarEntry? = null
        this.viewModel.points.forEachIndexed { index, point ->
            if (point != null) {
                val value = BarEntry(point.x.toFloat(), point.y.toFloat(), point.data)
                if (index == this.viewModel.selectedIndex) {
                    entryToSelect = value
                }
                entries.add(value)
            }
        }

        val bar = BarDataSet(entries, null)
        bar.colors = listOf(Color.WHITE)
        bar.barBorderColor = ContextCompat.getColor(this.context, R.color.dkChartStrokeColor)
        bar.barBorderWidth = GraphConstants.GRAPH_LINE_WIDTH
        bar.isHighlightEnabled = true
        bar.highLightColor = DriveKitUI.colors.secondaryColor()
        bar.highLightAlpha = 255
        bar.setDrawValues(false)

        val data = BarData()
        data.addDataSet(bar)
        data.isHighlightEnabled = true
        data.barWidth = 0.5f

        with(this.chartView) {
            this.data = data
            this.isHighlightPerTapEnabled = true
            this.setPinchZoom(false)
            this.isScaleXEnabled = false
            this.isScaleYEnabled = false
            this.isDragEnabled = false
            this.isDoubleTapToZoomEnabled = false
            this.axisRight.isEnabled = false
            this.legend.isEnabled = false
            this.description = null
            this.setClipValuesToContent(false)
            setDragOffsetY(-GraphConstants.GRAPH_LINE_WIDTH / 2f)
        }

        with (this.chartView.xAxis) {
            this.setDrawAxisLine(false)
            this.setDrawGridLines(false)
            this.position = XAxis.XAxisPosition.BOTTOM
            this.textColor = ContextCompat.getColor(context, R.color.dkAxisLabelColor)
            this.setAvoidFirstLastClipping(true)
            viewModel.xAxisConfig?.let { xAxisConfig ->
                this.valueFormatter = GraphAxisFormatter(xAxisConfig)
                this.setLabelCount(xAxisConfig.labels.getCount(), false)
                this.axisMinimum = xAxisConfig.min.toFloat() - 0.5f
                this.axisMaximum = xAxisConfig.max.toFloat() + 0.5f
            }
        }

        with (this.chartView.axisLeft) {
            this.setDrawAxisLine(false)
            this.enableGridDashedLine(4.convertDpToPx().toFloat(), 2.convertDpToPx().toFloat(), 0F)
            this.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            this.textColor = R.color.dkAxisLabelColor
            viewModel.yAxisConfig?.let { yAxisConfig ->
                this.valueFormatter = GraphAxisFormatter(yAxisConfig)
                this.setLabelCount(yAxisConfig.labels.getCount(), true)
                this.axisMinimum = yAxisConfig.min.toFloat()
                this.axisMaximum = yAxisConfig.max.toFloat()
            }
        }

        this.chartView.invalidate()
        this.chartView.setOnChartValueSelectedListener(this)

        entryToSelect?.let {
            select(it)
        }
    }

    private fun select(entry: Entry) {
        this.selectedEntry = entry
        this.chartView.highlightValue(entry.x, 0)
        this.chartView.rendererXAxis?.let {
            if (it is DKXAxisRenderer) {
                it.selectedIndex = entry.x.toInt()
            }
        }
    }

    override fun onValueSelected(entry: Entry?, highlight: Highlight?) {
        if (entry != null && entry != this.selectedEntry) {
            val data: PointData? = entry.data as? PointData
            if (data != null) {
                select(entry)
                this.listener?.onSelectPoint(GraphPoint(entry.x.toDouble(), entry.y.toDouble(), data))
            }
        }
    }

    override fun onNothingSelected() {
        // Nothing to do.
    }

}

private class CustomBarChart(context: Context) : BarChart(context) {
    init {
        mViewPortHandler = CustomViewPortHandler()
        super.init()
    }
}
