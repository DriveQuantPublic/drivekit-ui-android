package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import androidx.lifecycle.Observer
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconStatus.*
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconStep
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_already_paired.*

class BeaconScannerAlreadyPairedFragment : Fragment() {
    companion object {
        fun newInstance(viewModel: BeaconViewModel): BeaconScannerAlreadyPairedFragment {
            val fragment = BeaconScannerAlreadyPairedFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_beacon_child_scanner_already_paired,
            container,
            false
        ).setDKStyle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val beaconCode = viewModel.beacon?.code ?: ""
        val vehicleName = viewModel.vehicleName ?: ""

        viewModel.vehiclePaired?.let {
            val vehiclePairedName = it.buildFormattedName(requireContext())
            text_view_description.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.mainFontColor(),
                "dk_vehicle_beacon_setup_replace_description",
                beaconCode,
                vehicleName,
                vehiclePairedName
            )
        }
        text_view_description.normalText()

        button_validate.button()
        button_validate.text = DKResource.convertToString(requireContext(), "dk_common_confirm")
        button_validate.setOnClickListener {
            viewModel.changeBeaconToVehicle()
        }

        button_abort.button()
        button_abort.typeface = Typeface.DEFAULT_BOLD
        button_abort.text = DKResource.convertToString(requireContext(), "dk_common_cancel")
        button_abort.setOnClickListener {
            viewModel.scanValidationFinished()
        }

        viewModel.beaconChangeObserver.observe(this, Observer {
            it?.let { vehicleBeaconStatus ->
                when (vehicleBeaconStatus) {
                    SUCCESS -> viewModel.updateScanState(BeaconStep.CONGRATS)
                    ERROR -> displayErrorAlert("dk_vehicle_failed_to_paired_beacon")
                    UNKNOWN_VEHICLE -> displayErrorAlert("dk_vehicle_unknown")
                    UNAVAILABLE_BEACON -> viewModel.updateScanState(BeaconStep.BEACON_UNAVAILABLE)
                }
            }
        })
    }

    private fun displayErrorAlert(identifier: String) {
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