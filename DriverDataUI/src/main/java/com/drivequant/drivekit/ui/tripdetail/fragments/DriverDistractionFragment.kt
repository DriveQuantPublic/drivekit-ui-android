package com.drivequant.drivekit.ui.tripdetail.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.GaugeType
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKTripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapTraceType
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import kotlinx.android.synthetic.main.driver_distraction_fragment.*

class DriverDistractionFragment : Fragment(), View.OnClickListener {

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
        outState.putSerializable("viewModel", viewModel)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("viewModel") as TripDetailViewModel?)?.let {
            viewModel = it
        }
        phone_call_selector.setSelection(true)

        phone_call_selector.apply {
            setOnClickListener(this@DriverDistractionFragment)
            setSelectorContent(viewModel.getPhoneCallsDuration(requireContext()))
        }
        screen_unlock_selector.apply {
            setOnClickListener(this@DriverDistractionFragment)
            setSelectorContent("${viewModel.getUnlockNumberEvent()}")
        }

        score_gauge.configure(viewModel.getScore(), GaugeType.DISTRACTION, Typeface.BOLD)
        gauge_type_title.text = requireContext().getString(R.string.dk_common_distraction)
        gauge_type_title.normalText()

        phone_call_item.apply {
            setDistractionEventContent(
                viewModel.getPhoneCallsNumber(requireContext()).first,
                viewModel.getPhoneCallsNumber(requireContext()).second
            )
        }
        val unlockEventId =
            if (viewModel.getUnlockNumberEvent() == 0) "dk_driverdata_no_screen_unlocking" else "dk_driverdata_unlock_event"
        val unlockEvent = DKResource.convertToString(requireContext(), unlockEventId)
        val unlockContent = DKResource.buildString(
            requireContext(),
            DriveKitUI.colors.secondaryColor(),
            DriveKitUI.colors.secondaryColor(),
            "dk_driverdata_unlock_screen_content",
            viewModel.getUnlockDuration(requireContext()),
            viewModel.getUnlockDistance(requireContext())
        )
        screen_unlock_item.apply {
            setDistractionEventContent(
                unlockEvent,
                unlockContent.toString()
            )
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.screen_unlock_selector -> {
                    viewModel.getSelectedTraceType().postValue(MapTraceType.UNLOCK_SCREEN)
                    screen_unlock_selector.setSelection(true)
                    phone_call_selector.setSelection(false)
                }
                R.id.phone_call_selector -> {
                    viewModel.getSelectedTraceType().postValue(MapTraceType.PHONE_CALL)
                    screen_unlock_selector.setSelection(true)
                    phone_call_selector.setSelection(false)
                }
            }
        }
    }
}