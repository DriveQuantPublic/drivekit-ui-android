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
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconStep
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.ScanState
import com.drivequant.drivekit.vehicle.ui.utils.NearbyDevicesUtils
import kotlinx.android.synthetic.main.fragment_beacon_scanner.image_view
import kotlinx.android.synthetic.main.fragment_beacon_scanner.text_view_title

class BeaconScannerFragment : Fragment(), ScanState {

    companion object {
        private const val REQUEST_ENABLE_BT = 1

        fun newInstance(viewModel: BeaconViewModel, step: BeaconStep) : BeaconScannerFragment {
            val fragment = BeaconScannerFragment()
            fragment.viewModel = viewModel
            fragment.beaconStep = step
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel
    private lateinit var beaconStep: BeaconStep

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_beacon_scanner, container, false).setDKStyle()
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
                    .positiveButton(
                        DKResource.convertToString(
                            requireContext(),
                            "dk_common_activate"
                        )
                    ) { _, _ ->
                        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
                    }
                    .negativeButton(DKResource.convertToString(requireContext(), "dk_common_back"))
                    .show()

                val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                titleTextView?.apply {
                    text = DKResource.convertToString(
                        requireContext(),
                        "dk_vehicle_beacon_enable_bluetooth_alert_title"
                    )
                    headLine1()
                }
                descriptionTextView?.apply {
                    text = DKResource.convertToString(
                        requireContext(),
                        "dk_vehicle_beacon_enable_bluetooth_alert_message"
                    )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            beaconStep.onImageClicked(viewModel)
        }
    }
}
