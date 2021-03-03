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
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.*
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import kotlinx.android.synthetic.main.driver_distraction_fragment.*
import kotlinx.android.synthetic.main.driver_distraction_fragment.gauge_type_title
import kotlinx.android.synthetic.main.driver_distraction_fragment.score_gauge
import kotlinx.android.synthetic.main.speeding_fragment.view.*

internal class DriverDistractionFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(tripDetailViewModel: DKTripDetailViewModel): DriverDistractionFragment {
            val fragment = DriverDistractionFragment()
            fragment.viewModel = tripDetailViewModel
            return fragment
        }
    }

    private lateinit var viewModel: DKTripDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.driver_distraction_fragment, container, false)
        view.setBackgroundColor(Color.WHITE)
        view.score_info.setColorFilter(DriveKitUI.colors.secondaryColor())
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putSerializable("itinId", viewModel.getItindId())
            outState.putSerializable("tripListConfigurationType", viewModel.getTripListConfigurationType())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val itinId = savedInstanceState?.getSerializable("itinId") as String?
        val tripListConfigurationType = savedInstanceState?.getSerializable("tripListConfigurationType") as TripListConfigurationType?

        if (itinId != null && tripListConfigurationType != null) {
            viewModel = ViewModelProviders.of(
                this,
                TripDetailViewModelFactory(itinId, tripListConfigurationType.getTripListConfiguration())
            ).get(TripDetailViewModel::class.java)
        }

        phone_call_selector.apply {
            setOnClickListener(this@DriverDistractionFragment)
            setSelectorContent(viewModel.getPhoneCallsDuration(requireContext()))
            setSelection(false)
        }
        screen_unlock_selector.apply {
            setOnClickListener(this@DriverDistractionFragment)
            setSelectorContent("${viewModel.getUnlockNumberEvent()}")
            setSelection(true)
        }

        score_gauge.configure(viewModel.getDistractionScore(), GaugeType.DISTRACTION, Typeface.BOLD)
        gauge_type_title.text = requireContext().getString(R.string.dk_common_distraction)
        gauge_type_title.normalText()

        phone_call_item.apply {
            setDistractionEventContent(
                viewModel.getPhoneCallsNumber(requireContext()).first,
                viewModel.getPhoneCallsNumber(requireContext()).second
            )
        }

        val unlockEvent = DKResource.convertToString(requireContext(), "dk_driverdata_unlock_event")
        val unlockContent =
            if (viewModel.hasScreenUnlocking()) {
                DKResource.buildString(
                    requireContext(),
                    DriveKitUI.colors.secondaryColor(),
                    DriveKitUI.colors.secondaryColor(),
                    "dk_driverdata_unlock_screen_content",
                    viewModel.getUnlockDuration(requireContext()),
                    viewModel.getUnlockDistance(requireContext())
                ).toString()
            } else {
                DKResource.convertToString(requireContext(), "dk_driverdata_no_screen_unlocking")
            }
        screen_unlock_item.apply {
            setDistractionEventContent(
                unlockEvent,
                unlockContent
            )
            setDistractionContentColor(true)
        }

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
                DKResource.convertToString(requireContext(), "dk_driverdata_distraction_score")
            description?.text = DKResource.convertToString(
                requireContext(),
                "dk_driverdata_distraction_score_info"
            )
            icon?.setImageResource(R.drawable.dk_common_distraction)
            title?.headLine1()
            description?.normalText()
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.screen_unlock_selector -> {
                    viewModel.getSelectedTraceType().postValue(MapTraceType.UNLOCK_SCREEN)
                    screen_unlock_selector.setSelection(true)
                    phone_call_selector.setSelection(false)
                    screen_unlock_item.setDistractionContentColor(true)
                    phone_call_item.setDistractionContentColor(false)
                }
                R.id.phone_call_selector -> {
                    viewModel.getSelectedTraceType().postValue(MapTraceType.PHONE_CALL)
                    phone_call_selector.setSelection(true)
                    screen_unlock_selector.setSelection(false)
                    screen_unlock_item.setDistractionContentColor(false)
                    phone_call_item.setDistractionContentColor(true)
                }
            }
        }
    }
}