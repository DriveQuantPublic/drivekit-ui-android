package com.drivequant.drivekit.timeline.ui.component.graph.view

import android.content.Context
import android.graphics.Color
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.GraphViewModel
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

internal class LineGraphView(context: Context, graphViewModel: GraphViewModel) : GraphViewBase(context, graphViewModel) {

    private lateinit var chartView: LineChart
    private var selectedEntry: Entry? = null

    init {
        chartView.setData(LineData())
        chartView.setDragEnabled(true)
        chartView.setScaleEnabled(true)
        chartView.setPinchZoom(false)
        chartView.getXAxis().isEnabled = false
        chartView.getAxisRight().isEnabled = false
        chartView.getLegend().isEnabled = true

        chartView.getLegend().horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        chartView.getLegend().verticalAlignment = Legend.LegendVerticalAlignment.TOP
        chartView.getLegend().orientation = Legend.LegendOrientation.HORIZONTAL
        chartView.getLegend().setDrawInside(false)

        chartView.getAxisLeft().textSize = 9.0f
        chartView.getAxisLeft().textColor = Color.BLACK
        chartView.getAxisLeft().setLabelCount(6, false)
        chartView.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        chartView.getAxisLeft().axisLineColor = Color.BLACK
        chartView.getDescription().isEnabled = false
        chartView.setAutoScaleMinMaxEnabled(true)

        val lineDataSet = LineDataSet(null, "Test")
        lineDataSet.setDrawCircles(false)
        lineDataSet.lineWidth = 1.8f
        lineDataSet.fillColor = Color.BLACK
        lineDataSet.fillAlpha = 1
        lineDataSet.color = Color.BLUE
        lineDataSet.isHighlightEnabled = false
        lineDataSet.setDrawValues(false)
    }

    override fun initChartView() {
        val chartView = LineChart(context)
        this.chartView = chartView
    }

    override fun getChartView(): BarLineChartBase<*> {
        return this.chartView
    }

    override fun setupData() {
        //TODO
    }
}
