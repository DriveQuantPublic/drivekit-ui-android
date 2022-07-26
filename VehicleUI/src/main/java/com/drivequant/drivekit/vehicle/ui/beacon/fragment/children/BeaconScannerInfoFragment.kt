package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.graphics.drawable.DrawableCompat
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.beaconutils.BeaconData
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.databaseutils.entity.Beacon
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import com.drivequant.drivekit.vehicle.ui.utils.BeaconInfoScannerManager
import com.drivequant.drivekit.vehicle.ui.utils.DKBeaconRetrievedInfo
import com.drivequant.drivekit.vehicle.ui.utils.DKBeaconInfoListener
import com.drivequant.drivekit.vehicle.ui.vehicles.utils.VehicleUtils
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_info.*

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_beacon_child_scanner_info, container, false).setDKStyle()
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
                            button_beacon_info.visibility = View.VISIBLE
                            viewModel.apply {
                                this.batteryLevel = beaconRetrievedInfo.batteryLevel
                                this.estimatedDistance = beaconRetrievedInfo.estimatedDistance
                                this.rssi = beaconRetrievedInfo.rssi
                                this.txPower = beaconRetrievedInfo.txPower
                            }
                            if (isAdded) {
                                text_view_battery.text = buildBeaconCharacteristics("${beaconRetrievedInfo.batteryLevel}", "%")
                                computeBatteryDrawable(beaconRetrievedInfo.batteryLevel)?.let { drawable ->
                                    text_view_battery.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null)
                                }

                                text_view_distance.text = buildBeaconCharacteristics(beaconRetrievedInfo.estimatedDistance.format(1), "dk_common_unit_meter")
                                DKResource.convertToDrawable(requireContext(), "dk_beacon_distance")?.let { drawable ->
                                    text_view_distance.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null)
                                }

                                text_view_signal_intensity.text = buildBeaconCharacteristics("${beaconRetrievedInfo.rssi}", "dBm")
                                DKResource.convertToDrawable(requireContext(), "dk_beacon_signal_intensity")?.let { drawable ->
                                    text_view_signal_intensity.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null)
                                }
                            }
                        }
                    }
                )
                beaconBatteryScannerManager?.startBatteryReaderScanner()
            }
        }

        view_border.setBackgroundColor(DriveKitUI.colors.mainFontColor())

        text_view_connected_vehicle_name.headLine2()

        if (isValid) {
            view_border.setBackgroundColor(DriveKitUI.colors.secondaryColor())
            text_view_connected_vehicle_name.text = viewModel.vehicleName ?:run {
                DKResource.convertToString(requireContext(), "dk_beacon_vehicle_unknown")
            }
        } else {
            view_border.setBackgroundColor(DriveKitUI.colors.complementaryFontColor())
            text_view_connected_vehicle_name.text = viewModel.fetchVehicleFromSeenBeacon(VehicleUtils().fetchVehiclesOrderedByDisplayName(requireContext()))?.let { vehicle ->
                 vehicle.buildFormattedName(requireContext())
            }?: run {
                DKResource.convertToString(requireContext(), "dk_beacon_vehicle_unknown")
            }
        }

        configureInfoButton()

        view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())

        text_view_major_title.apply {
            normalText(DriveKitUI.colors.complementaryFontColor())
            text = DKResource.convertToString(requireContext(), "dk_beacon_major")
        }
        text_view_major_value.apply {
            headLine2()
            text = viewModel.seenBeacon?.major.toString()
        }
        text_view_minor_title.apply {
            normalText(DriveKitUI.colors.complementaryFontColor())
            text = DKResource.convertToString(requireContext(), "dk_beacon_minor")
        }
        text_view_minor_value.apply {
            headLine2()
            text = viewModel.seenBeacon?.minor.toString()
        }
    }

    private fun configureInfoButton() {
        button_beacon_info.setImageDrawable(DKResource.convertToDrawable(requireContext(), "dk_common_info"))
        DrawableCompat.setTint(button_beacon_info.drawable, DriveKitUI.colors.secondaryColor())
        button_beacon_info.setOnClickListener {
            viewModel.launchDetailFragment()
        }
    }

    override fun onStop() {
        super.onStop()
        beaconBatteryScannerManager?.stopBatteryReaderScanner()
    }

    private fun computeBatteryDrawable(batteryLevel: Int) = when {
        batteryLevel >= 75 -> "dk_beacon_battery_100"
        batteryLevel >= 50 -> "dk_beacon_battery_75"
        batteryLevel >= 25 -> "dk_beacon_battery_50"
        else -> "dk_beacon_battery_25"
    }.let {
        DKResource.convertToDrawable(requireContext(), it)
    }

    private fun buildBeaconCharacteristics(value: String, unitIdentifier: String) : Spannable {
        val mainFontColor = DriveKitUI.colors.mainFontColor()
        val primaryColor = DriveKitUI.colors.primaryColor()
        val unit = DKResource.convertToString(requireContext(), unitIdentifier)

        return DKSpannable()
            .append(value, requireContext().resSpans {
                color(primaryColor)
                typeface(Typeface.BOLD)
                size(R.dimen.dk_text_medium)
            })
            .append(" ")
            .append(unit, requireContext().resSpans {
                color(mainFontColor)
                typeface(Typeface.NORMAL)
                size(R.dimen.dk_text_small)
            })
            .toSpannable()
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        progress_circular?.apply {
            visibility = if (displayProgress) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}