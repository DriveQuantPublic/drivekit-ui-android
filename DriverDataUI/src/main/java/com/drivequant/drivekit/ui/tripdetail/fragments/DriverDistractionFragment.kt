package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeConfiguration
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.*
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import kotlinx.android.synthetic.main.driver_distraction_fragment.*
import kotlinx.android.synthetic.main.driver_distraction_fragment.gauge_type_title
import kotlinx.android.synthetic.main.driver_distraction_fragment.score_gauge
import kotlinx.android.synthetic.main.driver_distraction_fragment.score_info

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
        }
        screen_unlock_selector.apply {
            setOnClickListener(this@DriverDistractionFragment)
            setSelectorContent("${viewModel.getUnlockNumberEvent()}")
        }

        viewModel.getSelectedTraceType().value?.let {
            onSelectorChanged(it)
        } ?: run {
            onSelectorChanged(MapTraceType.UNLOCK_SCREEN)
        }

        score_gauge.configure(viewModel.getDistractionScore(), GaugeConfiguration.DISTRACTION(viewModel.getDistractionScore()), Typeface.BOLD)
        score_info.init(GaugeConfiguration.DISTRACTION(viewModel.getDistractionScore()))
        gauge_type_title.text = requireContext().getString(R.string.dk_common_distraction)
        gauge_type_title.normalText()

        phone_call_item.apply {
            setDistractionEventContent(
                viewModel.getPhoneCallsNumber(requireContext()).first,
                viewModel.getPhoneCallsNumber(requireContext()).second
            )
        }

        val unlockEvent = DKResource.convertToString(requireContext(), "dk_driverdata_unlock_events")
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
        screen_unlock_item.setDistractionEventContent(
            unlockEvent,
            unlockContent
        )
    }

    private fun onSelectorChanged(mapTraceType: MapTraceType) {
        when(mapTraceType) {
            MapTraceType.UNLOCK_SCREEN -> {
                screen_unlock_selector.setSelection(true)
                phone_call_selector.setSelection(false)
                screen_unlock_item.setDistractionContentColor(true)
                phone_call_item.setDistractionContentColor(false)
            }
            MapTraceType.PHONE_CALL ->{
                phone_call_selector.setSelection(true)
                screen_unlock_selector.setSelection(false)
                screen_unlock_item.setDistractionContentColor(false)
                phone_call_item.setDistractionContentColor(true)
            }
            else -> {
                //DO NOTHING
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.screen_unlock_selector -> {
                    MapTraceType.UNLOCK_SCREEN.let { mapTraceType ->
                        viewModel.getSelectedTraceType().postValue(mapTraceType)
                        onSelectorChanged(mapTraceType)
                    }
                }
                R.id.phone_call_selector -> {
                    MapTraceType.PHONE_CALL.let { mapTraceType ->
                        viewModel.getSelectedTraceType().postValue(mapTraceType)
                        onSelectorChanged(mapTraceType)
                    }
                }
            }
        }
    }
}