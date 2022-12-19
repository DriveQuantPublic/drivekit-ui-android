package com.drivequant.drivekit.timeline.ui.component.graph

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.TimelineGraphViewModel

internal class TimelineGraphView(context: Context): LinearLayout(context) {

    private lateinit var viewModel: TimelineGraphViewModel
    private lateinit var graph_title: TextView

    init {
        val view = View.inflate(context, R.layout.dk_timeline_graph_view, null).setDKStyle()
        graph_title = view.findViewById(R.id.graph_title)
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun configure(viewModel: TimelineGraphViewModel) {
        this.viewModel = viewModel
        update()
    }

    fun update() {
        graph_title.text = viewModel.titleKey
    }
}