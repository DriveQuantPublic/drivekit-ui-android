package com.drivequant.drivekit.timeline.ui.component.graph

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.TimelineGraphViewModel

internal class TimelineGraphView(context: Context): CardView(context) {

    private lateinit var viewModel: TimelineGraphViewModel
    private val graphTitle: TextView

    init {
        val view = View.inflate(context, R.layout.dk_timeline_graph_view, null).setDKStyle()
        graphTitle = view.findViewById(R.id.graph_title)
        addView(view, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
    }

    fun configure(viewModel: TimelineGraphViewModel) {
        this.viewModel = viewModel
        update()
    }

    fun update() {
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