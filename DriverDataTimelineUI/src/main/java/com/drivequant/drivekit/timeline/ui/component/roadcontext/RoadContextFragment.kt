package com.drivequant.drivekit.timeline.ui.component.roadcontext

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.component.roadcontext.adapter.RoadContextItemListAdapter
import kotlinx.android.synthetic.main.fragment_road_context.*


class RoadContextFragment : Fragment() {

    private lateinit var viewModel: RoadContextViewModel
    private var adapter: RoadContextItemListAdapter? = null

    companion object {
        fun newInstance() = RoadContextFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_road_context, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkViewModelInitialization()
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

        custom_progress_bar.thumb.mutate().alpha = 0

        val progressItemList = mutableListOf<ProgressItem>()
        progressItemList.add(
            ProgressItem(R.color.dkRoadContextUrbainDenseColor, 25f)
        )
        progressItemList.add(
            ProgressItem(R.color.dkRoadContextHighwayColor, 25f)
        )
        progressItemList.add(
            ProgressItem(R.color.dkRoadContextUrbainFluidColor, 25f)
        )
        progressItemList.add(
            ProgressItem(R.color.dkRoadContextSubUrbainColor, 25f)
        )
        custom_progress_bar.initData(progressItemList)
        custom_progress_bar.invalidate()
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(RoadContextViewModel::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        checkViewModelInitialization()
    }
}

data class ProgressItem(
    val color: Int,
    val progressItemPercentage: Float
)