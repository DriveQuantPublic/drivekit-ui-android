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
import android.annotation.SuppressLint

import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Build
import android.provider.Settings
import androidx.appcompat.widget.Toolbar
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.commons.views.DiagnosisItemView
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_BATTERY_OPTIMIZATION
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionStatus
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionType
import com.drivequant.drivekit.permissionsutils.diagnosis.model.SensorType
import com.drivequant.drivekit.common.ui.utils.ContactType
import com.drivequant.drivekit.permissionsutils.permissions.receiver.SensorsReceiver


class AppDiagnosisActivity : RequestPermissionActivity() {

    private var sensorsReceiver: SensorsReceiver? = null
    private var errorsCount = 0

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(this, "dk_tag_permissions_diagnosis"), javaClass.simpleName)

        setContentView(R.layout.activity_app_diagnosis)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setToolbar()

        init()

        displayBatteryOptimizationSection()
        displayBluetoothItem()
        displayActivityItem()
        displayAutoResetItem()
        displayLogSection()
        displayReportSection()

        setStyle()
    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)

        this.title = DKResource.convertToString(this, "dk_perm_utils_app_diag_title")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun init() {
        checkGPS()
        checkBluetooth()
        checkLocation()
        checkActivity()
        checkAutoReset()
        checkNotification()
        checkExternalStorage()
        checkNetwork()

        setSummaryContent()
    }

    private fun setSummaryContent() {
        if (errorsCount == 0) {
            image_view_summary_icon.setImageDrawable(
                DKResource.convertToDrawable(this, "dk_perm_utils_checked")
            )
            text_view_summary_title.text =
                DKResource.convertToString(this, "dk_perm_utils_diag_app_ok")
            text_view_summary_description.text =
                DKResource.convertToString(this, "dk_perm_utils_diag_app_ok_text")
        } else {
            text_view_summary_description.text =
                DKResource.convertToString(this, "dk_perm_utils_app_diag_app_ko_text")
            image_view_summary_icon.setImageDrawable(
                DKResource.convertToDrawable(this, "dk_perm_utils_high_priority")
            )
            text_view_summary_title.text = resources.getQuantityString(
                R.plurals.diagnosis_error_plural,
                errorsCount,
                errorsCount
            )
        }
        errorsCount = 0
    }

    private fun checkBluetooth() {
        if (DiagnosisHelper.isSensorActivated(this, SensorType.BLUETOOTH)) {
            diag_item_bluetooth.setNormalState()
        } else {
            if (PermissionsUtilsUI.isBluetoothNeeded) {
                errorsCount++
            }
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
                    diag_item_bluetooth.getDiagnosticTextKO()
                titleTextView?.headLine1()
                descriptionTextView?.normalText()
            }
        }
    }

    private fun checkGPS() {
        if (DiagnosisHelper.isSensorActivated(this, SensorType.GPS)) {
            diag_item_location_sensor.setNormalState()
        } else {
            errorsCount++
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
                errorsCount++
                setProblemState(PermissionType.ACTIVITY, diag_item_activity_recognition)
            }
        }
    }

    private fun checkLocation() {
        when (DiagnosisHelper.getPermissionStatus(this, PermissionType.LOCATION)) {
            PermissionStatus.VALID -> diag_item_location.setNormalState()
            PermissionStatus.NOT_VALID -> {
                errorsCount++
                setProblemState(PermissionType.LOCATION, diag_item_location)
            }
        }
    }

    private fun checkAutoReset() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (DiagnosisHelper.getAutoResetStatus(this) == PermissionStatus.VALID) {
                diag_item_auto_reset_permissions.setNormalState()
            } else {
                errorsCount++
                setProblemState(PermissionType.AUTO_RESET, diag_item_auto_reset_permissions)
            }
        }
    }

    private fun checkExternalStorage() {
        val loggingStatus = checkLoggingStatus()
        switch_enable_logging.isChecked = loggingStatus
        val description = if (loggingStatus) {
            handleLoggingDescription()
        } else {
            DKResource.convertToString(this, "dk_perm_utils_app_diag_log_ko")
        }

        text_view_logging_description.text = SpannableString.valueOf(description)
        switch_enable_logging.setOnClickListener {
            val loggingDescription = if (switch_enable_logging.isChecked) {
                DriveKit.enableLogging(PermissionsUtilsUI.logPathFile)
                handleLoggingDescription()
            } else {
                DriveKit.disableLogging()
                DKResource.convertToString(this, "dk_perm_utils_app_diag_log_ko")
            }
            text_view_logging_description.text = SpannableString.valueOf(loggingDescription)
        }
    }

    private fun getUserFriendlyPath() : String {
        val path = DriveKitLog.buildDirectory(PermissionsUtilsUI.logPathFile).path
        return path.substring("storage/emulated/0/".lastIndex + 1)
    }

    private fun handleLoggingDescription(): Spannable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            SpannableString.valueOf(
                DKResource.convertToString(
                    this,
                    "dk_perm_utils_app_diag_log_ok_android11"
                )
            )
        } else {
            DKResource.buildString(
                this,
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.mainFontColor(),
                "dk_perm_utils_app_diag_log_ok",
                getUserFriendlyPath()
            )
        }
    }

    private fun checkNotification() {
        when (DiagnosisHelper.getPermissionStatus(this, PermissionType.NOTIFICATION)) {
            PermissionStatus.VALID -> diag_item_notification.setNormalState()
            PermissionStatus.NOT_VALID -> {
                errorsCount++
                setProblemState(PermissionType.NOTIFICATION, diag_item_notification)
            }
        }
    }

    private fun checkNetwork() {
        if (DiagnosisHelper.isNetworkReachable(this)) {
            diag_item_connectivity.setNormalState()
        } else {
            errorsCount++
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
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.secondaryColor(),
                "dk_perm_utils_app_diag_battery_text_android_02",
                DKResource.convertToString(this, "dk_perm_utils_app_diag_battery_link_android")
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

    private fun displayAutoResetItem() {
        diag_item_auto_reset_permissions.visibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun displayLogSection() {
        val visibility =
            if (PermissionsUtilsUI.shouldDisplayDiagnosisLogs) View.VISIBLE else View.GONE
        switch_enable_logging.visibility = visibility
        text_view_logging_description.visibility = visibility
    }

    private fun displayReportSection() {
        when (val contactType = PermissionsUtilsUI.contactType) {
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
                    var mailBody =
                        "${contentMail.getMailBody()} ${PermissionsUtilsUI.getDiagnosisDescription(
                            this
                        )}"

                    if (contentMail.overrideMailBodyContent()) {
                        mailBody = contentMail.getMailBody()
                    }

                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "plain/text"
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                    intent.putExtra(Intent.EXTRA_BCC, bccRecipients)
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                    intent.putExtra(Intent.EXTRA_TEXT, mailBody)

                    if (switch_enable_logging.isChecked && checkLoggingStatus()) {
                        DriveKitLog.getLogUriFile(this)?.let {
                            intent.putExtra(Intent.EXTRA_STREAM, it)
                        }
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun displayBluetoothItem() {
        val visibility = if (PermissionsUtilsUI.isBluetoothNeeded) View.VISIBLE else View.GONE
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

    private fun checkLoggingStatus(): Boolean =
        DriveKitLog.isLoggingEnabled &&
                PermissionsUtilsUI.shouldDisplayDiagnosisLogs

    private fun requestPermission(permissionType: PermissionType) {
        when (permissionType) {
            PermissionType.LOCATION -> requestLocationPermission()
            PermissionType.ACTIVITY -> requestActivityPermission()
            PermissionType.AUTO_RESET -> requestAutoResetPermission()
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

    private fun requestAutoResetPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = DiagnosisHelper.buildSettingsIntent(this)
            intent.action = Intent.ACTION_AUTO_REVOKE_PERMISSIONS
            startActivityForResult(intent, REQUEST_PERMISSIONS_OPEN_SETTINGS)
        }
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
        text_view_summary_description.normalText(DriveKitUI.colors.complementaryFontColor())

        text_view_battery_title.headLine1()
        text_view_battery_description_1.normalText(DriveKitUI.colors.complementaryFontColor())
        text_view_battery_description_2.normalText(DriveKitUI.colors.complementaryFontColor())
        text_view_battery_description_3.normalText(DriveKitUI.colors.complementaryFontColor())

        button_help_report.button()
        text_view_help_title.headLine1()
        text_view_help_description.normalText(DriveKitUI.colors.complementaryFontColor())
        if (switch_enable_logging.isChecked) {
            switch_enable_logging.thumbDrawable.tintDrawable(
                DriveKitUI.colors.secondaryColor()
            )
        }
        switch_enable_logging.setTextColor(DriveKitUI.colors.mainFontColor())
        switch_enable_logging.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(
                com.drivequant.drivekit.common.ui.R.dimen.dk_text_medium
            )
        )
        switch_enable_logging.setTypeface(DriveKitUI.primaryFont(this), Typeface.BOLD)
        text_view_logging_description.normalText(DriveKitUI.colors.complementaryFontColor())

        summary_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        diag_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        battery_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        support_view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        dk_diagnosis_root.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }

    override fun onResume() {
        super.onResume()
        registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(sensorsReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PERMISSIONS_OPEN_SETTINGS -> alertDialog?.dismiss()
            REQUEST_BATTERY_OPTIMIZATION ->
                if (DiagnosisHelper.getBatteryOptimizationsStatus(this) == PermissionStatus.VALID) {
                    text_view_battery_description_2.visibility = View.GONE
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}