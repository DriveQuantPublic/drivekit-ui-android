package com.drivequant.drivekit.timeline.ui.component.graph.view

import android.annotation.SuppressLint
import android.content.Context
import com.drivequant.drivekit.common.ui.extension.intColor
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.graph.GraphConstants
import com.drivequant.drivekit.timeline.ui.component.graph.GraphPoint
import com.drivequant.drivekit.timeline.ui.component.graph.PointData
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.GraphViewModel
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

@SuppressLint("ViewConstructor")
internal class LineGraphView(context: Context, graphViewModel: GraphViewModel) : GraphViewBase(context, graphViewModel), OnChartValueSelectedListener {

    private lateinit var chartView: LineChart
    private var selectedEntry: Entry? = null
    private val defaultIcon = GraphConstants.circleIcon(context)
    private val selectedIcon = GraphConstants.circleIcon(context, DKColors.secondaryColor)
    private val invisibleIcon = GraphConstants.invisibleIcon()

    override fun initChartView() {
        val chartView = CustomLineChart(context)
        this.chartView = chartView
    }

    override fun getChartView(): BarLineChartBase<*> {
        return this.chartView
    }

    override fun setupData() {
        this.viewModel.xAxisConfig?.let {
            this.chartView.setXAxisRenderer(DKXAxisRenderer.from(context, this.chartView, it))
        }
        val entries = mutableListOf<Entry>()
        this.viewModel.points.forEachIndexed { index, point ->
            if (point != null) {
                val value = Entry(point.x.toFloat(), point.y.toFloat(), point.data)
                val isInterpolatedPoint = point.data?.interpolatedPoint ?: true
                if (index == this.viewModel.selectedIndex) {
                    select(value, isInterpolatedPoint)
                } else if (isInterpolatedPoint) {
                    value.icon = this.invisibleIcon
                } else {
                    value.icon = this.defaultIcon
                }
                entries.add(value)
            }
        }
        val line = LineDataSet(entries, null)
        line.colors = listOf(R.color.dkChartStrokeColor.intColor(context))
        line.setDrawVerticalHighlightIndicator(false)
        line.setDrawHorizontalHighlightIndicator(false)
        line.isHighlightEnabled = true
        line.setDrawValues(false)
        line.setDrawCircles(false)
        line.lineWidth = GraphConstants.GRAPH_LINE_WIDTH

        val data = LineData()
        data.addDataSet(line)
        data.isHighlightEnabled = true

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
            this.extraBottomOffset = 4f
            this.extraRightOffset = 18f
        }

        with (this.chartView.xAxis) {
            this.setDrawAxisLine(false)
            this.yOffset = 12f
            this.setDrawGridLines(false)
            this.position = XAxis.XAxisPosition.BOTTOM
            this.textColor = R.color.dkAxisLabelColor.intColor(context)
            this.textSize = GraphConstants.GRAPH_LABEL_TEXT_SIZE
            viewModel.xAxisConfig?.let { xAxisConfig ->
                this.valueFormatter = GraphAxisFormatter(xAxisConfig)
                this.setLabelCount(xAxisConfig.labels.getCount(), true)
                this.axisMinimum = xAxisConfig.min.toFloat()
                this.axisMaximum = xAxisConfig.max.toFloat()
            }
        }

        with (this.chartView.axisLeft) {
            this.setDrawAxisLine(false)
            this.xOffset = 12f
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
    }

    private fun select(entry: Entry, isInterpolatedPoint: Boolean) {
        if (isInterpolatedPoint) {
            clearPreviousSelectedEntry(false)
        } else {
            clearPreviousSelectedEntry()
            this.selectedEntry = entry
            entry.icon = this.selectedIcon
        }

        this.chartView.rendererXAxis?.let {
            if (it is DKXAxisRenderer) {
                it.selectedIndex = entry.x.toInt()
            }
        }
    }

    private fun clearPreviousSelectedEntry(shouldRestoreIcon: Boolean = true) {
        if (shouldRestoreIcon) {
            this.selectedEntry?.icon = this.defaultIcon
        }
        this.selectedEntry = null
    }

    override fun onValueSelected(entry: Entry?, highlight: Highlight?) {
        if (entry != null && entry != this.selectedEntry) {
            val data: PointData? = entry.data as? PointData
            if (data != null) {
                select(entry, data.interpolatedPoint)
                this.listener?.onSelectPoint(GraphPoint(entry.x.toDouble(), entry.y.toDouble(), data))
            }
        }
    }

    override fun onNothingSelected() {
        // Do nothing.
    }

}

private class CustomLineChart(context: Context) : LineChart(context) {
    init {
        mViewPortHandler = CustomViewPortHandler()
        super.init()
    }
}
