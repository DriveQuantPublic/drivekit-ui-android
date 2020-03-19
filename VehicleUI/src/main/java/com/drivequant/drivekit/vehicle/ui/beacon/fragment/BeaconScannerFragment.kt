package com.drivequant.drivekit.vehicle.ui.beacon.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconStep
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.ScanState
import kotlinx.android.synthetic.main.fragment_beacon_scanner.*

class BeaconScannerFragment : Fragment(), ScanState {

    companion object {
        fun newInstance(viewModel: BeaconViewModel) : BeaconScannerFragment {
            val fragment = BeaconScannerFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel : BeaconViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_beacon_scanner, container, false).setDKStyle()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mainFontColor = DriveKitUI.colors.mainFontColor()
        text_view_title.normalText(mainFontColor)

    }

    override fun onStateUpdated(step: BeaconStep) {
        text_view_title.text = step.getTitle(requireContext())
        image_view.setImageDrawable(step.getImage(requireContext()))
    }

    override fun onScanFinished() {
        TODO("Not yet implemented")
    }

    override fun displayLoader() {
        TODO("Not yet implemented")
    }

    override fun hideLoader() {
        TODO("Not yet implemented")
    }
}