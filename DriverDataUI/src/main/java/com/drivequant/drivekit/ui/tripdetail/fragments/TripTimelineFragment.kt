package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.ui.databinding.TripTimelineFragmentBinding
import com.drivequant.drivekit.ui.tripdetail.adapter.TripTimelineAdapter
import com.drivequant.drivekit.ui.tripdetail.viewholder.OnItemClickListener
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKTripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModelFactory
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType

class TripTimelineFragment : Fragment() {

    companion object {
        fun newInstance(tripDetailViewModel: DKTripDetailViewModel) : TripTimelineFragment {
            val fragment = TripTimelineFragment()
            fragment.tripDetailViewModel = tripDetailViewModel
            return fragment
        }
    }

    private lateinit var tripDetailViewModel: DKTripDetailViewModel
    private var _binding: TripTimelineFragmentBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TripTimelineFragmentBinding.inflate(inflater, container, false)
        FontUtils.overrideFonts(context, binding.root)
        binding.root.setDKStyle(Color.WHITE)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::tripDetailViewModel.isInitialized) {
            outState.putSerializable("itinId", tripDetailViewModel.getItinId())
            outState.putSerializable(
                "tripListConfigurationType",
                tripDetailViewModel.getTripListConfigurationType()
            )
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itinId = savedInstanceState?.getSerializableCompat("itinId", String::class.java)
        val tripListConfigurationType = savedInstanceState?.getSerializableCompat("tripListConfigurationType", TripListConfigurationType::class.java)

        if (itinId != null && tripListConfigurationType != null) {
            tripDetailViewModel = ViewModelProvider(
                this,
                TripDetailViewModelFactory(itinId, tripListConfigurationType.getTripListConfiguration())
            )[TripDetailViewModel::class.java]
        }

        binding.timelineList.layoutManager =
            LinearLayoutManager(requireContext())
        binding.timelineList.adapter = TripTimelineAdapter(tripDetailViewModel.getTripEvents(), object : OnItemClickListener {
            override fun onItemClicked(position: Int) {
                tripDetailViewModel.getSelectedEvent().postValue(position)
            }
        }, DriveKitUI.colors.secondaryColor())
        tripDetailViewModel.getSelectedEvent().observe(viewLifecycleOwner) {
            it?.let { position ->
                (binding.timelineList.adapter as TripTimelineAdapter).selectedPosition = position
                binding.timelineList.smoothScrollToPosition(position)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
