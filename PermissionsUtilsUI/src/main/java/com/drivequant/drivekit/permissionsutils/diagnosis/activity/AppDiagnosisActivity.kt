package com.drivequant.drivekit.permissionsutils.diagnosis.activity


import kotlinx.android.synthetic.main.activity_app_diagnosis.*
import com.drivequant.drivekit.permissionsutils.permissions.activity.RequestPermissionActivity
import com.drivequant.drivekit.permissionsutils.R
import android.os.Bundle
import android.util.TypedValue
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import android.Manifest
import android.content.DialogInterface

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.permissionsutils.commons.views.DiagnosisItemView

import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_STORAGE_PERMISSIONS_RATIONALE
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionStatus
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionType
import com.drivequant.drivekit.permissionsutils.diagnosis.model.SensorType
import com.drivequant.drivekit.permissionsutils.permissions.receiver.SensorsReceiver

class AppDiagnosisActivity : RequestPermissionActivity() {

    private var sensorsReceiver: SensorsReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_diagnosis)
        setStyle()
        init()

    }

    private fun setStyle() {
        text_view_summary_title.headLine1()
        text_view_summary_description.normalText()
        summary_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        diag_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        battery_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        support_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())

        text_view_battery_title.headLine1()
        text_view_battery_description_1.normalText()
        text_view_battery_description_2.normalText()
        text_view_battery_description_3.normalText()

        button_help_report.button()
        text_view_help_title.headLine1()
        text_view_help_description.normalText()

        switch_enable_logging.setTextColor(DriveKitUI.colors.mainFontColor())
        switch_enable_logging.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(
                com.drivequant.drivekit.common.ui.R.dimen.dk_text_medium
            )
        )
        text_view_logging_description.normalText()
    }

    private fun requestPermission(permissionType: PermissionType) {
        when (permissionType) {
            PermissionType.LOCATION -> requestLocationPermission()
            PermissionType.ACTIVITY -> requestActivityPermission()
            PermissionType.EXTERNAL_STORAGE -> print("//TODO")
            PermissionType.NOTIFICATION -> {
                val notificationIntent = Intent()
                notificationIntent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                notificationIntent.putExtra("app_package", packageName)
                notificationIntent.putExtra("app_uid", applicationInfo.uid)
                notificationIntent.putExtra(
                    "android.provider.extra.APP_PACKAGE",
                    packageName
                )
                startActivity(notificationIntent)
            }
        }
    }

    private fun init() {
        //TODO  implement hasError() method
        if (false) {
            image_view_summary_icon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_checked
                )
            )
            text_view_summary_title.text = getString(R.string.dk_perm_utils_diag_app_ok)
            text_view_summary_description.text = getString(R.string.dk_perm_utils_diag_app_ok_text)
        } else {
            image_view_summary_icon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_high_priority
                )
            )
            text_view_summary_title.text = getString(R.string.dk_perm_utils_app_diag_app_ko_01)
            text_view_summary_description.text =
                getString(R.string.dk_perm_utils_app_diag_app_ko_text)
        }
    }

    private fun setNormalState(diagnosticItem: DiagnosisItemView) {
        diagnosticItem.setDiagnosisDrawable(false)
        diagnosticItem.setOnClickListener {
            val infoDiagnosis = DKAlertDialog.LayoutBuilder()
                .init(this)
                .layout(R.layout.template_alert_dialog_layout)
                .cancelable(false)
                .positiveButton(getString(R.string.dk_common_ok),
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    }).show()

            val titleTextView = infoDiagnosis.findViewById<TextView>(R.id.text_view_alert_title)
            val descriptionTextView =
                infoDiagnosis.findViewById<TextView>(R.id.text_view_alert_description)

            titleTextView?.text = diagnosticItem.getDiagnosisTitle()
            descriptionTextView?.text = diagnosticItem.getDiagnosticTextOK()
            titleTextView?.headLine1()
            descriptionTextView?.normalText()
        }
    }

    private fun requestActivityPermission() {
        permissionCallback = object :
            OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {

            }

            override fun onPermissionDeclined(permissionName: Array<String>) {
                handlePermissionDeclined(
                    this@AppDiagnosisActivity, R.string.dk_perm_utils_app_diag_location_ko_android,
                    this@AppDiagnosisActivity::requestActivityPermission
                )
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                handlePermissionTotallyDeclined(
                    this@AppDiagnosisActivity,
                    R.string.dk_perm_utils_app_diag_location_ko_android
                )
            }
        }
        request(
            this,
            permissionCallback as OnPermissionCallback,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

    private fun requestLocationPermission() {
        permissionCallback = object :
            OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {

            }

            override fun onPermissionDeclined(permissionName: Array<String>) {
                handlePermissionDeclined(
                    this@AppDiagnosisActivity, R.string.dk_perm_utils_app_diag_location_ko_android,
                    this@AppDiagnosisActivity::requestLocationPermission
                )
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                handlePermissionTotallyDeclined(
                    this@AppDiagnosisActivity,
                    R.string.dk_perm_utils_app_diag_location_ko_android
                )
            }
        }

        if (DiagnosisHelper.hasFineLocationPermission(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!DiagnosisHelper.hasBackgroundLocationApproved(this)) {
                    request(
                        this,
                        permissionCallback as OnPermissionCallback,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                request(
                    this,
                    permissionCallback as OnPermissionCallback,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            } else {
                request(
                    this,
                    permissionCallback as OnPermissionCallback,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    // Mail method

    private fun buildContentMail() {

        //TODO format data
        // send log file with the mail
    }

    private fun refresh() {
            val sensors = arrayListOf(SensorType.BLUETOOTH, SensorType.GPS)
            val permissions = arrayListOf(
                PermissionType.LOCATION,
                PermissionType.ACTIVITY,
                PermissionType.EXTERNAL_STORAGE,
                PermissionType.NOTIFICATION
            )

            sensors.forEach { sensorType ->
                when (sensorType) {
                    SensorType.GPS -> {
                        if (DiagnosisHelper.isSensorActivated(this, sensorType)) {
                            setNormalState(diag_item_location_sensor)
                        } else {
                            diag_item_location_sensor.setDiagnosisDrawable(true)
                            diag_item_location_sensor.setOnClickListener {
                                alertDialog = DKAlertDialog.LayoutBuilder()
                                    .init(this)
                                    .layout(R.layout.template_alert_dialog_layout)
                                    .positiveButton(diag_item_location_sensor.getDiagnosisLink(),
                                        DialogInterface.OnClickListener { _, _ ->
                                            enableSensor(sensorType)
                                        })
                                    .show()

                                val titleTextView =
                                    alertDialog?.findViewById<TextView>(R.id.text_view_alert_title)
                                val descriptionTextView =
                                    alertDialog?.findViewById<TextView>(R.id.text_view_alert_description)
                                titleTextView?.text = diag_item_location_sensor.getDiagnosisTitle()
                                descriptionTextView?.text =
                                    diag_item_location_sensor.getDiagnosticTextKO()
                                titleTextView?.headLine1()
                                descriptionTextView?.normalText()
                            }
                        }
                    }

                    SensorType.BLUETOOTH -> {
                        if (DiagnosisHelper.isSensorActivated(this, sensorType)) {
                            setNormalState(diag_item_bluetooth)
                        } else {
                            diag_item_bluetooth.setDiagnosisDrawable(true)
                            diag_item_bluetooth.setOnClickListener {
                                alertDialog = DKAlertDialog.LayoutBuilder()
                                    .init(this)
                                    .layout(R.layout.template_alert_dialog_layout)
                                    .positiveButton(diag_item_bluetooth.getDiagnosisLink(),
                                        DialogInterface.OnClickListener { _, _ ->
                                            //TODO enableSensor() method
                                        })
                                    .show()

                                val titleTextView =
                                    alertDialog?.findViewById<TextView>(R.id.text_view_alert_title)
                                val descriptionTextView =
                                    alertDialog?.findViewById<TextView>(R.id.text_view_alert_description)
                                titleTextView?.text = diag_item_bluetooth.getDiagnosisTitle()
                                descriptionTextView?.text =
                                    diag_item_bluetooth.getDiagnosticTextKO()
                            }
                        }
                    }

                    //TODO develop permission section
            }
        }
    }

    private fun enableSensor(sensorType: SensorType) {
        //TODO implement enableSensor method
    }

    private fun registerReceiver() {
        sensorsReceiver = object : SensorsReceiver() {
            override fun getBluetoothState(bluetoothState: Boolean) {
                //If we want to check bluetooth state individually
            }

            override fun getConnectivityState(connectivityState: Boolean) {
                //If we want to check connectivity state individually
            }

            override fun getGpsState(gpsState: Boolean) {
                //If we want to check gps state individually
            }

            override fun onSensorBroadcastReceived() {
                refresh()
            }
        }

        val gpsFilter = IntentFilter(SensorsReceiver.LOCATION_INTENT_ACTION)
        registerReceiver(sensorsReceiver, gpsFilter)

        val bluetoothFilter = IntentFilter(SensorsReceiver.BLUETOOTH_INTENT_ACTION)
        registerReceiver(sensorsReceiver, bluetoothFilter)

        val connectivityFilter = IntentFilter(SensorsReceiver.CONNECTIVITY_INTENT_ACTION)
        registerReceiver(sensorsReceiver, connectivityFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(sensorsReceiver)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PERMISSIONS_OPEN_SETTINGS -> alertDialog?.dismiss()
            REQUEST_STORAGE_PERMISSIONS_RATIONALE -> {
                if (DiagnosisHelper.getPermissionStatus(
                        this,
                        PermissionType.EXTERNAL_STORAGE
                    ) == PermissionStatus.VALID
                ) {
                    //TODO
                }
            }
        }
    }
}
