package com.drivequant.drivekit.common.ui.component.lasttripscards.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.lasttripscards.viewmodel.DKLastTripsViewModel
import com.drivequant.drivekit.common.ui.component.triplist.viewholder.TripViewHolder
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import kotlinx.android.synthetic.main.dk_fragment_last_trips.root
import kotlinx.android.synthetic.main.dk_fragment_last_trips.text_view_last_trip_header
import kotlinx.android.synthetic.main.dk_fragment_last_trips.trip_item_container

internal class DKLastTripsFragment : Fragment() {

    private var viewModel: DKLastTripsViewModel? = null

    companion object {
        fun newInstance(
            viewModel: DKLastTripsViewModel?
        ): DKLastTripsFragment {
            val fragment = DKLastTripsFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_last_trips, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { context ->
            viewModel?.let { viewModel ->
                text_view_last_trip_header.apply {
                    headLine2()
                    text = viewModel.getTripCardTitle(context)
                }
                val tripList = View.inflate(context, R.layout.item_trip_list, null)
                tripList.setBackgroundColor(DriveKitUI.colors.transparentColor())
                val holder = TripViewHolder(tripList)
                viewModel.trip.let { tripListItem ->
                    holder.bind(tripListItem, viewModel.tripData, true)
                }
                trip_item_container.addView(tripList)
                root.setOnClickListener {
                    DriveKitNavigationController.driverDataUIEntryPoint?.startTripDetailActivity(
                        context,
                        viewModel.trip.getItinId(),
                        false
                    )
                }
            }
        }
    }
}
