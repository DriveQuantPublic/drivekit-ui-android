package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_beacon_unavailable.*

class BeaconScannerBeaconUnavailableFragment : Fragment() {
    companion object {
        fun newInstance(viewModel: BeaconViewModel): BeaconScannerBeaconUnavailableFragment {
            val fragment = BeaconScannerBeaconUnavailableFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_beacon_child_scanner_beacon_unavailable, container, false).setDKStyle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_view_description.normalText()
        text_view_description.text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_code_retry_title")

        button_retry.button()
        button_retry.text = DKResource.convertToString(requireContext(), "dk_common_cancel")
        button_retry.setOnClickListener {
            activity?.onBackPressed()
        }

        button_abort.normalText(DKColors().secondaryColor())
        button_abort.typeface = Typeface.DEFAULT_BOLD
        button_abort.text = DKResource.convertToString(requireContext(), "dk_common_finish")
        button_abort.setOnClickListener {
            viewModel.scanValidationFinished()
        }
    }
}