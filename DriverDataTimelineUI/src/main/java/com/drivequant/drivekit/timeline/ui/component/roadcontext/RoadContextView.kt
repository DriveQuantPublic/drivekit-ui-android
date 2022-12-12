package com.drivequant.drivekit.timeline.ui.component.roadcontext

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.roadcontext.adapter.RoadContextItemListAdapter
import kotlinx.android.synthetic.main.dk_road_context_empty_view.view.*
import kotlinx.android.synthetic.main.dk_road_context_view.view.*

class RoadContextView(context: Context) : LinearLayout(context) {

    private lateinit var viewModel: RoadContextViewModel
    private var adapter: RoadContextItemListAdapter? = null

    init {
        val view = View.inflate(context, R.layout.dk_road_context_view, null).setDKStyle()
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun configure(roadContextViewModel: RoadContextViewModel) {
        this.viewModel = roadContextViewModel
        update()
    }

    private fun update() {
        initRoadContextContainer()
        initProgressItems()
        displayRoadContextItems()
    }

    private fun displayRoadContextItems() {
        context?.let { context ->
            recycler_view_road_context.layoutManager = GridLayoutManager(context, 2)
            adapter?.notifyDataSetChanged() ?: run {
                adapter = RoadContextItemListAdapter(
                    context,
                    viewModel,
                )
                recycler_view_road_context.layoutManager = LinearLayoutManager(context)
                recycler_view_road_context.adapter = adapter
            }
        }
    }

    private fun initProgressItems() {
        val progressItems = mutableListOf<ProgressItem>()
        viewModel.distanceByContext.forEach{
            progressItems.add(
                ProgressItem(it.key.getColorResId(), viewModel.getPercent(it.key))
            )
        }
        custom_bar.init(progressItems)
    }

    private fun initRoadContextContainer() {
        if (viewModel.shouldShowEmptyViewContainer()) {
            displayEmptyRoadContextUI()
        } else {
            displayRoadContextUI()
        }
    }

    private fun displayRoadContextUI() {
        context?.let { context ->
            with(text_view_road_context_title) {
                text = String.format(
                    DKResource.convertToString(context, "dk_timeline_road_context_title"),
                    viewModel.formatDistanceInKm(context)
                )
                normalText(DriveKitUI.colors.mainFontColor())
            }
        }
        empty_road_context_view.visibility = View.GONE
        road_context_view_container.visibility = View.VISIBLE
    }

    private fun displayEmptyRoadContextUI() {
        with(empty_road_context_view) {
            visibility = View.VISIBLE
            with(text_view_no_data_title) {
                headLine2(DriveKitUI.colors.primaryColor())
                text = DKResource.convertToString(
                    context,
                    "dk_timeline_road_context_title_no_data"
                )
            }
            with(text_view_no_data_description) {
                normalText(DriveKitUI.colors.complementaryFontColor())
                text = DKResource.convertToString(
                    context,
                    "dk_timeline_road_context_description_empty_data"
                )
            }
            road_context_view_container?.visibility = View.GONE
        }
    }
}