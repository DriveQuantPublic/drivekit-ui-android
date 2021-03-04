package com.drivequant.drivekit.ui.tripdetail.fragments

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeType
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.*
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import kotlinx.android.synthetic.main.safety_fragment.gauge_type_title
import kotlinx.android.synthetic.main.safety_fragment.score_gauge
import kotlinx.android.synthetic.main.speeding_fragment.*
import kotlinx.android.synthetic.main.speeding_fragment.view.*

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
        view.score_info.setColorFilter(DriveKitUI.colors.secondaryColor())
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

        gauge_type_title.text = DKResource.convertToString(requireContext(), "dk_common_speed")
        score_gauge.configure(viewModel.getSpeedingScore(), GaugeType.SPEEDING, Typeface.BOLD)

        val speedingDistance = viewModel.getSpeedingDistanceAndPercent(requireContext()).first
        val totalDistancePercent = viewModel.getSpeedingDistanceAndPercent(requireContext()).second
        val speedingDuration = viewModel.getSpeedingDurationAndPercent(requireContext()).first
        val totalDurationPercent = viewModel.getSpeedingDurationAndPercent(requireContext()).second

        val distanceValue = if (speedingDistance >= 1000) {
            DKDataFormatter.formatMeterDistanceInKm(
                requireContext(),
                DKDataFormatter.ceilDistance(speedingDistance.toDouble(), 10000))
        } else {
            DKDataFormatter.formatMeterDistance(requireContext(), speedingDistance.toDouble())
        }

        val durationValue = DKDataFormatter.formatDuration(
            requireContext(),
            DKDataFormatter.ceilDuration(speedingDuration.toDouble(), 600))

        speeding_distance_value.apply {
            setSelectorContent(totalDistancePercent)
            setSelection(false)
        }

        speeding_duration_value.apply {
            setSelectorContent(totalDurationPercent)
            setSelection(false)
        }

        val durationContent = if (speedingDuration == 0) {
                speeding_duration_value.visibility = View.GONE
                DKResource.convertToString(requireContext(), "dk_driverdata_no_speeding_content_congratulations")
            } else {
                DKResource.buildString(
                    requireContext(),
                    DriveKitUI.colors.mainFontColor(),
                    DriveKitUI.colors.mainFontColor(),
                    "dk_driverdata_speeding_events_trip_description", durationValue).toString()
            }

        val distanceContent = if (speedingDistance == 0) {
            DKResource.convertToString(requireContext(), "dk_driverdata_no_speeding_events")
        } else {
            DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.mainFontColor(),
                "dk_driverdata_speeding_events_trip_description", distanceValue).toString()
        }

        val durationResId = if (speedingDuration == 0) {
            "dk_driverdata_no_speeding_title_congratulations"
        } else {
            "dk_driverdata_speeding_events_duration"
        }

        speeding_distance_item.setDistractionEventContent(
            DKResource.convertToString(requireContext(), "dk_driverdata_speeding_events_distance"),
            distanceContent)
        
        speeding_duration_item.setDistractionEventContent(
            DKResource.convertToString(requireContext(), durationResId),
            durationContent)

        score_info.setOnClickListener {
            val alert = DKAlertDialog.LayoutBuilder()
                .init(requireContext())
                .layout(R.layout.template_alert_dialog_layout)
                .cancelable(true)
                .positiveButton(DKResource.convertToString(requireContext(), "dk_common_ok"),
                    DialogInterface.OnClickListener
                    { dialog, _ -> dialog.dismiss() })
                .show()

            val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
            val description = alert.findViewById<TextView>(R.id.text_view_alert_description)
            val icon = alert.findViewById<ImageView>(R.id.image_view_alert_icon)

            title?.text =
                DKResource.convertToString(requireContext(), "dk_driverdata_speeding_score")
            description?.text = DKResource.convertToString(
                requireContext(),
                "dk_driverdata_speeding_score_info"
            )
            icon?.setImageResource(R.drawable.dk_speeding)
            title?.headLine1()
            description?.normalText()
        }
    }
}