package com.drivequant.drivekit.ui.tripdetail.fragments

import androidx.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.adapter.TripTimelineAdapter
import com.drivequant.drivekit.ui.tripdetail.viewholder.OnItemClickListener
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKTripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModelFactory
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import kotlinx.android.synthetic.main.trip_timeline_fragment.*

class TripTimelineFragment : Fragment() {

    companion object {
        fun newInstance(tripDetailViewModel: DKTripDetailViewModel) : TripTimelineFragment {
            val fragment = TripTimelineFragment()
            fragment.tripDetailViewModel = tripDetailViewModel
            return fragment
        }
    }

    private lateinit var tripDetailViewModel: DKTripDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.trip_timeline_fragment, container, false)
        FontUtils.overrideFonts(context, view)
        view.setBackgroundColor(Color.WHITE)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::tripDetailViewModel.isInitialized) {
            outState.putSerializable("itinId", tripDetailViewModel.getItindId())
            outState.putSerializable(
                "tripListConfigurationType",
                tripDetailViewModel.getTripListConfigurationType()
            )
        }

        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val itinId = savedInstanceState?.getSerializable("itinId") as String?
        val tripListConfigurationType = savedInstanceState?.getSerializable("tripListConfigurationType") as TripListConfigurationType?

        if (itinId != null && tripListConfigurationType != null) {
            tripDetailViewModel = ViewModelProviders.of(
                this,
                TripDetailViewModelFactory(itinId, tripListConfigurationType.getTripListConfiguration())
            ).get(TripDetailViewModel::class.java)
        }

        timeline_list.layoutManager =
            LinearLayoutManager(requireContext())
        timeline_list.adapter = TripTimelineAdapter(tripDetailViewModel.getTripEvents(), object : OnItemClickListener {
            override fun onItemClicked(position: Int) {
                tripDetailViewModel.getSelectedEvent().postValue(position)
            }
        }, DriveKitUI.colors.secondaryColor())
        tripDetailViewModel.getSelectedEvent().observe(this, Observer {
            it?.let {position ->
                (timeline_list.adapter as TripTimelineAdapter).selectedPosition = position
                timeline_list.smoothScrollToPosition(position)
            }
        })
    }
}
