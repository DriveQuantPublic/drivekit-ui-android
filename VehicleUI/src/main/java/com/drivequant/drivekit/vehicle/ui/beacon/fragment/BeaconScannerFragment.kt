package com.drivequant.drivekit.vehicle.ui.beacon.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
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
        updateChildFragment()
    }

    private fun updateStep(beaconStep: BeaconStep){
        this.beaconStep = beaconStep
        text_view_title.text = beaconStep.getTitle(requireContext(), viewModel)
        text_view_title.normalText(DriveKitUI.colors.mainFontColor())
        image_view.setImageDrawable(beaconStep.getImage(requireContext()))
        image_view.setOnClickListener {
            if (!viewModel.isBluetoothSensorEnabled()) {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(requireContext())
                    .layout(R.layout.template_alert_dialog_layout)
                    .positiveButton(DKResource.convertToString(requireContext(), "dk_common_ok"),
                        DialogInterface.OnClickListener { _, _ ->
                            viewModel.enableBluetoothSensor()
                            beaconStep.onImageClicked(viewModel)
                        })
                    .negativeButton(DKResource.convertToString(requireContext(), "dk_common_close"),
                        DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                    .show()

                val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                titleTextView?.apply {
                    text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_enable_bluetooth_alert_title")
                    headLine1()
                }
                descriptionTextView?.apply {
                    text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_enable_bluetooth_alert_message")
                    normalText()
                }
            } else {
                beaconStep.onImageClicked(viewModel)
            }
        }
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
}