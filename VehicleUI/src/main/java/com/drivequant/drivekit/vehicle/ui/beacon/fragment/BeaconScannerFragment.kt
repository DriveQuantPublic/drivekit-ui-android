package com.drivequant.drivekit.vehicle.ui.beacon.fragment

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconStep
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.ScanState
import com.drivequant.drivekit.vehicle.ui.databinding.FragmentBeaconScannerBinding
import com.drivequant.drivekit.vehicle.ui.utils.NearbyDevicesUtils

class BeaconScannerFragment : Fragment(), ScanState {

    companion object {
        private const val REQUEST_ENABLE_BT = 1

        fun newInstance(viewModel: BeaconViewModel, step: BeaconStep): BeaconScannerFragment {
            val fragment = BeaconScannerFragment()
            fragment.viewModel = viewModel
            fragment.beaconStep = step
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel
    private lateinit var beaconStep: BeaconStep
    private var _binding: FragmentBeaconScannerBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBeaconScannerBinding.inflate(inflater, container, false)
        binding.root.setDKStyle()
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putSerializable("scanType", viewModel.scanType)
            outState.putString("vehicleId", viewModel.vehicleId)
            outState.putSerializable("beacon", viewModel.beacon)
            outState.putSerializable("beaconStep", beaconStep)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            val scanType = it.getSerializableCompat("scanType", BeaconScanType::class.java)
            val vehicleId = it.getString("vehicleId")
            val beacon = it.getSerializableCompat("beacon", Beacon::class.java)
            beaconStep = it.getSerializableCompat("beaconStep", BeaconStep::class.java)!!

            if (scanType != null) {
                viewModel = BeaconViewModel(scanType, vehicleId, beacon)
                viewModel.init(requireContext())
            }
        }

        viewModel.listener = this
        updateStep(beaconStep)
        updateChildFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateStep(beaconStep: BeaconStep){
        this.beaconStep = beaconStep
        binding.textViewTitle.text = beaconStep.getTitle(requireContext(), viewModel)
        binding.textViewTitle.normalText()
        binding.imageView.setImageDrawable(beaconStep.getImage(requireContext()))
        binding.imageView.setOnClickListener {
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
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                titleTextView?.apply {
                    setText(R.string.dk_vehicle_beacon_enable_bluetooth_alert_title)
                    headLine1()
                }
                descriptionTextView?.apply {
                    setText(R.string.dk_vehicle_beacon_enable_bluetooth_alert_message)
                    normalText()
                }
            } else if (!NearbyDevicesUtils.isBluetoothScanAuthorized()) {
                activity?.let {
                    NearbyDevicesUtils.displayPermissionsError(it)
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
                    .commitAllowingStateLoss()
            }
        }
    }

    override fun onStateUpdated(step: BeaconStep) {
        DriveKitLog.i(DriveKitVehicleUI.TAG, "Beacon scanner step update: $step")
        updateStep(step)
        updateChildFragment()
    }

    override fun onScanFinished() {
        activity?.finish()
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            beaconStep.onImageClicked(viewModel)
        }
    }
}
