package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconStep
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_not_found.*
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_progress.text_view_description

class BeaconScannerNotFoundFragment : Fragment() {
    companion object {
        fun newInstance(viewModel: BeaconViewModel): BeaconScannerNotFoundFragment {
            val fragment = BeaconScannerNotFoundFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_beacon_child_scanner_not_found, container, false).setDKStyle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_view_description.normalText()
        text_view_description.text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_scan_retry")

        button_retry.setImageDrawable(DKResource.convertToDrawable(requireContext(), "dk_beacon_retry"))
        button_retry.setOnClickListener {
            viewModel.updateScanState(BeaconStep.SCAN)
        }

        if (viewModel.scanType != BeaconScanType.PAIRING) {
            button_cancel.visibility = View.GONE
            button_abort.visibility = View.GONE
        } else {
            button_cancel.button()
            button_cancel.text = DKResource.convertToString(requireContext(), "dk_common_cancel")
            button_cancel.setOnClickListener {
                activity?.onBackPressed()
            }

            button_abort.normalText(DriveKitUI.colors.secondaryColor())
            button_abort.typeface = Typeface.DEFAULT_BOLD
            button_abort.text = DKResource.convertToString(requireContext(), "dk_common_finish")
            button_abort.setOnClickListener {
                viewModel.scanValidationFinished()
            }
        }
    }
}