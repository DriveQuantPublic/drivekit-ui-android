package com.drivequant.drivekit.common.ui.component.lasttrips.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.lasttrips.viewmodel.DKLastTripsViewModel
import com.drivequant.drivekit.common.ui.component.triplist.viewholder.TripViewHolder
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_fragment_last_trips.*

class DKLastTripsFragment : Fragment() {

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        context?.let { context ->
            viewModel?.let { viewModel ->
                text_view_last_trip_header.text = viewModel.getTripCardTitle(context)
                val view = View.inflate(context, R.layout.item_trip_list, null)
                view.setBackgroundColor(DriveKitUI.colors.transparentColor())
                val holder = TripViewHolder(view)
                viewModel.trip.let { tripListItem ->
                    holder.bind(tripListItem, viewModel.tripData, true)
                }
                trip_item_container.addView(view)
                root.setOnClickListener {
                    DriveKitNavigationController.driverDataUIEntryPoint?.startTripDetailActivity(
                        context,
                        viewModel.trip.getItinId()
                    )
                }
            } ?: run {
                text_view_see_more.visibility = View.VISIBLE
                text_view_see_more.headLine1()
                text_view_see_more.text = DKResource.convertToString(context,"dk_common_see_more_trips")
                container.visibility = View.GONE
                root.setOnClickListener {
                    DriveKitNavigationController.driverDataUIEntryPoint?.startTripListActivity(
                        context
                    )
                }
            }
        }
    }
}