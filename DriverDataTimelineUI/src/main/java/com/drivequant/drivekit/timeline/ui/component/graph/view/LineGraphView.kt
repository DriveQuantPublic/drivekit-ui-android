package com.drivequant.drivekit.timeline.ui.component.graph.view

import android.annotation.SuppressLint
import android.content.Context
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.graph.GraphConstants
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.GraphViewModel
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@SuppressLint("ViewConstructor")
internal class LineGraphView(context: Context, graphViewModel: GraphViewModel) : GraphViewBase(context, graphViewModel) {

    private lateinit var chartView: LineChart
    private var selectedEntry: Entry? = null
    private val defaultIcon = GraphConstants.circleIcon(context)
    private val selectedIcon = GraphConstants.circleIcon(context, DriveKitUI.colors.secondaryColor())
    private val invisibleIcon = GraphConstants.invisibleIcon()

    override fun initChartView() {
        val chartView = LineChart(context)
        this.chartView = chartView
    }

    override fun getChartView(): BarLineChartBase<*> {
        return this.chartView
    }

    override fun setupData() {
        this.viewModel.xAxisConfig?.let {
            this.chartView.setXAxisRenderer(DKAxisRenderer.from(this.chartView, it))
        }
        val entries = mutableListOf<Entry>()
        this.viewModel.points.forEachIndexed { index, point ->
            if (point != null) {
                val value = Entry(point.x.toFloat(), point.y.toFloat(), point.data)
                val isInterpolatedPoint = point.data?.interpolatedPoint ?: true
                if (index == this.viewModel.selectedIndex) {
                    //select()
                } else if (isInterpolatedPoint) {
                    value.icon = this.invisibleIcon
                } else {
                    value.icon = this.defaultIcon
                }
                entries.add(value)
            }
        }
        val line = LineDataSet(entries, null)
        line.colors = listOf(R.color.dkChartStrokeColor)
        line.setDrawVerticalHighlightIndicator(false)
        line.setDrawHorizontalHighlightIndicator(false)
        line.isHighlightEnabled = true
        line.setDrawValues(false)
        line.setDrawCircles(false)
        line.lineWidth = 2F

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
            this.extraLeftOffset = 4.convertDpToPx().toFloat()
            this.extraRightOffset = 20.convertDpToPx().toFloat()
        }

        with (this.chartView.xAxis) {
            this.mDecimals = 0
            this.setDrawAxisLine(false)
            this.setDrawGridLines(false)
            this.position = XAxis.XAxisPosition.BOTTOM
            this.textColor = R.color.dkAxisLabelColor
            //this.textSize = //TODO()
            viewModel.xAxisConfig?.let { xAxisConfig ->
                this.valueFormatter = GraphAxisFormatter(xAxisConfig)
                this.setLabelCount(xAxisConfig.labels.getCount(), true)
                this.axisMinimum = xAxisConfig.min.toFloat()
                this.axisMaximum = xAxisConfig.max.toFloat()
            }
        }

        with (this.chartView.axisLeft) {
            this.mDecimals = 0
            this.enableGridDashedLine(4.convertDpToPx().toFloat(), 2.convertDpToPx().toFloat(), 0F)
            this.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            this.xOffset = -4.convertDpToPx().toFloat()
            this.textColor = R.color.dkAxisLabelColor
            viewModel.yAxisConfig?.let { yAxisConfig ->
                this.valueFormatter = GraphAxisFormatter(yAxisConfig)
                this.setLabelCount(yAxisConfig.labels.getCount(), true)
                this.axisMinimum = yAxisConfig.min.toFloat()
                this.axisMaximum = yAxisConfig.max.toFloat()
            }
        }
        this.chartView.setClipValuesToContent(false)
        this.chartView.invalidate()
//        self.chartView.delegate = self
    }

    private fun select(entry: Entry, isInterpolatedPoint: Boolean) {
        if (isInterpolatedPoint) {
            clearPreviousSelectedEntry(false)
        } else {
            clearPreviousSelectedEntry()
            this.selectedEntry = entry
            entry.icon = this.selectedIcon
        }
//        if let renderer = self.chartView.xAxisRenderer as? DKXAxisRenderer {
//            renderer.selectedIndex = Int(entry.x)
//        }
    }

    private fun clearPreviousSelectedEntry(shouldRestoreIcon: Boolean = true) {
        if (shouldRestoreIcon) {
            this.selectedEntry?.icon = this.defaultIcon
        }
        this.selectedEntry = null
    }
}
