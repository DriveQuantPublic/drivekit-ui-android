package com.drivequant.drivekit.permissionsutils.diagnosis.activity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.ContactType
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.TextArg
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.utils.ConnectivityType
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.core.utils.PermissionStatus
import com.drivequant.drivekit.core.utils.PermissionType
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.commons.views.DiagnosisItemView
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback
import com.drivequant.drivekit.permissionsutils.permissions.activity.RequestPermissionActivity
import com.drivequant.drivekit.permissionsutils.permissions.receiver.SensorsReceiver

class AppDiagnosisActivity : RequestPermissionActivity() {

    private val REQUEST_NOTIFICATIONS = 6

    private var sensorsReceiver: SensorsReceiver? = null
    private var errorsCount = 0

    private lateinit var itemLocation: DiagnosisItemView
    private lateinit var itemActivityRecognition: DiagnosisItemView
    private lateinit var itemAutoResetPermissions: DiagnosisItemView
    private lateinit var itemNearbyDevices: DiagnosisItemView
    private lateinit var itemNotification: DiagnosisItemView
    private lateinit var itemFullScreenIntent: DiagnosisItemView
    private lateinit var itemBluetooth: DiagnosisItemView
    private lateinit var itemLocationSensor: DiagnosisItemView
    private lateinit var itemConnectivity: DiagnosisItemView
    private lateinit var itemBatteryOptimization: DiagnosisItemView
    private lateinit var summaryIcon: ImageView
    private lateinit var summaryTitle: TextView
    private lateinit var summaryDescription: TextView
    private lateinit var helpTitle: TextView
    private lateinit var helpDescription: TextView
    private lateinit var helpReportButton: ComposeView
    private lateinit var batteryTitle: TextView
    private lateinit var batteryDescription: TextView
    private lateinit var summaryViewSeparator: View
    private lateinit var diagViewSeparator: View
    private lateinit var batteryViewSeparator: View
    private lateinit var diagnosisRoot: View


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_permissions_diagnosis), javaClass.simpleName)

        setContentView(R.layout.activity_app_diagnosis)

        this.itemLocation = findViewById(R.id.diag_item_location)
        this.itemActivityRecognition = findViewById(R.id.diag_item_activity_recognition)
        this.itemAutoResetPermissions = findViewById(R.id.diag_item_auto_reset_permissions)
        this.itemNearbyDevices = findViewById(R.id.diag_item_nearby_devices)
        this.itemNotification = findViewById(R.id.diag_item_notification)
        this.itemBluetooth = findViewById(R.id.diag_item_bluetooth)
        this.itemLocationSensor = findViewById(R.id.diag_item_location_sensor)
        this.itemConnectivity = findViewById(R.id.diag_item_connectivity)
        this.itemBatteryOptimization = findViewById(R.id.diag_item_battery_optimization)
        this.itemFullScreenIntent = findViewById(R.id.diag_item_full_screen_intent)
        this.summaryIcon = findViewById(R.id.image_view_summary_icon)
        this.summaryTitle = findViewById(R.id.text_view_summary_title)
        this.summaryDescription = findViewById(R.id.text_view_summary_description)
        this.helpTitle = findViewById(R.id.text_view_help_title)
        this.helpDescription = findViewById(R.id.text_view_help_description)
        this.helpReportButton = findViewById(R.id.button_help_report)
        this.batteryTitle = findViewById(R.id.text_view_battery_title)
        this.batteryDescription = findViewById(R.id.text_view_battery_description)
        this.summaryViewSeparator = findViewById(R.id.summary_view_separator)
        this.diagViewSeparator = findViewById(R.id.diag_view_separator)
        this.batteryViewSeparator = findViewById(R.id.battery_view_separator)
        this.diagnosisRoot = findViewById(R.id.dk_diagnosis_root)

        setToolbar()
        init()
        displayBluetoothItem()
        displayActivityItem()
        displayAutoResetItem()
        displayNearbyDevicesItem()
        displayFullScreenIntentItem()
        displayBatteryOptimizationItem()
        displayBatteryOptimizationSection()
        displayReportSection()

        DKEdgeToEdgeManager.apply {
            DKEdgeToEdgeManager.apply {
                setSystemStatusBarForegroundColor(window)
                update(this@AppDiagnosisActivity.diagnosisRoot) { view, insets ->
                    addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                    addSystemNavigationBarBottomPadding(view, insets)
                }
            }
        }
    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun init() {
        checkPermissionItem(PermissionType.LOCATION, this.itemLocation)
        checkPermissionItem(PermissionType.ACTIVITY, this.itemActivityRecognition)
        checkPermissionItem(PermissionType.AUTO_RESET, this.itemAutoResetPermissions)
        checkPermissionItem(PermissionType.NEARBY, this.itemNearbyDevices)
        checkPermissionItem(PermissionType.NOTIFICATION, this.itemNotification)
        checkPermissionItem(PermissionType.FULL_SCREEN_INTENT, this.itemFullScreenIntent)
        checkGPS()
        checkBluetooth()
        checkNetwork()
        checkBatteryOptimization()
        setSummaryContent()
    }

    private fun setSummaryContent() {
        if (errorsCount == 0) {
            this.summaryIcon.setImageResource(R.drawable.dk_perm_utils_checked)
            this.summaryTitle.setText(R.string.dk_perm_utils_diag_app_ok)
            this.summaryDescription.setText(R.string.dk_perm_utils_diag_app_ok_text)
        } else {
            this.summaryDescription.setText(R.string.dk_perm_utils_app_diag_app_ko_text)
            this.summaryIcon.setImageResource(R.drawable.dk_perm_utils_high_priority)
            this.summaryTitle.text = resources.getQuantityString(
                R.plurals.diagnosis_error_plural,
                errorsCount,
                errorsCount
            )
        }
        errorsCount = 0
    }

    private fun checkBluetooth() =
        if (DiagnosisHelper.isActivated(this, ConnectivityType.BLUETOOTH)) {
            this.itemBluetooth.setNormalState()
        } else {
            if (PermissionsUtilsUI.isBluetoothNeeded) {
                errorsCount++
            }
            this.itemBluetooth.setDiagnosisDrawable(PermissionStatus.NOT_VALID)
            this.itemBluetooth.setOnClickListener {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(this)
                    .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                    .positiveButton(this.itemBluetooth.getDiagnosisLink()) { _, _ ->
                        enableSensor(ConnectivityType.BLUETOOTH)
                    }
                    .show()

                val titleTextView =
                    alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)
                titleTextView?.text = this.itemBluetooth.getDiagnosisTitle()
                descriptionTextView?.text = this.itemBluetooth.getDiagnosticTextKO()
                descriptionTextView?.text =
                    this.itemBluetooth.getDiagnosticTextKO()
            }
        }

    private fun checkGPS() {
        if (DiagnosisHelper.isActivated(this, ConnectivityType.GPS)) {
            this.itemLocationSensor.setNormalState()
        } else {
            errorsCount++
            this.itemLocationSensor.setDiagnosisDrawable(PermissionStatus.NOT_VALID)
            this.itemLocationSensor.setOnClickListener {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(this)
                    .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                    .positiveButton(this.itemLocationSensor.getDiagnosisLink()) { _, _ ->
                        enableSensor(ConnectivityType.GPS)
                    }
                    .show()

                val titleTextView =
                    alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)
                titleTextView?.text = this.itemLocationSensor.getDiagnosisTitle()
                descriptionTextView?.text =
                    this.itemLocationSensor.getDiagnosticTextKO()
            }
        }
    }

    private fun checkPermissionItem(permissionType: PermissionType, diagnosticItem: DiagnosisItemView) {
        when (val status = DiagnosisHelper.getPermissionStatus(this, permissionType)) {
            PermissionStatus.VALID -> diagnosticItem.setNormalState()
            PermissionStatus.WARNING,
            PermissionStatus.NOT_VALID -> {
                if (status == PermissionStatus.NOT_VALID) {
                    errorsCount++
                }
                setProblemState(diagnosticItem, status, object : ResolveProblemStateListener {
                    override fun onSubmit() {
                        requestPermission(permissionType)
                    }
                })
            }
        }
    }

    private fun checkNetwork() {
        if (DiagnosisHelper.isNetworkReachable(this)) {
            this.itemConnectivity.setNormalState()
        } else {
            errorsCount++
            this.itemConnectivity.setDiagnosisDrawable(PermissionStatus.NOT_VALID)
            this.itemConnectivity.setOnClickListener {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(this)
                    .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                    .positiveButton(this.itemConnectivity.getDiagnosisLink()) { _, _ ->
                        val networkIntent = Intent(Settings.ACTION_SETTINGS)
                        startActivity(networkIntent)
                    }
                    .show()

                val titleTextView = alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)
                titleTextView?.text = this.itemConnectivity.getDiagnosisTitle()
                descriptionTextView?.text = this.itemConnectivity.getDiagnosticTextKO()
            }
        }
    }

    private fun checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            when (DiagnosisHelper.getBatteryOptimizationsStatus(this)) {
                PermissionStatus.VALID -> this.itemBatteryOptimization.setNormalState()
                PermissionStatus.WARNING -> {
                    // do nothing
                }
                PermissionStatus.NOT_VALID -> {
                    errorsCount++
                    setProblemState(
                        this.itemBatteryOptimization, PermissionStatus.NOT_VALID, object : ResolveProblemStateListener {
                            override fun onSubmit() {
                                DiagnosisHelper.requestBatteryOptimization(this@AppDiagnosisActivity)
                            }})
                }
            }
        }
    }

    private fun displayBatteryOptimizationSection() {
        val brand = Brand.from(Build.MANUFACTURER)
        if (brand == Brand.UNKNOWN) {
            this.batteryDescription.text = DKResource.buildString(
                this,
                R.string.dk_perm_utils_energy_saver_android_unknown_brand_main,
                DKColors.complementaryFontColor,
                TextArg(getString(R.string.dk_perm_utils_energy_saver_android_unknown_brand_link_text), DKColors.secondaryColor)
            )
        } else {
            this.batteryDescription.text = DKResource.buildString(
                this,
                R.string.dk_perm_utils_energy_saver_android_known_brand_main,
                DKColors.complementaryFontColor,
                TextArg(brand.name),
                TextArg(getString(R.string.dk_perm_utils_energy_saver_android_known_brand_link_text), DKColors.secondaryColor)
            )
        }
        this.batteryDescription.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(brand.linkKey()))
            startActivity(intent)
        }
    }

    private fun displayActivityItem() {
        this.itemActivityRecognition.visibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun displayAutoResetItem() {
        this.itemAutoResetPermissions.visibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun displayNearbyDevicesItem() {
        this.itemNearbyDevices.visibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun displayFullScreenIntentItem() {
        val display = DriveKit.modules.tripAnalysis?.isCrashDetectionFeedbackEnabled() ?: false
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                && DiagnosisHelper.getNotificationStatus(this) == PermissionStatus.VALID
        this.itemFullScreenIntent.visibility = if (display) View.VISIBLE else View.GONE
    }

    private fun displayReportSection() {
        val contactType = PermissionsUtilsUI.contactType
        if (contactType == ContactType.NONE) {
            this.helpTitle.visibility = View.GONE
            this.helpDescription.visibility = View.GONE
            this.helpReportButton.visibility = View.GONE
        }
        manageHelpReportButton(contactType)
    }

    private fun manageHelpReportButton(contactType: ContactType) {
        this.helpReportButton.setContent {
            DKPrimaryButton(getString(R.string.dk_perm_utils_app_diag_help_request_button)) {
                when (contactType) {
                    ContactType.NONE -> { }
                    is ContactType.EMAIL -> {
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
                        DriveKitLog.getLogUriFile(this)?.let {
                            intent.putExtra(Intent.EXTRA_STREAM, it)
                        }
                        startActivity(intent)
                    }
                    is ContactType.WEB -> {
                        val intent = Intent(Intent.ACTION_VIEW, contactType.url)
                        startActivity(intent)
                    }
                }
            }
        }

    }

    private fun displayBluetoothItem() {
        val visibility = if (PermissionsUtilsUI.isBluetoothNeeded) View.VISIBLE else View.GONE
        this.itemBluetooth.visibility = visibility
    }

    private fun displayBatteryOptimizationItem() {
        this.itemBatteryOptimization.visibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun enableSensor(sensorType: ConnectivityType) {
        when (sensorType) {
            ConnectivityType.GPS -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            ConnectivityType.BLUETOOTH -> {
                @SuppressLint("MissingPermission")
                if (DiagnosisHelper.getNearbyDevicesStatus(this) == PermissionStatus.VALID) {
                    startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                }
            }
        }
    }

    private fun registerReceiver() {
        sensorsReceiver = object : SensorsReceiver() {
            override fun getBluetoothState(bluetoothState: Boolean) {
                // If you want to get the bluetooth state individually
            }

            override fun getConnectivityState(connectivityState: Boolean) {
                // If you want to get the connectivity state individually
            }

            override fun getGpsState(gpsState: Boolean) {
                // If you want to get the GPS state individually
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

    private fun requestPermission(permissionType: PermissionType) {
        when (permissionType) {
            PermissionType.LOCATION -> requestLocationPermission()
            PermissionType.ACTIVITY -> requestActivityPermission()
            PermissionType.AUTO_RESET -> requestAutoResetPermission()
            PermissionType.NOTIFICATION -> requestNotificationPermission()
            PermissionType.NEARBY -> requestNearbyPermission()
            PermissionType.FULL_SCREEN_INTENT -> requestFullScreenIntentPermission()
        }
    }

    private fun requestActivityPermission() {
        permissionCallback = object :
            OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                // Nothing to do.
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
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

    private fun requestAutoResetPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = DiagnosisHelper.buildSettingsIntent(this)
            intent.action = Intent.ACTION_AUTO_REVOKE_PERMISSIONS
            startActivityForResult(intent, DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS)
        }
    }

    private fun requestNotificationPermission() {
        val notificationIntent = Intent()
        notificationIntent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        notificationIntent.putExtra("app_package", packageName)
        notificationIntent.putExtra("app_uid", applicationInfo.uid)
        notificationIntent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
        startActivityForResult(notificationIntent, REQUEST_NOTIFICATIONS)
    }

    private fun requestNearbyPermission() {
        permissionCallback = object :
            OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                // Nothing to do.
            }

            override fun onPermissionDeclined(permissionName: Array<String>) {
                handlePermissionDeclined(
                    this@AppDiagnosisActivity, com.drivequant.drivekit.common.ui.R.string.dk_common_app_diag_nearby_ko,
                    this@AppDiagnosisActivity::requestNearbyPermission
                )
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                handlePermissionTotallyDeclined(
                    this@AppDiagnosisActivity,
                    com.drivequant.drivekit.common.ui.R.string.dk_common_app_diag_nearby_ko
                )
            }
        }
        request(
            this,
            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT
        )
    }

    private fun requestFullScreenIntentPermission() {
        DiagnosisHelper.requestFullScreenPermission(this)
    }

    private fun requestLocationPermission() {
        permissionCallback = object :
            OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                // Nothing to do.
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
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                }
            }
        } else {
            request(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun setProblemState(diagnosticItem: DiagnosisItemView, permissionStatus: PermissionStatus, listener: ResolveProblemStateListener) {
        diagnosticItem.apply {
            setDiagnosisDrawable(permissionStatus)
            setOnClickListener {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(this@AppDiagnosisActivity)
                    .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                    .positiveButton(getDiagnosisLink()) { _, _ ->
                        listener.onSubmit()
                    }
                    .show()

                val titleTextView =
                    alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)
                titleTextView?.text = getDiagnosisTitle()
                descriptionTextView?.text = getDiagnosticTextKO()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver()
        setActivityTitle(getString(R.string.dk_perm_utils_app_diag_title))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(sensorsReceiver)
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var dismissAlertDialog = false
        when (requestCode) {
            DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS -> dismissAlertDialog = true
            DiagnosisHelper.REQUEST_FULL_SCREEN_INTENT -> dismissAlertDialog = true
            REQUEST_NOTIFICATIONS -> {
                dismissAlertDialog = true
                displayFullScreenIntentItem()
            }
        }
        if (dismissAlertDialog) {
            alertDialog?.dismiss()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

private enum class Brand {
    UNKNOWN,
    ASUS,
    BLACKVIEW,
    GOOGLE,
    HUAWEI,
    HONOR,
    ONEPLUS,
    OPPO,
    REALME,
    SAMSUNG,
    VIVO,
    WIKO,
    XIAOMI;

    companion object {
        fun from(value: String): Brand {
            val uppercaseValue = value.uppercase()
            for (brand in values()) {
                if (brand.name.uppercase() == uppercaseValue) {
                    return brand
                }
            }
            return UNKNOWN
        }
    }

    @StringRes
    fun linkKey(): Int = when (this) {
        UNKNOWN -> R.string.dk_perm_utils_brand_unknown
        ASUS -> R.string.dk_perm_utils_brand_asus
        BLACKVIEW -> R.string.dk_perm_utils_brand_blackview
        GOOGLE -> R.string.dk_perm_utils_brand_google
        HUAWEI -> R.string.dk_perm_utils_brand_huawei
        HONOR -> R.string.dk_perm_utils_brand_honor
        ONEPLUS -> R.string.dk_perm_utils_brand_oneplus
        OPPO -> R.string.dk_perm_utils_brand_oppo
        REALME -> R.string.dk_perm_utils_brand_realme
        SAMSUNG -> R.string.dk_perm_utils_brand_samsung
        VIVO -> R.string.dk_perm_utils_brand_vivo
        WIKO -> R.string.dk_perm_utils_brand_wiko
        XIAOMI -> R.string.dk_perm_utils_brand_xiaomi
    }
}

interface ResolveProblemStateListener {
    fun onSubmit()
}
