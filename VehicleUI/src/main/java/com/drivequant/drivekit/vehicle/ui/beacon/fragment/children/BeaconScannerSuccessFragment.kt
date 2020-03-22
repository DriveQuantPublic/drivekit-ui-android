package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.drivequant.beaconutils.*
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.tripanalysis.TripAnalysisConfig
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType.*
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconStep
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_progress.*

class BeaconScannerSuccessFragment : Fragment() {
    companion object {
        fun newInstance(viewModel: BeaconViewModel): BeaconScannerSuccessFragment {
            val fragment = BeaconScannerSuccessFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_beacon_child_scanner_success, container, false)
        FontUtils.overrideFonts(context, view)
        view.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_view_description.normalText()
        text_view_description.text = DKResource.convertToString(requireContext(), "Found") // TODO
    }
}