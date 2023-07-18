package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_beacon_unavailable.button_abort
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_beacon_unavailable.button_retry
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_beacon_unavailable.text_view_description

class BeaconScannerBeaconUnavailableFragment : Fragment() {
    companion object {
        fun newInstance(viewModel: BeaconViewModel): BeaconScannerBeaconUnavailableFragment {
            val fragment = BeaconScannerBeaconUnavailableFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_beacon_child_scanner_beacon_unavailable, container, false).setDKStyle()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putSerializable("scanType", viewModel.scanType)
            outState.putString("vehicleId", viewModel.vehicleId)
            outState.putSerializable("beacon", viewModel.beacon)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            val scanType = it.getSerializableCompat("scanType", BeaconScanType::class.java)
            val vehicleId = it.getString("vehicleId")
            val beacon = it.getSerializableCompat("beacon", Beacon::class.java)
            if (scanType != null) {
                viewModel = BeaconViewModel(scanType, vehicleId, beacon)
                viewModel.init(requireContext())
            }
        }

        text_view_description.normalText()
        text_view_description.text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_code_retry_title")

        button_retry.button()
        button_retry.text = DKResource.convertToString(requireContext(), "dk_common_cancel")
        button_retry.setOnClickListener {
            activity?.onBackPressed()
        }

        button_abort.apply {
            normalText(DriveKitUI.colors.secondaryColor())
            text = DKResource.convertToString(requireContext(), "dk_common_finish")
            setOnClickListener {
                viewModel.scanValidationFinished()
            }
        }
    }
}
