package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconStatus.*
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconStep
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_progress.text_view_description
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_success.*

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
        view.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vehicleName = viewModel.vehicleName?.let { it }?:run { "" }
        val beaconCode = viewModel.beacon?.code?.let { it }?:run { "" }

        text_view_description.normalText()
        text_view_description.text = DKResource.buildString(
            requireContext(),
            DriveKitUI.colors.mainFontColor(),
            DriveKitUI.colors.mainFontColor(),
            "dk_vehicle_beacon_setup_code_success_recap",
            beaconCode,
            vehicleName
        )

        button_validate.button()
        button_validate.text = DKResource.convertToString(requireContext(), "dk_common_confirm")
        button_validate.setOnClickListener {
            viewModel.addBeaconToVehicle()
        }

        viewModel.beaconAddObserver.observe(this, Observer {
            it?.let { vehicleBeaconStatus ->
                when (vehicleBeaconStatus){
                    SUCCESS -> viewModel.updateScanState(BeaconStep.CONGRATS)
                    ERROR -> displayErrorAlert("dk_vehicle_failed_to_paired_beacon")
                    UNKNOWN_VEHICLE -> displayErrorAlert("dk_vehicle_unknown")
                    UNAVAILABLE_BEACON -> viewModel.updateScanState(BeaconStep.BEACON_UNAVAILABLE)
                }
            }
        })
    }

    private fun displayErrorAlert(identifier: String){
         DKAlertDialog.LayoutBuilder().init(requireContext())
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton(DKResource.convertToString(requireContext(), identifier),
                DialogInterface.OnClickListener { dialogInterface, _ ->  dialogInterface.dismiss() })
            .show()
    }
}