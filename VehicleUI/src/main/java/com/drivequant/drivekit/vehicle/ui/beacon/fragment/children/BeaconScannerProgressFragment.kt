package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.drivequant.beaconutils.BeaconData
import com.drivequant.beaconutils.BeaconInfo
import com.drivequant.beaconutils.BeaconListener
import com.drivequant.beaconutils.BeaconScanner
import com.drivequant.beaconutils.BeaconScannerMode
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType.DIAGNOSTIC
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType.PAIRING
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType.VERIFY
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconStep
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel

class BeaconScannerProgressFragment : Fragment(), BeaconListener {

    companion object {
        fun newInstance(viewModel: BeaconViewModel): BeaconScannerProgressFragment {
            val fragment = BeaconScannerProgressFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel
    private lateinit var updateProgressBar: Thread
    private lateinit var progressBar: ProgressBar
    private var isBeaconFound = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_beacon_child_scanner_progress, container, false).setDKStyle()
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

        view.findViewById<TextView>(R.id.text_view_description).apply {
            normalText()
            setText(R.string.dk_vehicle_beacon_wait_scan)
        }

        view.let {
            progressBar = it.findViewById(R.id.progress_bar)
        }
        runUpdateProgressBarThread()
        startBeaconScan()
    }

    private fun runUpdateProgressBarThread() {
        updateProgressBar = Thread {
            var progressStatus = 0
            while (progressStatus < 100) {
                progressStatus++
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                }
                val finalProgressStatus = progressStatus
                progressBar.progress = finalProgressStatus
            }
            activity?.runOnUiThread {
                if (!isBeaconFound) {
                    stopBeaconScan()
                    if (this.isVisible) {
                        viewModel.updateScanState(BeaconStep.BEACON_NOT_FOUND)
                    }
                }
            }
        }
        updateProgressBar.start()
    }

    private fun startBeaconScan() {
        BeaconScanner.registerListener(this, requireContext())
    }

    private fun stopBeaconScan() {
        BeaconScanner.unregisterListener(this, requireContext())
    }

    private fun goToNextStep(){
        when (viewModel.scanType){
            PAIRING -> {
                viewModel.checkVehiclePaired(object : BeaconViewModel.ServiceListeners {
                    override fun onCheckVehiclePaired(isSameVehicle : Boolean) {
                        viewModel.vehiclePaired?.let {
                            if (isSameVehicle){
                                DriveKitLog.i(DriveKitVehicleUI.TAG, "Beacon scanner: beacon is already paired to this vehicle")
                                Toast.makeText(requireContext(),
                                    R.string.dk_vehicle_beacon_already_paired_to_vehicle,
                                    Toast.LENGTH_LONG)
                                .show()
                                viewModel.scanValidationFinished()
                            } else {
                                viewModel.updateScanState(BeaconStep.BEACON_ALREADY_PAIRED)
                            }
                        }?:run {
                            viewModel.updateScanState(BeaconStep.SUCCESS)
                        }
                    }
                })
            }
            DIAGNOSTIC -> {
                DriveKitVehicle.getVehiclesOrderByNameAsc(object: VehicleListQueryListener {
                    override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                        if (vehicles.isEmpty()){
                            viewModel.updateScanState(BeaconStep.VERIFIED)
                        } else {
                            viewModel.fetchVehicleFromBeacon(vehicles)?.let {
                                viewModel.vehicle = it
                                viewModel.vehicleId = it.vehicleId
                                viewModel.computeVehicleName(requireContext())
                                viewModel.updateScanState(BeaconStep.VERIFIED)
                            } ?: run {
                                viewModel.updateScanState(BeaconStep.WRONG_BEACON)
                            }
                        }
                    }
                }, SynchronizationType.CACHE)

            }
            VERIFY -> {
                if (viewModel.isBeaconValid()){
                    viewModel.updateScanState(BeaconStep.VERIFIED)
                } else {
                    viewModel.updateScanState(BeaconStep.WRONG_BEACON)
                }
            }
        }
    }

    override fun beaconFound(beacon: BeaconInfo) {
        DriveKitLog.i(DriveKitVehicleUI.TAG, "Beacon scanner - Found: major=${beacon.major}, minor=${beacon.minor}")
        stopBeaconScan()
        isBeaconFound = true
        viewModel.seenBeacon = beacon
        goToNextStep()
    }

    override fun beaconList(): List<BeaconData> {
        val beaconsToCheck = mutableListOf<BeaconData>()
        viewModel.beacon?.let {
            val beaconData = when (viewModel.scanType){
                PAIRING -> BeaconData(it.proximityUuid, it.major, it.minor)
                DIAGNOSTIC, VERIFY -> BeaconData(it.proximityUuid)
            }
            beaconsToCheck.add(beaconData)
        }
        return beaconsToCheck
    }

    override fun scanMode(): BeaconScannerMode {
        return BeaconScannerMode.LOW_LATENCY
    }

    override fun onDestroy() {
        stopBeaconScan()
        super.onDestroy()
    }
}
