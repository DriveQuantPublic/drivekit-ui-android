package com.drivequant.drivekit.timeline.ui.component.graph

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.TimelineGraphViewModel

internal class TimelineGraphView(context: Context): LinearLayout(context) {

    private lateinit var viewModel: TimelineGraphViewModel
    private val graphTitle: TextView
    private val graphContainer: FrameLayout

    init {
        val view = View.inflate(context, R.layout.dk_timeline_graph_view, null).setDKStyle()
        addView(view, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        graphTitle = view.findViewById(R.id.graph_title)
        graphContainer = view.findViewById(R.id.graph_view_container)
    }

    fun configure(viewModel: TimelineGraphViewModel) {
        this.viewModel = viewModel
        update()
    }

    private fun update() {
        with(graphTitle) {
            text = DKResource.buildString(
                context,
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.primaryColor(),
                viewModel.titleKey,
                viewModel.description,
                textSize = R.dimen.dk_text_small,
                highlightSize = R.dimen.dk_text_normal
            )
        }
    }
}