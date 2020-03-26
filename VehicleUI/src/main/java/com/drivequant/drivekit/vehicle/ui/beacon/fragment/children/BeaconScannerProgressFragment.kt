package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.drivequant.beaconutils.*
import com.drivequant.beaconutils.compatibility.BeaconScannerPreLollipop
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.tripanalysis.TripAnalysisConfig
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType.*
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconStep
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_progress.*

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_beacon_child_scanner_progress, container, false).setDKStyle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_view_description.normalText()
        text_view_description.text = DKResource.convertToString(requireContext(), "dk_vehicle_beacon_wait_scan")
        progressBar = view.findViewById(R.id.progress_bar)
        runUpdateProgressBarThread()
        startBeaconScan()
    }

    private fun runUpdateProgressBarThread() {
        updateProgressBar = Thread(Runnable {
            var progressStatus = 0
            while (progressStatus < 100) {
                progressStatus++
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) { }
                val finalProgressStatus = progressStatus
                progressBar.progress = finalProgressStatus
            }
            activity?.runOnUiThread {
                if (!isBeaconFound) {
                    stopBeaconScan()
                    viewModel.updateScanState(BeaconStep.BEACON_NOT_FOUND)
                }
            }
        })
        updateProgressBar.start()
    }

    private fun startBeaconScan(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BeaconScanner.registerListener(this, requireContext())
        } else {
            BeaconScannerPreLollipop.registerBeaconListener(requireContext(), this)
        }
    }

    private fun stopBeaconScan(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BeaconScanner.unregisterListener(this, requireContext())
        } else {
            BeaconScannerPreLollipop.unregisterBeaconListener()
        }
    }

    private fun goToNextStep(){
        when (viewModel.scanType){
            PAIRING -> {
                viewModel.showLoader()
                viewModel.checkVehiclePaired(object : BeaconViewModel.ServiceListeners {
                    override fun onCheckVehiclePaired(isSameVehicle : Boolean) {
                        viewModel.hideLoader()
                        viewModel.vehiclePaired?.let {
                            if (isSameVehicle){
                                Toast.makeText(requireContext(),
                                    DKResource.convertToString(requireContext(), "dk_vehicle_beacon_already_paired_to_vehicle"),
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
                viewModel.fetchVehicleFromBeacon()?.let {
                    viewModel.vehicle = it
                    viewModel.vehicleId = it.vehicleId
                    viewModel.computeVehicleName(requireContext())
                    viewModel.updateScanState(BeaconStep.VERIFIED)
                }?:run  {
                    viewModel.updateScanState(BeaconStep.WRONG_BEACON)
                }
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
        stopBeaconScan()
        isBeaconFound = true
        viewModel.seenBeacon = beacon
        goToNextStep()
    }

    override fun beaconList(): List<BeaconData> {
        return TripAnalysisConfig.getAllBeacons()
    }

    override fun scanMode(): BeaconScannerMode {
        return BeaconScannerMode.LOW_LATENCY
    }

    override fun onDestroy() {
        stopBeaconScan()
        super.onDestroy()
    }
}