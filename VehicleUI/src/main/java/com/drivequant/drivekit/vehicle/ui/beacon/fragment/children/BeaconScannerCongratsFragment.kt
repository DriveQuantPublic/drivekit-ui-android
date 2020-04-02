package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_progress.text_view_description
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_success.*

class BeaconScannerCongratsFragment : Fragment() {
    companion object {
        fun newInstance(viewModel: BeaconViewModel): BeaconScannerCongratsFragment {
            val fragment = BeaconScannerCongratsFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_beacon_child_scanner_congrats, container, false)
        view.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_view_description.normalText()
        text_view_description.text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_store_notice")

        button_validate.button()
        button_validate.text = DKResource.convertToString(requireContext(), "dk_common_finish")
        button_validate.setOnClickListener {
            viewModel.scanValidationFinished()
        }
    }
}