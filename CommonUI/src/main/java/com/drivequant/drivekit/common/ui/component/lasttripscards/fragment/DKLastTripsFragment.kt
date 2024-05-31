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
import com.drivequant.drivekit.common.ui.databinding.DkFragmentLastTripsBinding
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController

internal class DKLastTripsFragment : Fragment() {

    private var viewModel: DKLastTripsViewModel? = null
    private var _binding: DkFragmentLastTripsBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    companion object {
        fun newInstance(viewModel: DKLastTripsViewModel?): DKLastTripsFragment {
            val fragment = DKLastTripsFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DkFragmentLastTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { context ->
            viewModel?.let { viewModel ->
                binding.textViewLastTripHeader.apply {
                    headLine2()
                    text = viewModel.getTripCardTitle(context)
                }
                val tripList = View.inflate(context, R.layout.item_trip_list, null)
                tripList.setBackgroundColor(DriveKitUI.colors.transparentColor())
                val holder = TripViewHolder(tripList)
                viewModel.trip.let { tripListItem ->
                    holder.bind(tripListItem, viewModel.tripData, true)
                }
                binding.tripItemContainer.addView(tripList)
                binding.root.setOnClickListener {
                    DriveKitNavigationController.driverDataUIEntryPoint?.startTripDetailActivity(
                        context,
                        viewModel.trip.getItinId(),
                        false
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
