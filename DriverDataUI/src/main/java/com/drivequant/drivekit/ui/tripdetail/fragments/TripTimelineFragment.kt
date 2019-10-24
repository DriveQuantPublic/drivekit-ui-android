package com.drivequant.drivekit.ui.tripdetail.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.TripDetailViewConfig
import com.drivequant.drivekit.ui.TripsViewConfig
import com.drivequant.drivekit.ui.tripdetail.adapter.TripTimelineAdapter
import com.drivequant.drivekit.ui.tripdetail.viewholder.OnItemClickListener
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import kotlinx.android.synthetic.main.trip_timeline_fragment.*

class TripTimelineFragment : Fragment() {

    companion object {
        fun newInstance(viewModel: TripDetailViewModel,
                        tripsViewConfig: TripsViewConfig,
                        detailViewConfig: TripDetailViewConfig
        ) : TripTimelineFragment {
            val fragment = TripTimelineFragment()
            fragment.viewModel = viewModel
            fragment.tripsViewConfig = tripsViewConfig
            fragment.detailViewConfig = detailViewConfig
            return fragment
        }
    }

    private lateinit var viewModel: TripDetailViewModel
    private lateinit var tripsViewConfig: TripsViewConfig
    private lateinit var detailViewConfig: TripDetailViewConfig

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.trip_timeline_fragment, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("config", tripsViewConfig)
        outState.putSerializable("detailConfig", detailViewConfig)
        outState.putSerializable("viewModel", viewModel)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("config") as TripsViewConfig?)?.let{
            tripsViewConfig = it
        }
        (savedInstanceState?.getSerializable("detailConfig") as TripDetailViewConfig?)?.let{
            detailViewConfig = it
        }
        (savedInstanceState?.getSerializable("viewModel") as TripDetailViewModel?)?.let{
            viewModel = it
        }
        timeline_list.layoutManager = LinearLayoutManager(requireContext())
        timeline_list.adapter = TripTimelineAdapter(viewModel.events, object : OnItemClickListener {
            override fun onItemClicked(position: Int) {
                viewModel.selection.postValue(position)
            }
        },tripsViewConfig.secondaryColor, detailViewConfig)
        viewModel.selection.observe(this, Observer {
            it?.let {position ->
                (timeline_list.adapter as TripTimelineAdapter).selectedPosition = position
                timeline_list.smoothScrollToPosition(position)
            }
        })
    }

}
