package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.drivequant.beaconutils.BeaconData
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.databinding.FragmentBeaconChildScannerInfoBinding
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import com.drivequant.drivekit.vehicle.ui.utils.BeaconInfoScannerManager
import com.drivequant.drivekit.vehicle.ui.utils.DKBeaconInfoListener
import com.drivequant.drivekit.vehicle.ui.utils.DKBeaconRetrievedInfo
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils

class BeaconScannerInfoFragment : Fragment() {

    companion object {
        fun newInstance(viewModel: BeaconViewModel, isValid: Boolean): BeaconScannerInfoFragment {
            val fragment = BeaconScannerInfoFragment()
            fragment.viewModel = viewModel
            fragment.isValid = isValid
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel
    private var isValid: Boolean = false
    private var beaconBatteryScannerManager: BeaconInfoScannerManager? = null
    private var _binding: FragmentBeaconChildScannerInfoBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBeaconChildScannerInfoBinding.inflate(inflater, container, false)
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
        viewModel.seenBeacon?.let { beacon ->
            context?.let { context ->
                updateProgressVisibility(true)
                beaconBatteryScannerManager = BeaconInfoScannerManager(
                    context,
                    BeaconData(beacon.proximityUuid, beacon.major, beacon.minor),
                    object : DKBeaconInfoListener {
                        override fun onBeaconInfoRetrieved(beaconRetrievedInfo: DKBeaconRetrievedInfo) {
                            beaconBatteryScannerManager?.stopBatteryReaderScanner()
                            updateProgressVisibility(false)
                            binding.buttonBeaconInfo.visibility = View.VISIBLE
                            viewModel.apply {
                                this.batteryLevel = beaconRetrievedInfo.batteryLevel
                                this.estimatedDistance = beaconRetrievedInfo.estimatedDistance
                                this.rssi = beaconRetrievedInfo.rssi
                                this.txPower = beaconRetrievedInfo.txPower
                            }
                            if (isAdded) {
                                binding.textViewBattery.text = buildBeaconCharacteristics("${beaconRetrievedInfo.batteryLevel}", "%")
                                computeBatteryDrawable(beaconRetrievedInfo.batteryLevel)?.let { drawable ->
                                    binding.textViewBattery.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null)
                                }

                                binding.textViewDistance.text = buildBeaconCharacteristics(beaconRetrievedInfo.estimatedDistance.format(1), getString(com.drivequant.drivekit.common.ui.R.string.dk_common_unit_meter))
                                ContextCompat.getDrawable(requireContext(), R.drawable.dk_beacon_distance)?.let { drawable ->
                                    binding.textViewDistance.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null)
                                }

                                binding.textViewSignalIntensity.text = buildBeaconCharacteristics("${beaconRetrievedInfo.rssi}", "dBm")
                                ContextCompat.getDrawable(requireContext(), R.drawable.dk_beacon_signal_intensity)?.let { drawable ->
                                    binding.textViewSignalIntensity.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null)
                                }
                            }
                        }
                    }
                )
                beaconBatteryScannerManager?.startBatteryReaderScanner()
            }
        }

        binding.textViewConnectedVehicleName.headLine2()

        if (isValid) {
            binding.viewBorder.setBackgroundColor(DKColors.secondaryColor)
            viewModel.vehicleName?.let {
                binding.textViewConnectedVehicleName.text = it
            } ?: run {
                binding.textViewConnectedVehicleName.setText(R.string.dk_beacon_vehicle_unknown)
            }
        } else {
            binding.viewBorder.setBackgroundColor(DKColors.complementaryFontColor)
            binding.textViewConnectedVehicleName.text = viewModel.fetchVehicleFromSeenBeacon(VehicleUtils().fetchVehiclesOrderedByDisplayName(requireContext()))?.let { vehicle ->
                 vehicle.buildFormattedName(requireContext())
            }?: run {
                getString(R.string.dk_beacon_vehicle_unknown)
            }
        }

        configureInfoButton()

        binding.textViewMajorTitle.apply {
            normalText()
            setText(R.string.dk_beacon_major)
        }
        binding.textViewMajorValue.apply {
            headLine2()
            text = viewModel.seenBeacon?.major.toString()
        }
        binding.textViewMinorTitle.apply {
            normalText()
            setText(R.string.dk_beacon_minor)
        }
        binding.textViewMinorValue.apply {
            headLine2()
            text = viewModel.seenBeacon?.minor.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configureInfoButton() {
        binding.buttonBeaconInfo.setImageResource(com.drivequant.drivekit.common.ui.R.drawable.dk_common_info)
        binding.buttonBeaconInfo.drawable.tintDrawable(DKColors.secondaryColor)
        binding.buttonBeaconInfo.setOnClickListener {
            viewModel.launchDetailFragment()
        }
    }

    override fun onStop() {
        super.onStop()
        beaconBatteryScannerManager?.stopBatteryReaderScanner()
    }

    private fun computeBatteryDrawable(batteryLevel: Int) = when {
        batteryLevel >= 75 -> R.drawable.dk_beacon_battery_100
        batteryLevel >= 50 -> R.drawable.dk_beacon_battery_75
        batteryLevel >= 25 -> R.drawable.dk_beacon_battery_50
        else -> R.drawable.dk_beacon_battery_25
    }.let {
        ContextCompat.getDrawable(requireContext(), it)
    }

    private fun buildBeaconCharacteristics(value: String, unit: String): Spannable {
        val mainFontColor = DKColors.mainFontColor
        val primaryColor = DKColors.primaryColor

        return DKSpannable()
            .append(value, requireContext().resSpans {
                color(primaryColor)
                typeface(Typeface.BOLD)
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_medium)
            })
            .append(" ")
            .append(unit, requireContext().resSpans {
                color(mainFontColor)
                typeface(Typeface.NORMAL)
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_small)
            })
            .toSpannable()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        binding.progressCircular.visibility = if (displayProgress) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}
