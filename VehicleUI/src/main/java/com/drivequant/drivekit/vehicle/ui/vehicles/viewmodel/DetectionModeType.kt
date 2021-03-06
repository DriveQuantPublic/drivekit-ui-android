package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.DetectionModeStatus
import com.drivequant.drivekit.vehicle.manager.DetectionModeStatus.*
import com.drivequant.drivekit.vehicle.manager.VehicleUpdateDetectionModeQueryListener
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.activity.BeaconActivity
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconScanType
import com.drivequant.drivekit.vehicle.ui.bluetooth.activity.BluetoothActivity
import com.drivequant.drivekit.vehicle.ui.extension.buildFormattedName
import com.drivequant.drivekit.vehicle.ui.extension.getDeviceDisplayIdentifier
import com.drivequant.drivekit.vehicle.ui.extension.isConfigured

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
       return DKResource.convertToString(context, this.title)
    }

    fun getDescription(context: Context, vehicle: Vehicle): SpannableString {
        var configured = true
        val parameter = vehicle.getDeviceDisplayIdentifier()
        val stringIdentifier = when (this){
            DISABLED, GPS -> {
                descriptionConfigured
            }
            BEACON, BLUETOOTH -> {
                if (vehicle.isConfigured()){
                    descriptionConfigured
                } else {
                    configured = false
                    descriptionNotConfigured
                }
            }
        }
        val parameteredString = DKResource.buildString(context,  DriveKitUI.colors.mainFontColor(),
            DriveKitUI.colors.mainFontColor(),stringIdentifier, parameter)

        return if (configured){
            DKSpannable().append(parameteredString, context.resSpans {
                color(DriveKitUI.colors.mainFontColor())
                typeface(Typeface.NORMAL)
                size(R.dimen.dk_text_normal)
            }).toSpannable()
        } else {
            DKSpannable().append(parameteredString, context.resSpans {
                color(DriveKitUI.colors.criticalColor())
                typeface(BOLD)
                size(R.dimen.dk_text_normal)
            }).toSpannable()
        }
    }

    fun getConfigureButtonText(context: Context): String {
        return when (this){
            BEACON, BLUETOOTH -> DKResource.convertToString(context, configureButtonText)
            else -> ""
        }
    }

    fun onConfigureButtonClicked(context: Context, viewModel : VehiclesListViewModel, vehicle: Vehicle){
        if (vehicle.isConfigured()){
            displayConfigAlertDialog(context, viewModel, vehicle)
        } else {
            val vehicleName = vehicle.buildFormattedName(context)
            when (this){
                BEACON -> BeaconActivity.launchActivity(context, BeaconScanType.PAIRING, vehicle.vehicleId)
                BLUETOOTH -> BluetoothActivity.launchActivity(context, vehicle.vehicleId, vehicleName)
                else -> { }
            }
        }
    }

    fun detectionModeSelected(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle){
        val detectionMode = DetectionMode.getEnumByName(this.name)
        updateDetectionMode(context, detectionMode, viewModel, vehicle)
    }

    private fun updateDetectionMode(context: Context, detectionMode: DetectionMode, viewModel: VehiclesListViewModel, vehicle: Vehicle, forceGPSVehicleUpdate: Boolean = false){
        viewModel.progressBarObserver.postValue(true)
        DriveKitVehicle.updateDetectionMode(vehicle, detectionMode, object: VehicleUpdateDetectionModeQueryListener {
            override fun onResponse(status: DetectionModeStatus) {
                viewModel.progressBarObserver.postValue(false)
                when (status){
                    SUCCESS -> viewModel.fetchVehicles(context)
                    ERROR -> manageError(context, viewModel)
                    GPS_MODE_ALREADY_EXISTS -> manageGPSModeAlreadyExists(context, viewModel, detectionMode, vehicle)
                }
            }
        }, forceGPSVehicleUpdate)
    }

    private fun manageError(context: Context, viewModel: VehiclesListViewModel){
        Toast.makeText(context, DKResource.convertToString(context, "dk_vehicle_error_message"), Toast.LENGTH_SHORT).show()
        viewModel.fetchVehicles(context, SynchronizationType.CACHE)
    }

    private fun manageGPSModeAlreadyExists(context: Context, viewModel: VehiclesListViewModel, detectionMode: DetectionMode, vehicle: Vehicle) {
        val gpsVehicle = viewModel.vehiclesList.first { it.detectionMode == DetectionMode.GPS }
        val title = DKResource.convertToString(context, "app_name")
        val message = DKResource.buildString(context,
            DriveKitUI.colors.mainFontColor(),
            DriveKitUI.colors.mainFontColor(),"dk_vehicle_gps_already_exists_confirm",
            getEnumByDetectionMode(detectionMode).getTitle(context),
            vehicle.buildFormattedName(context),
            gpsVehicle.buildFormattedName(context),
            getEnumByDetectionMode(DetectionMode.DISABLED).getTitle(context)
        )

        val alert = DKAlertDialog.LayoutBuilder().init(context)
            .layout(R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton(context.getString(R.string.dk_common_confirm),
                DialogInterface.OnClickListener { _, _ ->
                    updateDetectionMode(context, detectionMode, viewModel, vehicle, true)
                })
            .negativeButton(negativeListener =
                DialogInterface.OnClickListener { dialog, _ ->
                    viewModel.fetchVehicles(context)
                    dialog.dismiss()
                })
            .show()

        val titleTextView = alert.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView = alert.findViewById<TextView>(R.id.text_view_alert_description)

        titleTextView?.text = title
        descriptionTextView?.text = message
        titleTextView?.headLine1()
        descriptionTextView?.normalText()
    }

    private fun displayConfigAlertDialog(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle){
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
        val separatorDescription = alert.findViewById<View>(R.id.view_separator_description)
        val separatorVerify = alert.findViewById<View>(R.id.view_separator_verify)
        val separatorReplace = alert.findViewById<View>(R.id.view_separator_replace)

        val primaryColor = DriveKitUI.colors.primaryColor()
        val neutralColor = DriveKitUI.colors.neutralColor()
        title?.text = DKResource.convertToString(context, configureButtonText)
        title?.normalText(DriveKitUI.colors.fontColorOnPrimaryColor())
        title?.setBackgroundColor(primaryColor)

        description?.text = DKResource.convertToString(context, configureDescText)
        description?.normalText()

        verify?.headLine2(primaryColor)
        replace?.headLine2(primaryColor)
        delete?.headLine2(primaryColor)

        separatorDescription?.setBackgroundColor(neutralColor)
        separatorVerify?.setBackgroundColor(neutralColor)
        separatorReplace?.setBackgroundColor(neutralColor)

        val vehicleName = viewModel.getTitle(context, vehicle)

        when (this){
            BEACON -> {
                description?.text = DKResource.buildString(
                    context, DriveKitUI.colors.mainFontColor(),
                    DriveKitUI.colors.mainFontColor(), configureDescText, vehicleName
                )

                verify?.let {
                    it.visibility = View.VISIBLE
                    it.text = DKResource.convertToString(context, "dk_beacon_verify")
                    it.setOnClickListener {
                        vehicle.beacon?.let { beacon ->
                            alert.dismiss()
                            BeaconActivity.launchActivity(context, BeaconScanType.VERIFY, vehicle.vehicleId, beacon)
                        }
                    }
                }
                replace?.let {
                    it.visibility = View.VISIBLE
                    it.text = DKResource.convertToString(context, "dk_vehicle_replace")
                    it.setOnClickListener {
                        vehicle.beacon?.let { beacon ->
                            alert.dismiss()
                            BeaconActivity.launchActivity(context, BeaconScanType.PAIRING, vehicle.vehicleId, beacon)
                        }
                    }
                }
                delete?.let {
                    if (DriveKitVehicleUI.canRemoveBeacon) {
                        it.visibility = View.VISIBLE
                        it.text = DKResource.convertToString(context, "dk_vehicle_delete")
                        it.setOnClickListener {
                            alert.dismiss()
                            viewModel.removeBeaconToVehicle(vehicle)
                        }
                    } else {
                        it.visibility = View.GONE
                    }
                }
            }
            BLUETOOTH -> {
                separatorVerify?.visibility = View.GONE
                description?.text = DKResource.buildString(
                    context, DriveKitUI.colors.mainFontColor(),
                    DriveKitUI.colors.mainFontColor(), configureDescText, vehicleName
                )

                delete?.let {
                    it.visibility = View.VISIBLE
                    it.text = DKResource.convertToString(context, "dk_vehicle_delete")
                    it.setOnClickListener {
                        alert.dismiss()
                        viewModel.removeBluetoothToVehicle(vehicle)
                    }
                }
            }
            else -> { }
        }
    }
}

