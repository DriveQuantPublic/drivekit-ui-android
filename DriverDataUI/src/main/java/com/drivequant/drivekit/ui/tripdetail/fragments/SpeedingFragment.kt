package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.databinding.SpeedingFragmentBinding
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKTripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModelFactory
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType

internal class SpeedingFragment : Fragment() {

    private var _binding: SpeedingFragmentBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    companion object {
        fun newInstance(tripDetailViewModel: DKTripDetailViewModel): SpeedingFragment {
            val fragment = SpeedingFragment()
            fragment.viewModel = tripDetailViewModel
            return fragment
        }
    }

    private lateinit var viewModel: DKTripDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SpeedingFragmentBinding.inflate(inflater, container, false)
        binding.root.setDKStyle(android.R.color.white)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putSerializable("itinId", viewModel.getItinId())
            outState.putSerializable("tripListConfigurationType", viewModel.getTripListConfigurationType())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itinId = savedInstanceState?.getSerializableCompat("itinId", String::class.java)
        val tripListConfigurationType =
            savedInstanceState?.getSerializableCompat("tripListConfigurationType", TripListConfigurationType::class.java)

        if (itinId != null && tripListConfigurationType != null) {
            viewModel = ViewModelProvider(
                this,
                TripDetailViewModelFactory(itinId, tripListConfigurationType.getTripListConfiguration())
            )[TripDetailViewModel::class.java]
        }

        if (!this::viewModel.isInitialized) {
            activity?.finish()
            return
        }

        binding.gaugeTypeTitle.setText(com.drivequant.drivekit.common.ui.R.string.dk_common_speed)
        binding.scoreGauge.configure(viewModel.getSpeedingScore(), GaugeConfiguration.SPEEDING(viewModel.getSpeedingScore()), Typeface.BOLD)
        binding.scoreInfo.init(GaugeConfiguration.SPEEDING(viewModel.getSpeedingScore()))

        val speedingDistance = viewModel.getSpeedingDistanceAndPercent(requireContext()).first
        val totalDistancePercent = viewModel.getSpeedingDistanceAndPercent(requireContext()).second
        val speedingDuration = viewModel.getSpeedingDurationAndPercent(requireContext()).first
        val totalDurationPercent = viewModel.getSpeedingDurationAndPercent(requireContext()).second

        val distanceValue = if (speedingDistance >= 1000) {
            DKDataFormatter.formatMeterDistanceInKm(
                requireContext(),
                DKDataFormatter.ceilDistance(speedingDistance.toDouble(), 10000)).convertToString()
        } else {
            DKDataFormatter.formatMeterDistance(requireContext(), speedingDistance.toDouble()).convertToString()
        }

        val durationValue = DKDataFormatter.formatDuration(
            requireContext(),
            DKDataFormatter.ceilDuration(speedingDuration.toDouble(), 600)).convertToString()

        binding.speedingDistanceValue.apply {
            setSelectorContent(totalDistancePercent)
            setSelection(false)
        }

        binding.speedingDurationValue.apply {
            setSelectorContent(totalDurationPercent)
            setSelection(false)
        }

        val durationContent = if (speedingDuration == 0) {
            binding.speedingDurationValue.visibility = View.GONE
            getString(R.string.dk_driverdata_no_speeding_content_congratulations)
        } else {
            DKResource.buildString(
                requireContext(),
                DKColors.mainFontColor,
                DKColors.mainFontColor,
                R.string.dk_driverdata_speeding_events_trip_description,
                durationValue
            ).toString()
        }

        val distanceContent = if (speedingDistance == 0) {
            getString(R.string.dk_driverdata_no_speeding_events)
        } else {
            DKResource.buildString(
                requireContext(),
                DKColors.mainFontColor,
                DKColors.mainFontColor,
                R.string.dk_driverdata_speeding_events_trip_description,
                distanceValue
            ).toString()
        }

        val durationResId = if (speedingDuration == 0) {
            R.string.dk_driverdata_no_speeding_title_congratulations
        } else {
            R.string.dk_driverdata_speeding_events_duration
        }

        binding.speedingDistanceItem.setDistractionEventContent(getString(R.string.dk_driverdata_speeding_events_distance), distanceContent)
        binding.speedingDurationItem.setDistractionEventContent(getString(durationResId), durationContent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
