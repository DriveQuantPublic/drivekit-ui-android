package com.drivekit.demoapp.component

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet

import android.widget.LinearLayout
import com.github.mikephil.charting.charts.LineChart
import android.view.ViewGroup
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import androidx.core.content.ContextCompat
import com.drivekit.drivekitdemoapp.R
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

class TripSimulatorGraphView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var lineChart: LineChart

    init {
        val view = inflate(context, R.layout.trip_simulator_graph_view, null)
        lineChart = view.findViewById(R.id.line_chart)
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        lineChart.apply {
            data = LineData()
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            xAxis.apply {
                isEnabled = true
                position = XAxis.XAxisPosition.BOTTOM
            }
            axisRight.isEnabled = false

            legend.apply {
                isEnabled = true
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            axisLeft.apply {
                isEnabled = true
                textSize = 9.0f
                textColor = Color.BLACK
                setLabelCount(6, false)
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                axisLineColor = Color.BLACK
            }

            description.isEnabled = true
            description.text = context.getString(R.string.trip_simulator_graph_time)
            isAutoScaleMinMaxEnabled = true
        }
    }

    private fun addEntry(value: Float, title: String, color: Int) {
        val data = lineChart.data
        if (data != null) {
            var lineDataSet = data.getDataSetByIndex(0)
            if (lineDataSet == null) {
                lineDataSet = createSet(title, color)
                data.addDataSet(lineDataSet)
            }
            data.addEntry(
                Entry(
                    lineDataSet.entryCount.toFloat(), value
                ), 0
            )
            data.notifyDataChanged()
            lineChart.apply {
                notifyDataSetChanged()
                setVisibleXRangeMaximum(30f)
                moveViewToX(lineChart.data.entryCount.toFloat())
            }
        }
    }

    private fun createSet(title: String, color: Int): LineDataSet {
        val lineDataSet = LineDataSet(null, title)
        lineDataSet.setDrawCircles(false)
        lineDataSet.lineWidth = 1.8f
        lineDataSet.fillColor = Color.BLACK
        lineDataSet.fillAlpha = 1
        lineDataSet.color = ContextCompat.getColor(context, color)
        lineDataSet.isHighlightEnabled = false
        lineDataSet.setDrawValues(false)
        return lineDataSet
    }

    fun configure(chartEntry: ChartEntry) {
        addEntry(chartEntry.value, chartEntry.title, chartEntry.colorResId)
    }
}

data class ChartEntry(
    val value: Float,
    val title: String,
    val colorResId: Int
)