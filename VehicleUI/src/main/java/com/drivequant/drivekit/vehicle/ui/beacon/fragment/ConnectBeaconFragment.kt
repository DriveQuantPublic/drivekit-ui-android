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
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.injectContent
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.databinding.FragmentBeaconConnectBinding

class ConnectBeaconFragment : Fragment() {

    companion object {
        private const val REQUEST_ENABLE_BT = 1

        fun newInstance(viewModel: BeaconViewModel): ConnectBeaconFragment {
            val fragment = ConnectBeaconFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel
    private var _binding: FragmentBeaconConnectBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBeaconConnectBinding.inflate(inflater, container, false)
        binding.root.setDKStyle()
        return binding.root
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

        binding.textViewConnectTitle.headLine1()
        binding.textViewConnectDesc1.normalText()
        binding.textViewConnectDesc2.normalText()
        binding.textViewConnectDesc3.normalText()
        binding.buttonBegin.injectContent {
            DKPrimaryButton(getString(R.string.dk_vehicle_begin)) {
                if (!viewModel.isBluetoothSensorEnabled()) {
                    val alertDialog = DKAlertDialog.LayoutBuilder()
                        .init(requireContext())
                        .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                        .positiveButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_activate)) { _, _ ->
                            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
                        }
                        .negativeButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_back))
                        .show()

                    val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                    val descriptionTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                    titleTextView?.apply {
                        setText(R.string.dk_vehicle_beacon_enable_bluetooth_alert_title)
                        headLine1()
                    }
                    descriptionTextView?.apply {
                        setText(R.string.dk_vehicle_beacon_enable_bluetooth_alert_message)
                        normalText()
                    }
                } else {
                    viewModel.onConnectButtonClicked()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            viewModel.onConnectButtonClicked()
        }
    }
}
