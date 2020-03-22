package com.drivequant.drivekit.vehicle.ui.beacon.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Spannable
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.children.BeaconScannerNotFoundFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.children.BeaconScannerProgressFragment
import com.drivequant.drivekit.vehicle.ui.beacon.fragment.children.BeaconScannerSuccessFragment
import com.drivequant.drivekit.vehicle.ui.extension.computeTitle

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
    BEACON_NOT_CONFIGURED; // TODO : update diagram

    fun getTitle(context: Context, viewModel: BeaconViewModel): String {
        val beaconCode = viewModel.beacon?.code?.let { it }?: run { "" }
        val vehicleName = viewModel.vehicle?.computeTitle(context, listOf())?.let { it }?: run { "" }

        return when (this) {
            INITIAL -> DKResource.convertToString(context, "dk_vehicle_beacon_start_scan")
            SCAN -> DKResource.convertToString(context, "dk_vehicle_beacon_setup_scan_title")
            SUCCESS -> DKResource.convertToString(context, "dk_vehicle_beacon_setup_code_success_message")
            BEACON_NOT_FOUND -> DKResource.buildString(context, "dk_vehicle_beacon_setup_code_not_matched", beaconCode).toString()
            BEACON_ALREADY_PAIRED -> DKResource.convertToString(context, "dk_vehicle_beacon_already_paired")
            CONGRATS -> DKResource.buildString(context, "dk_vehicle_beacon_setup_successlink_message", beaconCode, vehicleName).toString()
            BEACON_UNAVAILABLE -> DKResource.buildString(context,"dk_vehicle_beacon_setup_code_unavailable_id", beaconCode).toString()
            VERIFIED -> DKResource.convertToString(context, "dk_vehicle_beacon_setup_code_success_message")
            WRONG_BEACON -> DKResource.convertToString(context, "dk_vehicle_beacon_verify_wrong_vehicle")
            BEACON_NOT_CONFIGURED -> DKResource.convertToString(context, "dk_vehicle_beacon_diagnostic_alert")
        }
    }

    fun getDescription(context: Context): String? {
        return when (this){
            SUCCESS -> DKResource.convertToString(context, "dk_vehicle_beacon_setup_code_success_recap")
            CONGRATS -> DKResource.convertToString(context, "dk_vehicle_beacon_setup_congrats_recap")
            else -> null
        }
    }

    fun getConfirmTextButton(context: Context): String? {
        return when (this){
            SUCCESS -> DKResource.convertToString(context, "dk_common_confirm")
            CONGRATS -> DKResource.convertToString(context, "dk_common_finish")
            else -> null
        }
    }

    fun getImage(context: Context): Drawable? {
        val identifier = when (this){
            INITIAL -> "dk_beacon_start"
            SCAN -> "dk_beacon_scan_running"
            SUCCESS, VERIFIED -> "dk_beacon_ok"
            BEACON_NOT_FOUND, BEACON_ALREADY_PAIRED, BEACON_UNAVAILABLE,
                BEACON_NOT_CONFIGURED, WRONG_BEACON -> "dk_beacon_not_found"
            CONGRATS -> "dk_vehicle_congrats"
        }
        return DKResource.convertToDrawable(context, identifier)
    }

    fun getChildFragment(viewModel: BeaconViewModel): Fragment? {
        return when (this){
            INITIAL -> null
            SCAN -> BeaconScannerProgressFragment.newInstance(viewModel)
            SUCCESS -> BeaconScannerSuccessFragment.newInstance(viewModel)
            BEACON_NOT_FOUND -> BeaconScannerNotFoundFragment.newInstance(viewModel)
            // TODO: others steps...
            else -> null
        }
    }

    fun onImageClicked(viewModel: BeaconViewModel){
        when (this){
            INITIAL, BEACON_NOT_FOUND -> viewModel.updateScanState(this)
            else -> { }
        }
    }
}