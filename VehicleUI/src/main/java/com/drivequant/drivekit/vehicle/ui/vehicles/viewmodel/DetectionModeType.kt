package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.content.Context
import android.view.View
import android.widget.TextView
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicleManager
import com.drivequant.drivekit.vehicle.manager.DetectionModeStatus
import com.drivequant.drivekit.vehicle.manager.DetectionModeStatus.*
import com.drivequant.drivekit.vehicle.manager.VehicleUpdateDetectionModeQueryListener
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleBeaconStatus
import com.drivequant.drivekit.vehicle.manager.beacon.VehicleRemoveBeaconQueryListener
import com.drivequant.drivekit.vehicle.manager.bluetooth.VehicleBluetoothStatus
import com.drivequant.drivekit.vehicle.manager.bluetooth.VehicleRemoveBluetoothQueryListener
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.picker.commons.ResourceUtils

enum class DetectionModeType(
    private val title: String,
    private val descriptionConfigured: String,
    private val descriptionNotConfigured: String,
    private val configureButtonText: String,
    private val configureDescText: String
) {
    DISABLED(
        "dk_detection_mode_disabled_title",
        "dk_detection_mode_disabled_desc",
        "",
        "",
        ""),
    GPS("dk_detection_mode_gps_title",
        "dk_detection_mode_gps_desc",
        "",
        "",
        ""),
    BEACON("dk_detection_mode_beacon_title",
        "dk_detection_mode_beacon_desc_configured",
        "dk_detection_mode_beacon_desc_not_configured",
        "dk_vehicle_configure_beacon_title",
        "dk_vehicle_configure_beacon_desc"),
    BLUETOOTH("dk_detection_mode_bluetooth_title",
        "dk_detection_mode_bluetooth_desc_configured",
        "dk_detection_mode_bluetooth_desc_not_configured",
        "dk_vehicle_configure_bluetooth_title",
        "dk_vehicle_configure_bluetooth_desc");

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

    fun getConfigureButtonText(context: Context): String {
        return when (this){
            BEACON, BLUETOOTH -> ResourceUtils.convertToString(context, configureButtonText)?.let { it } ?: run { "" }
            else -> ""
        }
    }

    fun onConfigureButtonClicked(context: Context, viewModel : VehiclesListViewModel, vehicle: Vehicle){
        val alert = DKAlertDialog.LayoutBuilder()
            .init(context)
            .layout(R.layout.alert_dialog_beacon_bluetooth_chooser)
            .cancelable(true)
            .show()

        val title = alert.findViewById<TextView>(R.id.alert_dialog_header)
        val description = alert.findViewById<TextView>(R.id.alert_dialog_description)
        val verify = alert.findViewById<TextView>(R.id.text_view_verify)
        val replace = alert.findViewById<TextView>(R.id.text_view_replace)
        val delete= alert.findViewById<TextView>(R.id.text_view_delete)

        title?.text = DKResource.convertToString(context, configureButtonText)
        description?.text = DKResource.convertToString(context, configureDescText)

        when (this){
            BEACON -> {
                // TODO check from Singleton if these actions are displayable
                verify?.let {
                    it.visibility = View.VISIBLE
                    it.text = DKResource.convertToString(context, "dk_vehicle_delete")
                    it.setOnClickListener {
                        // TODO launch beacon screen
                    }
                }
                replace?.setOnClickListener {
                    it.visibility = View.VISIBLE
                    it.setOnClickListener {
                        // TODO launch beacon screen
                    }
                }

                delete?.setOnClickListener {
                    it.visibility = View.VISIBLE
                    DriveKitVehicleManager.removeBeaconToVehicle(vehicle, object: VehicleRemoveBeaconQueryListener {
                        override fun onResponse(status: VehicleBeaconStatus) {
                            alert.dismiss()
                            viewModel.fetchVehicles(SynchronizationType.CACHE)
                        }
                    })
                }
            }
            BLUETOOTH -> {
                delete?.let {
                    it.visibility = View.VISIBLE
                    it.text = DKResource.convertToString(context, "dk_vehicle_delete")
                    it.setOnClickListener {
                        DriveKitVehicleManager.removeBluetoothToVehicle(vehicle, object: VehicleRemoveBluetoothQueryListener {
                            override fun onResponse(status: VehicleBluetoothStatus) {
                                alert.dismiss()
                                viewModel.fetchVehicles(SynchronizationType.CACHE)
                            }
                        })
                    }
                }
            }
            else -> {}
        }
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

