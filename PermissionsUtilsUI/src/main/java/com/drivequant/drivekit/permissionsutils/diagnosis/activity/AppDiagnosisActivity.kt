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
import android.os.Environment
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.View
import android.widget.TextView
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.permissionsutils.PermissionUtilsUI
import com.drivequant.drivekit.permissionsutils.commons.views.DiagnosisItemView
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_BATTERY_OPTIMIZATION
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_STORAGE_PERMISSIONS_RATIONALE
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionStatus
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionType
import com.drivequant.drivekit.permissionsutils.diagnosis.model.SensorType
import com.drivequant.drivekit.permissionsutils.permissions.model.ContactType
import com.drivequant.drivekit.permissionsutils.permissions.receiver.SensorsReceiver
import java.io.File
import java.util.*

class AppDiagnosisActivity : RequestPermissionActivity() {

    private var sensorsReceiver: SensorsReceiver? = null
    private var diagnosticProblemsCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_diagnosis)

        init()

        displayBatteryOptimizationSection()
        displayBluetoothItem()
        displayActivityItem()
        displayLogSection()
        displayReportSection()

        setStyle()
    }

    private fun init() {
        checkGPS()
        checkBluetooth()
        checkLocation()
        checkActivity()
        checkNotification()
        checkExternalStorage()
        checkNetwork()

        setSummaryContent()
    }

    private fun setSummaryContent() {
        if (diagnosticProblemsCount == 0) {
            image_view_summary_icon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.dk_perm_utils_checked
                )
            )
            text_view_summary_title.text = getString(R.string.dk_perm_utils_diag_app_ok)
            text_view_summary_description.text = getString(R.string.dk_perm_utils_diag_app_ok_text)
        } else {
            text_view_summary_description.text =
                getString(R.string.dk_perm_utils_app_diag_app_ko_text)
            image_view_summary_icon.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.dk_perm_utils_high_priority
                )
            )
            text_view_summary_title.text =  when (diagnosticProblemsCount) {
                1 -> getString(R.string.dk_perm_utils_app_diag_app_ko_01)
                2 -> getString(R.string.dk_perm_utils_app_diag_app_ko_02)
                3 -> getString(R.string.dk_perm_utils_app_diag_app_ko_03)
                4 -> getString(R.string.dk_perm_utils_app_diag_app_ko_04)
                5 -> getString(R.string.dk_perm_utils_app_diag_app_ko_05)
                else -> getString(R.string.dk_perm_utils_app_diag_app_ko_01)
            }
        }
        diagnosticProblemsCount = 0
    }

    private fun checkBluetooth() {
        if (DiagnosisHelper.isSensorActivated(this, SensorType.BLUETOOTH)) {
            diag_item_bluetooth.setNormalState()
        } else {
            diagnosticProblemsCount++
            diag_item_bluetooth.setDiagnosisDrawable(true)
            diag_item_bluetooth.setOnClickListener {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(this)
                    .layout(R.layout.template_alert_dialog_layout)
                    .positiveButton(diag_item_bluetooth.getDiagnosisLink(),
                        DialogInterface.OnClickListener { _, _ ->
                            enableSensor(SensorType.BLUETOOTH)
                        })
                    .show()

                val titleTextView =
                    alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                titleTextView?.text = diag_item_bluetooth.getDiagnosisTitle()
                descriptionTextView?.text = diag_item_bluetooth.getDiagnosticTextKO()
                descriptionTextView?.text =
                    diag_item_location_sensor.getDiagnosticTextKO()
                titleTextView?.headLine1()
                descriptionTextView?.normalText()
            }
        }
    }

    private fun checkGPS() {
        if (DiagnosisHelper.isSensorActivated(this, SensorType.GPS)) {
            diag_item_location_sensor.setNormalState()
        } else {
            diagnosticProblemsCount++
            diag_item_location_sensor.setDiagnosisDrawable(true)
            diag_item_location_sensor.setOnClickListener {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(this)
                    .layout(R.layout.template_alert_dialog_layout)
                    .positiveButton(diag_item_location_sensor.getDiagnosisLink(),
                        DialogInterface.OnClickListener { _, _ ->
                            enableSensor(SensorType.GPS)
                        })
                    .show()

                val titleTextView =
                    alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                titleTextView?.text = diag_item_location_sensor.getDiagnosisTitle()
                descriptionTextView?.text =
                    diag_item_location_sensor.getDiagnosticTextKO()
                titleTextView?.headLine1()
                descriptionTextView?.normalText()
            }
        }
    }

    private fun checkActivity() {
        when (DiagnosisHelper.getPermissionStatus(this, PermissionType.ACTIVITY)) {
            PermissionStatus.VALID -> diag_item_activity_recognition.setNormalState()
            PermissionStatus.NOT_VALID -> {
                diagnosticProblemsCount++
                setProblemState(PermissionType.ACTIVITY, diag_item_activity_recognition)
            }
        }
    }

    private fun checkLocation() {
        when (DiagnosisHelper.getPermissionStatus(this, PermissionType.LOCATION)) {
            PermissionStatus.VALID -> diag_item_location.setNormalState()
            PermissionStatus.NOT_VALID -> {
                diagnosticProblemsCount++
                setProblemState(PermissionType.LOCATION, diag_item_location)
            }
        }
    }

    private fun checkExternalStorage() {
        val loggingStatus = checkLoggingStatus()
        switch_enable_logging.isChecked = loggingStatus
        val description = if (loggingStatus) {
            DKResource.buildString(
                this,
                "dk_perm_utils_app_diag_log_ok", PermissionUtilsUI.logPathFile.removePrefix("/")
            )
        } else {
            getString(R.string.dk_perm_utils_app_diag_log_ko)
        }
        text_view_logging_description.text = description
        switch_enable_logging.setOnClickListener {
            if (DiagnosisHelper.getPermissionStatus(
                    this,
                    PermissionType.EXTERNAL_STORAGE
                ) == PermissionStatus.NOT_VALID
            ) {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(this)
                    .layout(R.layout.template_alert_dialog_layout)
                    .positiveButton(getString(R.string.dk_perm_utils_app_diag_log_link),
                        DialogInterface.OnClickListener { _, _ ->
                            requestPermission(PermissionType.EXTERNAL_STORAGE)
                        })
                    .show()

                val titleTextView =
                    alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(R.id.text_view_alert_description)

                titleTextView?.text =
                    getString(R.string.dk_perm_utils_app_diag_log_title)
                descriptionTextView?.text =
                    getString(R.string.dk_perm_utils_app_diag_log_ko)

                titleTextView?.headLine1()
                descriptionTextView?.normalText()

            } else {
                val loggingDescription = if (switch_enable_logging.isChecked) {
                    DriveKit.enableLogging(PermissionUtilsUI.logPathFile)
                    DKResource.buildString(
                        this,
                        "dk_perm_utils_app_diag_log_ok",
                        PermissionUtilsUI.logPathFile.removePrefix("/")
                    )
                } else {
                    DriveKit.disableLogging()
                    getString(R.string.dk_perm_utils_app_diag_log_ko)
                }
                text_view_logging_description.text = loggingDescription
            }
        }
    }

    private fun checkNotification() {
        when (DiagnosisHelper.getPermissionStatus(this, PermissionType.NOTIFICATION)) {
            PermissionStatus.VALID -> diag_item_notification.setNormalState()
            PermissionStatus.NOT_VALID -> {
                diagnosticProblemsCount++
                setProblemState(PermissionType.NOTIFICATION, diag_item_notification)
            }
        }
    }

    private fun checkNetwork() {
        if (DiagnosisHelper.isNetworkReachable(this)) {
            diag_item_connectivity.setNormalState()
        } else {
            diagnosticProblemsCount++
            diag_item_connectivity.setDiagnosisDrawable(true)
            diag_item_connectivity.setOnClickListener {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(this)
                    .layout(R.layout.template_alert_dialog_layout)
                    .positiveButton(diag_item_connectivity.getDiagnosisLink(),
                        DialogInterface.OnClickListener { _, _ ->
                            val networkIntent = Intent(Settings.ACTION_SETTINGS)
                            startActivity(networkIntent)
                        })
                    .show()

                val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                titleTextView?.text = diag_item_connectivity.getDiagnosisTitle()
                descriptionTextView?.text = diag_item_connectivity.getDiagnosticTextKO()
                titleTextView?.headLine1()
                descriptionTextView?.normalText()
            }
        }
    }

    private fun displayBatteryOptimizationSection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            text_view_battery_description_2.text = DKResource.buildString(
                this,
                "dk_perm_utils_app_diag_battery_text_android_02",
                getString(R.string.dk_perm_utils_app_diag_battery_link_android)
            )

            if (DiagnosisHelper.getBatteryOptimizationsStatus(this) == PermissionStatus.VALID) {
                text_view_battery_description_2.visibility = View.GONE
            }

            text_view_battery_description_2.setOnClickListener {
                DiagnosisHelper.requestBatteryOptimization(this)
            }
        } else {
            text_view_battery_description_2.visibility = View.GONE
        }
    }

    private fun displayActivityItem() {
        diag_item_activity_recognition.visibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun displayLogSection() {
        val visibility =
            if (PermissionUtilsUI.shouldDisplayDiagnosisLogs) View.VISIBLE else View.GONE
        switch_enable_logging.visibility = visibility
        text_view_logging_description.visibility = visibility
    }

    private fun displayReportSection() {
        when (val contactType = PermissionUtilsUI.contactType) {
            is ContactType.NONE -> {
                text_view_help_title.visibility = View.GONE
                text_view_help_description.visibility = View.GONE
                button_help_report.visibility = View.GONE
                support_view_separator.visibility = View.GONE
            }
            is ContactType.WEB -> {
                button_help_report.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, contactType.url)
                    startActivity(intent)
                }
            }
            is ContactType.EMAIL -> {
                button_help_report.setOnClickListener {
                    val contentMail = contactType.contentMail
                    val recipients = contentMail.getRecipients().toTypedArray()
                    val bccRecipients = contentMail.getBccRecipients().toTypedArray()
                    val subject = contentMail.getSubject()

                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "plain/text"
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                    intent.putExtra(Intent.EXTRA_BCC, bccRecipients)
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                    intent.putExtra(Intent.EXTRA_TEXT, contentMail.getMailBody())

                    if (switch_enable_logging.isChecked && checkLoggingStatus()) {
                        val root = Environment.getExternalStorageDirectory()
                        val logPathFile = PermissionUtilsUI.logPathFile + getLoggingFile()
                        val file = File(root, logPathFile)
                        if (!file.exists() || !file.canRead()) {
                            startActivity(intent)
                        }
                        val uri =
                            FileProvider.getUriForFile(
                                this,
                                "$packageName.provider",
                                file
                            )
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun displayBluetoothItem() {
        val visibility = if (PermissionUtilsUI.isBluetoothNeeded) View.VISIBLE else View.GONE
        diag_item_bluetooth.visibility = visibility
    }

    private fun enableSensor(sensorType: SensorType) {
        when (sensorType) {
            SensorType.GPS -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            SensorType.BLUETOOTH -> startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
        }
    }

    private fun registerReceiver() {
        sensorsReceiver = object : SensorsReceiver() {
            override fun getBluetoothState(bluetoothState: Boolean) {
                //If you want to get the bluetooth state individually
            }

            override fun getConnectivityState(connectivityState: Boolean) {
                //If you want to get the connectivity state individually
            }

            override fun getGpsState(gpsState: Boolean) {
                //If you want to get the GPS state individually
            }

            override fun onSensorBroadcastReceived() {
                init()
            }
        }

        val gpsFilter = IntentFilter(SensorsReceiver.LOCATION_INTENT_ACTION)
        registerReceiver(sensorsReceiver, gpsFilter)

        val bluetoothFilter = IntentFilter(SensorsReceiver.BLUETOOTH_INTENT_ACTION)
        registerReceiver(sensorsReceiver, bluetoothFilter)

        val connectivityFilter = IntentFilter(SensorsReceiver.CONNECTIVITY_INTENT_ACTION)
        registerReceiver(sensorsReceiver, connectivityFilter)
    }

    private fun getLoggingFile(): String {
        val calendar = Calendar.getInstance()
        return "log-${calendar.get(Calendar.YEAR)}-${(calendar.get(Calendar.MONTH) + 1)}.txt"
    }

    private fun checkLoggingStatus(): Boolean =
        DiagnosisHelper.getPermissionStatus(
            this,
            PermissionType.EXTERNAL_STORAGE
        ) == PermissionStatus.VALID
                && DriveKit.config.logPath != null && PermissionUtilsUI.shouldDisplayDiagnosisLogs

    private fun requestPermission(permissionType: PermissionType) {
        when (permissionType) {
            PermissionType.LOCATION -> requestLocationPermission()
            PermissionType.ACTIVITY -> requestActivityPermission()
            PermissionType.EXTERNAL_STORAGE -> requestExternalStoragePermission()
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

    private fun requestExternalStoragePermission() {
        permissionCallback = object :
            OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                DriveKit.enableLogging(PermissionUtilsUI.logPathFile)
                text_view_logging_description.text =
                    getString(R.string.dk_perm_utils_app_diag_log_ok)
            }

            override fun onPermissionDeclined(permissionName: Array<String>) {
                handlePermissionDeclined(
                    this@AppDiagnosisActivity,
                    R.string.dk_common_permission_storage_rationale,
                    this@AppDiagnosisActivity::requestExternalStoragePermission
                )
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                handlePermissionTotallyDeclined(
                    this@AppDiagnosisActivity,
                    R.string.dk_common_permission_storage_rationale
                )
            }
        }
        request(
            this,
            permissionCallback as OnPermissionCallback,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private fun setProblemState(permissionType: PermissionType, diagnosticItem: DiagnosisItemView) {
        diagnosticItem.setDiagnosisDrawable(true)
        diagnosticItem.setOnClickListener {
            val alertDialog = DKAlertDialog.LayoutBuilder()
                .init(this)
                .layout(R.layout.template_alert_dialog_layout)
                .positiveButton(diagnosticItem.getDiagnosisLink(),
                    DialogInterface.OnClickListener { _, _ ->
                        requestPermission(permissionType)
                    })
                .show()

            val titleTextView =
                alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
            val descriptionTextView =
                alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
            titleTextView?.text = diagnosticItem.getDiagnosisTitle()
            descriptionTextView?.text =
                diagnosticItem.getDiagnosticTextKO()
            titleTextView?.headLine1()
            descriptionTextView?.normalText()
        }
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

    override fun onResume() {
        super.onResume()
        registerReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(sensorsReceiver)
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
                    DriveKit.enableLogging(PermissionUtilsUI.logPathFile)
                }
            }

            REQUEST_BATTERY_OPTIMIZATION ->
                if (DiagnosisHelper.getBatteryOptimizationsStatus(this) == PermissionStatus.VALID) {
                    text_view_battery_description_2.visibility = View.GONE
                }
        }
    }
}