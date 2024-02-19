package com.drivequant.drivekit.vehicle.ui.beacon.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.bigText
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconInfoStatus
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.databinding.FragmentBeaconInputIdBinding
import com.drivequant.drivekit.vehicle.ui.utils.NearbyDevicesUtils

class BeaconInputIdFragment : Fragment(), BeaconViewModel.BeaconInfoStatusListener {

    companion object {
        fun newInstance(viewModel: BeaconViewModel): BeaconInputIdFragment {
            val fragment = BeaconInputIdFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel
    private var _binding: FragmentBeaconInputIdBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBeaconInputIdBinding.inflate(inflater, container, false)
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
        viewModel.beaconInfoStatusListener = this

        if (!NearbyDevicesUtils.isBluetoothScanAuthorized()) {
            activity?.let {
                NearbyDevicesUtils.displayPermissionsError(it)
            }
        }

        binding.textViewBeaconCodeText.bigText(DriveKitUI.colors.mainFontColor())
        binding.codeWrapper.apply {
            typeface = DriveKitUI.primaryFont(context)
        }
        binding.buttonValidate.setOnClickListener {
            manageValidateClick(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayError(message: String) {
        val alert = DKAlertDialog.LayoutBuilder()
            .init(requireContext())
            .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
            .cancelable(true)
            .positiveButton()
            .show()

        val title = alert.findViewById<TextView>(R.id.text_view_alert_title)
        val description = alert.findViewById<TextView>(R.id.text_view_alert_description)

        title?.text = DKResource.buildString(
            requireContext(),
            textColor = DriveKitUI.colors.mainFontColor(),
            identifier = R.string.app_name
        )
        title?.headLine1()

        description?.text = DKResource.buildString(requireContext(),
            DriveKitUI.colors.mainFontColor(),
            DriveKitUI.colors.mainFontColor(),
            R.string.dk_vehicle_beacon_setup_code_invalid_id,
            message
        )
        description?.normalText()
    }

    private fun manageValidateClick(view: View){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        val codeValue: String = binding.codeField.text.toString().trim()
        if (codeValue.isEmpty()){
            binding.codeWrapper.isErrorEnabled = true
            binding.codeWrapper.error = getString(com.drivequant.drivekit.common.ui.R.string.dk_common_error_empty_field)
        } else {
            binding.codeWrapper.isErrorEnabled = false
            viewModel.checkCode(codeValue)
        }
    }

    override fun onBeaconStatusReceived(beaconCode: String, status: VehicleBeaconInfoStatus) {
        when (status) {
            VehicleBeaconInfoStatus.SUCCESS -> viewModel.onCodeValid()
            VehicleBeaconInfoStatus.ERROR, VehicleBeaconInfoStatus.UNKNOWN_BEACON -> displayError(beaconCode)
        }
    }
}
