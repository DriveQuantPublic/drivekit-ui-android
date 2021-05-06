package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconStatus.*
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
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

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putSerializable("scanType", viewModel.scanType)
            outState.putString("vehicleId", viewModel.vehicleId)
            outState.putSerializable("beacon", viewModel.beacon)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let {
            val scanType = it.getSerializable("scanType") as BeaconScanType?
            val vehicleId = it.getString("vehicleId")
            val beacon = it.getSerializable("beacon") as Beacon?
            if (scanType != null) {
                viewModel = BeaconViewModel(scanType, vehicleId, beacon)
                viewModel.init(requireContext())
            }
        }

        val vehicleName = viewModel.vehicleName ?: ""
        val beaconCode = viewModel.beacon?.code ?: ""

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
         val alert = DKAlertDialog.LayoutBuilder().init(requireContext())
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton()
            .show()

         val description = alert.findViewById<TextView>(R.id.text_view_alert_description)
         description?.text = DKResource.convertToString(requireContext(), identifier)
         description?.normalText()
    }
}