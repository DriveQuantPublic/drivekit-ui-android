package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeType
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.*
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import kotlinx.android.synthetic.main.safety_fragment.gauge_type_title
import kotlinx.android.synthetic.main.safety_fragment.score_gauge
import kotlinx.android.synthetic.main.speeding_fragment.*

internal class SpeedingFragment : Fragment() {

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
    ): View? {
        val view = inflater.inflate(R.layout.speeding_fragment, container, false)
        view.setBackgroundColor(Color.WHITE)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putSerializable("itinId", viewModel.getItindId())
            outState.putSerializable(
                "tripListConfigurationType",
                viewModel.getTripListConfigurationType()
            )
        }
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val itinId = savedInstanceState?.getSerializable("itinId") as String?
        val tripListConfigurationType =
            savedInstanceState?.getSerializable("tripListConfigurationType") as TripListConfigurationType?

        if (itinId != null && tripListConfigurationType != null) {
            viewModel = ViewModelProviders.of(
                this,
                TripDetailViewModelFactory(
                    itinId,
                    tripListConfigurationType.getTripListConfiguration()
                )
            ).get(TripDetailViewModel::class.java)
        }

        gauge_type_title.text = context?.getString(R.string.dk_common_speed_limit)
        score_gauge.configure(viewModel.getSpeedingScore(), GaugeType.SPEEDING, Typeface.BOLD)


        val distanceValue = if (viewModel.getSpeedingDistance(requireContext()).first >= 1000) {
            DKDataFormatter.formatMeterDistanceInKm(
                requireContext(),
                DKDataFormatter.ceilDistance(viewModel.getSpeedingDistance(requireContext()).first.toDouble(), 10000)
            )
        } else {
            DKDataFormatter.formatMeterDistance(
                requireContext(),
                viewModel.getSpeedingDistance(requireContext()).first.toDouble())
        }

        val durationValue = DKDataFormatter.formatDuration(
            requireContext(),
            DKDataFormatter.ceilDuration(viewModel.getSpeedingDistance(requireContext()).first.toDouble(), 600))

        speeding_distance_value.apply {
            setSelectorContent(distanceValue)
            setSelection(false)
        }

        speeding_duration_value.apply {
            setSelectorContent(durationValue)
            setSelection(false)
        }

        //TODO Verify div / 0
        val durationContent =
            if (viewModel.getSpeedingDuration(requireContext()).first == 0) {
                speeding_duration_value.visibility = View.INVISIBLE
                DKResource.convertToString(requireContext(), "dk_driverdata_no_speeding_content_congratulations")
            } else {
                DKResource.buildString(
                    requireContext(),
                    DriveKitUI.colors.secondaryColor(),
                    DriveKitUI.colors.secondaryColor(),
                    "dk_driverdata_speeding_events_trip_description",
                    (viewModel.getSpeedingDuration(requireContext()).first * viewModel.getSpeedingDuration(
                        requireContext()
                    ).second).div(100).toString()
                ).toString()
            }

        //TODO Verify div / 0
        val distanceContent = if (viewModel.getSpeedingDistance(requireContext()).first == 0) {
            DKResource.convertToString(requireContext(), "dk_driverdata_no_speeding_events")
        } else {
            DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.secondaryColor(),
                DriveKitUI.colors.secondaryColor(),
                "dk_driverdata_speeding_events_trip_description",
                (viewModel.getSpeedingDistance(requireContext()).first * viewModel.getSpeedingDistance(requireContext()).second).div(100).toString()
            ).toString()
        }
        
        
        val durationResId =
            if (viewModel.getSpeedingDuration(requireContext()).first.toDouble() == 0.0) {
                "dk_driverdata_no_speeding_title_congratulations"
            } else {
                "dk_driverdata_speeding_events_duration"
            }


        speeding_distance_item.setDistractionEventContent(
            DKResource.convertToString(requireContext(), "dk_driverdata_speeding_events_distance"),
            distanceContent
        )
        
        speeding_duration_item.setDistractionEventContent(
            DKResource.convertToString(requireContext(), durationResId),
            durationContent
        )
    }
}