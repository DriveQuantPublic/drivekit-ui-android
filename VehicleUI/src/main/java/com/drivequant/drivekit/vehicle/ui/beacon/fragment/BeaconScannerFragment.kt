package com.drivequant.drivekit.vehicle.ui.beacon.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconStep
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.ScanState
import kotlinx.android.synthetic.main.fragment_beacon_scanner.*

class BeaconScannerFragment : Fragment(), ScanState {

    companion object {
        fun newInstance(viewModel: BeaconViewModel, step: BeaconStep) : BeaconScannerFragment {
            val fragment = BeaconScannerFragment()
            fragment.viewModel = viewModel
            fragment.beaconStep = step
            return fragment
        }
    }

    private lateinit var viewModel : BeaconViewModel
    private lateinit var beaconStep : BeaconStep

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_beacon_scanner, container, false).setDKStyle()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.listener = this
        updateStep(beaconStep)
        val titleIdentifier = when (viewModel.scanType){
            BeaconScanType.PAIRING -> "dk_beacon_paired_title"
            BeaconScanType.DIAGNOSTIC, BeaconScanType.VERIFY -> "dk_beacon_diagnostic_title"
        }
        activity?.title = DKResource.convertToString(requireContext(), titleIdentifier)
        updateChildFragment()
    }

    private fun updateStep(beaconStep: BeaconStep){
        this.beaconStep = beaconStep
        val mainFontColor = DriveKitUI.colors.mainFontColor()
        text_view_title.normalText(mainFontColor)
        text_view_title.text = beaconStep.getTitle(requireContext(), viewModel)
        image_view.setImageDrawable(beaconStep.getImage(requireContext()))
    }

    private fun updateChildFragment(){
        activity?.supportFragmentManager?.let { fragmentManager ->
            beaconStep.getChildFragment(viewModel)?.let { fragment ->
                fragmentManager.beginTransaction()
                    .replace(R.id.container_child, fragment)
                    .commit()
            }
        }
    }

    override fun onStateUpdated(step: BeaconStep) {
        updateStep(step)
        updateChildFragment()
    }

    override fun onScanFinished() {
        activity?.finish()
    }

    override fun displayLoader() {
        // TODO("Not yet implemented")
    }

    override fun hideLoader() {
        // TODO("Not yet implemented")
    }
}