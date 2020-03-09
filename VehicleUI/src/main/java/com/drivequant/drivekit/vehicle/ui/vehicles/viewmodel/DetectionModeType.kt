package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicleManager
import com.drivequant.drivekit.vehicle.manager.DetectionModeStatus
import com.drivequant.drivekit.vehicle.manager.DetectionModeStatus.*
import com.drivequant.drivekit.vehicle.manager.VehicleUpdateDetectionModeQueryListener
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.commons.ResourceUtils
import com.drivequant.drivekit.vehicle.ui.vehicles.adapter.VehiclesListAdapter

enum class DetectionModeType(
    private val title: String,
    private val descriptionConfigured: String,
    private val descriptionNotConfigured: String,
    private val configureButtonText: String
) {
    DISABLED(
        "dk_detection_mode_disabled_title",
        "dk_detection_mode_disabled_desc",
        "",
        ""),
    GPS("dk_detection_mode_gps_title",
        "dk_detection_mode_gps_desc",
        "",
        ""),
    BEACON("dk_detection_mode_beacon_title",
        "dk_detection_mode_beacon_desc_configured",
        "dk_detection_mode_beacon_desc_not_configured",
        "dk_vehicle_configure_beacon"),
    BLUETOOTH("dk_detection_mode_bluetooth_title",
        "dk_detection_mode_bluetooth_desc_configured",
        "dk_detection_mode_bluetooth_desc_not_configured",
        "dk_vehicle_configure_bluetooth");

    companion object {
        fun getEnumByDetectionMode(detectionMode: DetectionMode): DetectionModeType {
            for (x in values()) {
                if (x.name == detectionMode.name) return x
            }
            throw IllegalArgumentException("Value ${detectionMode.name} not found in DetectionModeType list")
        }
    }

    fun getTitle(context: Context): String {
       return ResourceUtils.convertToString(context, this.title)?.let { it } ?: run { "" }
    }

    fun getDescription(context: Context, vehicle: Vehicle): String {
        val stringRes = when (this){
            DISABLED, GPS -> {
                descriptionConfigured
            }
            BEACON -> {
                vehicle.beacon?.let {
                    descriptionConfigured // TODO it.code
                } ?: run {
                    descriptionNotConfigured
                }
            }
            BLUETOOTH -> {
                vehicle.bluetooth?.let {
                    descriptionConfigured // TODO it.name
                } ?: run {
                    descriptionNotConfigured
                }
            }
        }
        return ResourceUtils.convertToString(context, stringRes)?.let { it } ?: run { "" }
    }

    fun getDescriptionSpannable(context: Context, vehicle: Vehicle): SpannableString {
        var param = ""
        val stringRes = when (this){
            DISABLED, GPS -> {
                descriptionConfigured
            }
            BEACON -> {
                vehicle.beacon?.let {
                    param = it.code
                    descriptionConfigured
                } ?: run {
                    descriptionNotConfigured
                }
            }
            BLUETOOTH -> {
                vehicle.bluetooth?.let {
                    it.name?.let { btName ->
                        param = btName
                    }
                    descriptionConfigured

                } ?: run {
                    descriptionNotConfigured
                }
            }
        }
        val spannableString = SpannableString(ResourceUtils.convertToString(context, stringRes)?.let { it } ?: run { "" })
        val quantityStartIndex = stringRes.indexOf("%s")
        //val quantityEndIndex = stringRes.indexOf("%s")
        return spannableString
    }

    fun getConfigureButtonText(context: Context): String {
        return when (this){
            BEACON, BLUETOOTH -> ResourceUtils.convertToString(context, configureButtonText)?.let { it } ?: run { "" }
            else -> ""
        }
    }

    fun onConfigureButtonClicked(context: Context, vehicle: Vehicle){
        Toast.makeText(context, vehicle.name, Toast.LENGTH_SHORT).show()
    }

    fun detectionModeSelected(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle){
        // TODO check connectivity
        // check if GPS already set
        // check if beacon configured to another
        // check if bluetooth configured to another

        val detectionMode = DetectionMode.getEnumByName(this.name)
        DriveKitVehicleManager.updateDetectionMode(vehicle, detectionMode, object: VehicleUpdateDetectionModeQueryListener {
            override fun onResponse(status: DetectionModeStatus) {
                when (status){
                    SUCCESS -> viewModel.fetchVehicles(SynchronizationType.CACHE)
                    ERROR -> { }
                    GPS_MODE_ALREADY_EXISTS -> {}
                }
            }
        })
    }
}

