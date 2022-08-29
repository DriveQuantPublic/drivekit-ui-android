package com.drivequant.drivekit.vehicle.ui.beacon.fragment

import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.fragment_beacon_connect.*


class ConnectBeaconFragment : Fragment() {

    companion object {
        private const val REQUEST_ENABLE_BT = 1

        fun newInstance(viewModel: BeaconViewModel) : ConnectBeaconFragment {
            val fragment = ConnectBeaconFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel : BeaconViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_beacon_connect, container, false).setDKStyle()
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

        text_view_connect_title.apply {
            headLine1()
            text =
                DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_guide_title")
        }
        text_view_connect_desc_1.apply {
            text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_guide_desc1")
            normalText()
        }
        text_view_connect_desc_2.apply {
            text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_guide_desc2")
            normalText()
        }
        text_view_connect_desc_3.apply {
            normalText()
            text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_setup_guide_desc3")
        }

        button_begin.button()
        button_begin.text =  DKResource.convertToString(requireContext(), "dk_vehicle_begin")
        button_begin.setOnClickListener {
            if (!viewModel.isBluetoothSensorEnabled()) {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(requireContext())
                    .layout(R.layout.template_alert_dialog_layout)
                    .positiveButton(
                        DKResource.convertToString(
                            requireContext(), "dk_common_activate")
                    ) { _, _ ->
                        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
                    }
                    .negativeButton(DKResource.convertToString(requireContext(), "dk_common_back"))
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
                viewModel.onConnectButtonClicked()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            viewModel.onConnectButtonClicked()
        }
    }
}