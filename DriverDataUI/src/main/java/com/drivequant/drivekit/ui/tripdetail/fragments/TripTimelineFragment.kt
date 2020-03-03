package com.drivequant.drivekit.ui.tripdetail.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.adapter.TripTimelineAdapter
import com.drivequant.drivekit.ui.tripdetail.viewholder.OnItemClickListener
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import kotlinx.android.synthetic.main.trip_timeline_fragment.*

class TripTimelineFragment : Fragment() {

    companion object {
        fun newInstance(viewModel: TripDetailViewModel  ) : TripTimelineFragment {
            val fragment = TripTimelineFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: TripDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.trip_timeline_fragment, container, false)
        FontUtils.overrideFonts(context, view)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("viewModel", viewModel)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("viewModel") as TripDetailViewModel?)?.let{
            viewModel = it
        }
        timeline_list.layoutManager = LinearLayoutManager(requireContext())
        timeline_list.adapter = TripTimelineAdapter(viewModel.events, object : OnItemClickListener {
            override fun onItemClicked(position: Int) {
                viewModel.selection.postValue(position)
            }
        }, DriveKitUI.colors.secondaryColor())
        viewModel.selection.observe(this, Observer {
            it?.let {position ->
                (timeline_list.adapter as TripTimelineAdapter).selectedPosition = position
                timeline_list.smoothScrollToPosition(position)
            }
        })
    }
}
