package com.drivequant.drivekit.timeline.ui.component.graph.view

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.graph.GraphPoint
import com.drivequant.drivekit.timeline.ui.component.graph.GraphType
import com.drivequant.drivekit.timeline.ui.component.graph.viewmodel.TimelineGraphViewModel

internal class TimelineGraphView(context: Context, val viewModel: TimelineGraphViewModel): LinearLayout(context), GraphViewListener {
    var listener: GraphViewListener? = null
    private val graphTitle: TextView
    private val graphView: GraphViewBase
    private val gestureDetector = GestureDetectorCompat(context, SwipeGestureDetector(viewModel))

    init {
        val view = View.inflate(context, R.layout.dk_timeline_graph_view, null).setDKStyle()
        addView(view, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        graphTitle = view.findViewById(R.id.graph_title)
        val graphContainer = view.findViewById(R.id.graph_view_container) as FrameLayout

        val graphView: GraphViewBase = when (this.viewModel.type) {
            GraphType.LINE -> LineGraphView(context, this.viewModel)
            GraphType.BAR -> BarGraphView(context, this.viewModel)
        }
        graphContainer.addView(graphView, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
        graphView.listener = this
        this.graphView = graphView
        this.viewModel.graphViewModelDidUpdate = this::updateContent
        updateContent()
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return if (this.gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onInterceptTouchEvent(event)
        }
    }

    private fun updateContent() {
        updateTitle()
        this.graphView.setupData()
    }

    private fun updateTitle() {
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

    override fun onSelectPoint(point: GraphPoint) {
        this.listener?.onSelectPoint(point)
    }
}

private class SwipeGestureDetector(private val viewModel: TimelineGraphViewModel) : GestureDetector.SimpleOnGestureListener() {
    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (velocityX > 0) {
            this.viewModel.showPreviousGraphData()
        } else {
            this.viewModel.showNextGraphData()
        }
        return super.onFling(e1, e2, velocityX, velocityY)
    }
}