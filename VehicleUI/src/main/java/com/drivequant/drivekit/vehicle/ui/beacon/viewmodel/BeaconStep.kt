package com.drivequant.drivekit.vehicle.ui.beacon.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.children.BeaconScannerAlreadyPairedFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.children.BeaconScannerBeaconNotConfiguredFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.children.BeaconScannerBeaconUnavailableFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.children.BeaconScannerCongratsFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.children.BeaconScannerInfoFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.children.BeaconScannerNotFoundFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.children.BeaconScannerProgressFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.children.BeaconScannerSuccessFragment

enum class BeaconStep {
    INITIAL,
    SCAN,
    SUCCESS,
    BEACON_NOT_FOUND,
    BEACON_ALREADY_PAIRED,
    CONGRATS,
    BEACON_UNAVAILABLE,
    VERIFIED,
    WRONG_BEACON,
    BEACON_NOT_CONFIGURED;

    fun getTitle(context: Context, viewModel: BeaconViewModel): Spannable {
        val beaconCode = viewModel.beacon?.code ?: ""
        val vehicleName = viewModel.vehicleName ?: ""
        val mainFontColor = DKColors.mainFontColor

        return when (this) {
            INITIAL -> DKResource.buildString(context,
                textColor = mainFontColor,
                identifier = R.string.dk_vehicle_beacon_start_scan)
            SCAN -> DKResource.buildString(context,
                textColor = mainFontColor,
                identifier = R.string.dk_vehicle_beacon_setup_scan_title)
            SUCCESS -> DKResource.buildString(context,
                textColor = mainFontColor,
                identifier = R.string.dk_vehicle_beacon_setup_code_success_message)
            BEACON_NOT_FOUND -> {
                if (viewModel.scanType == BeaconScanType.DIAGNOSTIC) {
                    DKResource.buildString(context,
                        textColor = mainFontColor,
                        highlightColor = mainFontColor,
                        identifier = R.string.dk_beacon_not_seen,
                        beaconCode)
                } else {
                    DKResource.buildString(context,
                        textColor = mainFontColor,
                        highlightColor = mainFontColor,
                        identifier = R.string.dk_vehicle_beacon_setup_code_not_matched,
                        beaconCode)
                }
            }
            BEACON_ALREADY_PAIRED -> DKResource.buildString(context,
                textColor = DKColors.mainFontColor,
                identifier = R.string.dk_vehicle_beacon_already_paired)
            CONGRATS -> DKResource.buildString(
                context,
                mainFontColor,
                mainFontColor,
                R.string.dk_vehicle_beacon_setup_successlink_message,
                beaconCode,
                vehicleName
            )
            BEACON_UNAVAILABLE -> DKResource.buildString(context, mainFontColor, mainFontColor, R.string.dk_vehicle_beacon_setup_code_unavailable_id, beaconCode)
            VERIFIED -> DKResource.buildString(context, mainFontColor, R.string.dk_vehicle_beacon_setup_code_success_message)
            WRONG_BEACON -> DKResource.buildString(context, mainFontColor, R.string.dk_vehicle_beacon_verify_wrong_vehicle)
            BEACON_NOT_CONFIGURED -> DKResource.buildString(context, mainFontColor, R.string.dk_vehicle_beacon_diagnostic_alert)
        }
    }

    fun getImage(context: Context): Drawable? {
        val identifier = when (this) {
            INITIAL -> R.drawable.dk_beacon_start
            SCAN -> R.drawable.dk_beacon_scan_running
            SUCCESS, VERIFIED -> R.drawable.dk_beacon_ok
            BEACON_NOT_FOUND, BEACON_ALREADY_PAIRED, BEACON_UNAVAILABLE,
                BEACON_NOT_CONFIGURED, WRONG_BEACON -> R.drawable.dk_beacon_not_found
            CONGRATS -> R.drawable.dk_vehicle_congrats
        }
        return ContextCompat.getDrawable(context, identifier)
    }

    fun getChildFragment(viewModel: BeaconViewModel): Fragment? {
        return when (this){
            INITIAL -> null
            SCAN -> BeaconScannerProgressFragment.newInstance(viewModel)
            SUCCESS -> BeaconScannerSuccessFragment.newInstance(viewModel)
            BEACON_NOT_FOUND -> BeaconScannerNotFoundFragment.newInstance(viewModel)
            BEACON_ALREADY_PAIRED -> BeaconScannerAlreadyPairedFragment.newInstance(viewModel)
            CONGRATS -> BeaconScannerCongratsFragment.newInstance(viewModel)
            BEACON_UNAVAILABLE -> BeaconScannerBeaconUnavailableFragment.newInstance(viewModel)
            BEACON_NOT_CONFIGURED -> BeaconScannerBeaconNotConfiguredFragment.newInstance(viewModel)
            VERIFIED -> BeaconScannerInfoFragment.newInstance(viewModel, isValid = true)
            WRONG_BEACON -> BeaconScannerInfoFragment.newInstance(viewModel, isValid = false)
        }
    }

    fun onImageClicked(viewModel: BeaconViewModel) {
        when (this) {
            INITIAL, BEACON_NOT_FOUND -> viewModel.updateScanState(SCAN)
            else -> { }
        }
    }
}
