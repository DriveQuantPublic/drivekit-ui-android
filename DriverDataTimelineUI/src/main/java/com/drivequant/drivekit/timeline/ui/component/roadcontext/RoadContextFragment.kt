package com.drivequant.drivekit.timeline.ui.component.roadcontext

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.roadcontext.adapter.RoadContextItemListAdapter
import com.drivequant.drivekit.timeline.ui.timeline.RoadContextItemData
import kotlinx.android.synthetic.main.dk_road_context_empty_view.view.*
import kotlinx.android.synthetic.main.fragment_road_context.*

class RoadContextFragment : Fragment() {

    private lateinit var viewModel: RoadContextViewModel
    private var adapter: RoadContextItemListAdapter? = null

    companion object {
        fun newInstance(viewModel: RoadContextViewModel): RoadContextFragment {
            val fragment = RoadContextFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_road_context, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO
        //checkViewModelInitialization()
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
                recycler_view_road_context.adapter = adapter
            }
        }
    }

    private fun initProgressItems() {
        val progressItems = mutableListOf<ProgressItem>()
        for (roadContext in viewModel.getRoadContextList()) {
            progressItems.add(
                ProgressItem(roadContext.getColorResId(), viewModel.getRoadContextPercent(
                    roadContext
                ))
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
                    viewModel.totalCalculatedDistance(context)
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
                    "dk_timeline_road_context_description_no_data"
                )
            }
            road_context_view_container.visibility = View.GONE
        }
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(
                this,
                RoadContextViewModel.RoadContextViewModelFactory(
                    mock()
                )
            ).get(RoadContextViewModel::class.java)
        }
    }

    //TODO Remove mock function
    private fun mock() : List<RoadContextItemData> {
        val item1 = RoadContextItemData(
            RoadContext.HEAVY_URBAN_TRAFFIC,
            listOf(1.0,2.0,4.0))

        val item2 = RoadContextItemData(
            RoadContext.CITY,
            listOf(1.0,2.0))

        val item3 = RoadContextItemData(
            RoadContext.EXPRESSWAYS,
            listOf(1.0))
        return listOf(item1, item2, item3)
    }

    override fun onResume() {
        super.onResume()
        //TODO
        //checkViewModelInitialization()
    }
}