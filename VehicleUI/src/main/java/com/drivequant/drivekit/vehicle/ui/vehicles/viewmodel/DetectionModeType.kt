package com.drivequant.drivekit.vehicle.ui.vehicles.viewmodel

import android.content.Context
import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.normalTextWithColor
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.DetectionModeStatus
import com.drivequant.drivekit.vehicle.manager.DetectionModeStatus.ERROR
import com.drivequant.drivekit.vehicle.manager.DetectionModeStatus.GPS_MODE_ALREADY_EXISTS
import com.drivequant.drivekit.vehicle.manager.DetectionModeStatus.SUCCESS
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
    @StringRes private val titleResId: Int,
    @StringRes private val descriptionConfiguredResId: Int,
    @StringRes private val descriptionNotConfigured: Int?,
    @StringRes private val configureButtonText: Int?,
    @StringRes private val configureDescText: Int?
) {
    DISABLED(
        R.string.dk_detection_mode_disabled_title,
        R.string.dk_detection_mode_disabled_desc,
        null,
        null,
        null),
    GPS(R.string.dk_detection_mode_gps_title,
            R.string.dk_detection_mode_gps_desc,
        null,
        null,
        null),
    BEACON(R.string.dk_detection_mode_beacon_title,
        R.string.dk_detection_mode_beacon_desc_configured,
        R.string.dk_detection_mode_beacon_desc_not_configured,
        R.string.dk_vehicle_configure_beacon_title,
        R.string.dk_vehicle_configure_beacon_desc),
    BLUETOOTH(R.string.dk_detection_mode_bluetooth_title,
        R.string.dk_detection_mode_bluetooth_desc_configured,
        R.string.dk_detection_mode_bluetooth_desc_not_configured,
        R.string.dk_vehicle_configure_bluetooth_title,
        R.string.dk_vehicle_configure_bluetooth_desc);

    companion object {
        fun getEnumByDetectionMode(detectionMode: DetectionMode): DetectionModeType {
            for (x in values()) {
                if (x.name == detectionMode.name) return x
            }
            throw IllegalArgumentException("Value ${detectionMode.name} not found in DetectionModeType list")
        }
    }

    fun getTitle(context: Context): String {
       return context.getString(this.titleResId)
    }

    fun getDescription(context: Context, vehicle: Vehicle): SpannableString {
        var configured = true
        val parameter = vehicle.getDeviceDisplayIdentifier()
        val stringIdentifier = when (this) {
            DISABLED, GPS -> {
                descriptionConfiguredResId
            }
            BEACON, BLUETOOTH -> {
                if (vehicle.isConfigured()) {
                    descriptionConfiguredResId
                } else {
                    configured = false
                    descriptionNotConfigured
                }
            }
        }
        val parameteredString = stringIdentifier?.let {
            DKResource.buildString(context, DKColors.mainFontColor, DKColors.mainFontColor, it, parameter)
        } ?: ""

        return if (configured) {
            DKSpannable().append(parameteredString, context.resSpans {
                color(DKColors.complementaryFontColor)
                typeface(Typeface.NORMAL)
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_normal)
            }).toSpannable()
        } else {
            DKSpannable().append(parameteredString, context.resSpans {
                color(DKColors.criticalColor)
                typeface(BOLD)
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_normal)
            }).toSpannable()
        }
    }

    fun getConfigureButtonText(context: Context): String {
        return when (this) {
            BEACON, BLUETOOTH -> configureButtonText?.let { context.getString(configureButtonText) } ?: ""
            else -> ""
        }
    }

    fun onConfigureButtonClicked(context: Context, viewModel : VehiclesListViewModel, vehicle: Vehicle) {
        if (vehicle.isConfigured()){
            displayConfigAlertDialog(context, viewModel, vehicle)
        } else {
            val vehicleName = vehicle.buildFormattedName(context)
            when (this) {
                BEACON -> BeaconActivity.launchActivity(context, BeaconScanType.PAIRING, vehicle.vehicleId)
                BLUETOOTH -> BluetoothActivity.launchActivity(context, vehicle.vehicleId, vehicleName)
                else -> { }
            }
        }
    }

    fun detectionModeSelected(context: Context, viewModel: VehiclesListViewModel, vehicle: Vehicle){
        val detectionMode = DetectionMode.valueOf(this.name)
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

    private fun manageError(context: Context, viewModel: VehiclesListViewModel) {
        Toast.makeText(context, R.string.dk_vehicle_error_message, Toast.LENGTH_SHORT).show()
        viewModel.fetchVehicles(context, SynchronizationType.CACHE)
    }

    private fun manageGPSModeAlreadyExists(context: Context, viewModel: VehiclesListViewModel, detectionMode: DetectionMode, vehicle: Vehicle) {
        val gpsVehicle = viewModel.vehiclesList.first { it.detectionMode == DetectionMode.GPS }
        val title = context.getString(R.string.app_name)
        val message = DKResource.buildString(context,
            DKColors.mainFontColor,
            DKColors.mainFontColor,
            R.string.dk_vehicle_gps_already_exists_confirm,
            getEnumByDetectionMode(detectionMode).getTitle(context),
            vehicle.buildFormattedName(context),
            gpsVehicle.buildFormattedName(context),
            getEnumByDetectionMode(DetectionMode.DISABLED).getTitle(context)
        )

        val alert = DKAlertDialog.LayoutBuilder().init(context)
            .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
            .cancelable(false)
            .positiveButton(context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_confirm)) { _, _ ->
                updateDetectionMode(context, detectionMode, viewModel, vehicle, true)
            }
            .negativeButton() { dialog, _ ->
                viewModel.fetchVehicles(context)
                dialog.dismiss()
            }
            .show()

        val titleTextView = alert.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView = alert.findViewById<TextView>(R.id.text_view_alert_description)

        titleTextView?.text = title
        descriptionTextView?.text = message
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
        val delete = alert.findViewById<TextView>(R.id.text_view_delete)
        val separatorVerify = alert.findViewById<View>(R.id.view_separator_verify)

        title?.run {
            if (configureButtonText != null) {
                setText(configureButtonText)
            } else {
                text = ""
            }
            normalText()
        }

        description?.run {
            if (configureDescText != null) {
                setText(configureDescText)
            } else {
                text = ""
            }
            normalTextWithColor()
        }

        verify?.headLine2()
        replace?.headLine2()
        delete?.headLine2()

        val vehicleName = viewModel.getTitle(context, vehicle)

        when (this) {
            BEACON -> {
                description?.text = if (configureDescText != null) {
                    DKResource.buildString(
                        context, DKColors.mainFontColor,
                        DKColors.mainFontColor, configureDescText, vehicleName
                    )
                } else {
                    ""
                }

                verify?.let {
                    it.visibility = View.VISIBLE
                    it.setText(R.string.dk_beacon_verify)
                    it.setOnClickListener {
                        vehicle.beacon?.let { beacon ->
                            alert.dismiss()
                            BeaconActivity.launchActivity(context, BeaconScanType.VERIFY, vehicle.vehicleId, beacon)
                        }
                    }
                }
                replace?.let {
                    it.visibility = View.VISIBLE
                    it.setText(R.string.dk_vehicle_replace)
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
                        it.setText(R.string.dk_vehicle_delete)
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
                description?.text = if (configureDescText != null) {
                    DKResource.buildString(
                        context, DKColors.mainFontColor,
                        DKColors.mainFontColor, configureDescText, vehicleName
                    )
                } else {
                    ""
                }

                delete?.let {
                    it.visibility = View.VISIBLE
                    it.setText(R.string.dk_vehicle_delete)
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
