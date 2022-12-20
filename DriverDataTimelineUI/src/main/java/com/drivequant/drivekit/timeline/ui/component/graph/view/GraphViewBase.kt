package com.drivequant.drivekit.timeline.ui.component.graph.view

import android.content.Context
import android.widget.LinearLayout
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.GraphViewModel
import com.github.mikephil.charting.charts.BarLineChartBase

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