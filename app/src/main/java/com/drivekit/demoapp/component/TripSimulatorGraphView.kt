package com.drivekit.demoapp.component

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.extension.intColor
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

internal class TripSimulatorGraphView @JvmOverloads constructor(
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
                setDrawGridLines(false)
            }
            axisRight.isEnabled = false
            legend.apply {
                isEnabled = true
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                orientation = Legend.LegendOrientation.HORIZONTAL
                form = Legend.LegendForm.NONE
                setDrawInside(false)
            }

            axisLeft.apply {
                isEnabled = true
                textSize = 9.0f
                textColor = Color.BLACK
                setLabelCount(6, false)
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                axisLineColor = Color.BLACK
                axisMinimum = 0f
            }

            description.isEnabled = true
            description.text = context.getString(R.string.trip_simulator_graph_time)
            isAutoScaleMinMaxEnabled = true
            setBackgroundColor(DKColors.transparentColor)
        }
    }

    private fun addEntry(value: Float, title: String, @ColorRes color: Int) {
        val data = lineChart.data
        data?.let {
            var lineDataSet = data.getDataSetByIndex(0)
            if (lineDataSet == null) {
                lineDataSet = createDataSet(title, color)
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
                setVisibleXRangeMaximum(90f)
                moveViewToX(lineChart.data.entryCount.toFloat())
            }
        }
    }

    private fun createDataSet(title: String, @ColorRes color: Int): LineDataSet {
        val lineDataSet = LineDataSet(null, title)
        lineDataSet.apply {
            setDrawCircles(false)
            lineWidth = 2f
            fillColor = Color.BLACK
            fillAlpha = 1
            setColor(color.intColor(context))
            isHighlightEnabled = false
            setDrawValues(false)
        }
        return lineDataSet
    }

    fun updateGraph(chartEntry: ChartEntry) {
        addEntry(chartEntry.value, chartEntry.title, chartEntry.colorResId)
    }

    fun clean() {
        lineChart.data.clearValues()
        lineChart.invalidate()
    }
}

internal data class ChartEntry(
    val value: Float,
    val title: String,
    @ColorRes val colorResId: Int
)
