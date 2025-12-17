package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.databinding.FragmentBeaconChildScannerBeaconNotConfiguredBinding

class BeaconScannerBeaconNotConfiguredFragment : Fragment() {

    companion object {
        fun newInstance(viewModel: BeaconViewModel): BeaconScannerBeaconNotConfiguredFragment {
            val fragment = BeaconScannerBeaconNotConfiguredFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel
    private var _binding: FragmentBeaconChildScannerBeaconNotConfiguredBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBeaconChildScannerBeaconNotConfiguredBinding.inflate(inflater, container, false)
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

        binding.buttonCancel.setContent {
            DKPrimaryButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_cancel)) {
                viewModel.scanValidationFinished()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
