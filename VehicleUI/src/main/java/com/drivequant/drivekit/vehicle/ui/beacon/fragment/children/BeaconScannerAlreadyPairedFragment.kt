package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconStatus.ERROR
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconStatus.SUCCESS
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconStatus.UNAVAILABLE_BEACON
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconStatus.UNKNOWN_VEHICLE
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconStep
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.databinding.FragmentBeaconChildScannerAlreadyPairedBinding
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName

class BeaconScannerAlreadyPairedFragment : Fragment() {

    companion object {
        fun newInstance(viewModel: BeaconViewModel): BeaconScannerAlreadyPairedFragment {
            val fragment = BeaconScannerAlreadyPairedFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel
    private var _binding: FragmentBeaconChildScannerAlreadyPairedBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBeaconChildScannerAlreadyPairedBinding.inflate(inflater, container, false)
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

        val beaconCode = viewModel.beacon?.code ?: ""
        val vehicleName = viewModel.vehicleName ?: ""

        viewModel.vehiclePaired?.let {
            val vehiclePairedName = it.buildFormattedName(requireContext())
            binding.textViewDescription.text = DKResource.buildString(
                requireContext(),
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.mainFontColor(),
                "dk_vehicle_beacon_setup_replace_description",
                beaconCode,
                vehicleName,
                vehiclePairedName
            )
        }
        binding.textViewDescription.normalText()

        binding.buttonValidate.button()
        binding.buttonValidate.text = DKResource.convertToString(requireContext(), "dk_common_confirm")
        binding.buttonValidate.setOnClickListener {
            viewModel.changeBeaconToVehicle()
        }

        binding.buttonAbort.button()
        binding.buttonAbort.typeface = Typeface.DEFAULT_BOLD
        binding.buttonAbort.text = DKResource.convertToString(requireContext(), "dk_common_cancel")
        binding.buttonAbort.setOnClickListener {
            viewModel.scanValidationFinished()
        }

        viewModel.beaconChangeObserver.observe(viewLifecycleOwner) {
            it?.let { vehicleBeaconStatus ->
                DriveKitLog.i(DriveKitVehicleUI.TAG, "Beacon scanner - Change beacon status: $vehicleBeaconStatus")
                when (vehicleBeaconStatus) {
                    SUCCESS -> viewModel.updateScanState(BeaconStep.CONGRATS)
                    ERROR -> displayErrorAlert("dk_vehicle_failed_to_paired_beacon")
                    UNKNOWN_VEHICLE -> displayErrorAlert("dk_vehicle_unknown")
                    UNAVAILABLE_BEACON -> viewModel.updateScanState(BeaconStep.BEACON_UNAVAILABLE)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
