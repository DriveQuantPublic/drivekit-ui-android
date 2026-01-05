package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.common.ui.utils.injectContent
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.databinding.ActivityNotificationsPermissionBinding
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class NotificationsPermissionActivity : BasePermissionActivity() {

    private val buttonText by lazy { mutableStateOf(getString(R.string.dk_perm_utils_permissions_phone_settings_notifications_button)) }

    private lateinit var binding: ActivityNotificationsPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar(R.string.dk_perm_utils_app_diag_notification_title)
        setStyle()
        manageSkipButton()

        binding.buttonRequestNotificationsPermission.injectContent {
            DKPrimaryButton(buttonText.value) {
                checkRequiredPermissions()
            }
        }

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(binding.root) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
        }
    }

    private fun checkRequiredPermissions() {
        permissionCallback = object : OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                forward()
            }

            override fun onPermissionDeclined(permissionName: Array<String>) {
                handlePermissionDeclined(
                    this@NotificationsPermissionActivity,
                    R.string.dk_perm_utils_app_diag_notification_ko,
                    this@NotificationsPermissionActivity::checkRequiredPermissions
                )
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                buttonText.value = getString(R.string.dk_perm_utils_permissions_text_button_notifications_settings)
                handlePermissionTotallyDeclined(
                    this@NotificationsPermissionActivity,
                    R.string.dk_perm_utils_app_diag_notification_ko
                )
            }
        }

        request(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    private fun manageSkipButton() {
        binding.buttonSkip.setOnClickListener {
            skip(PermissionView.NOTIFICATIONS)
        }
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS) {
            checkRequiredPermissions()
        }
    }

    private fun setStyle() {
        binding.textViewNotificationsPermissionTitle.highlightMedium()
        binding.textViewNotificationsPermissionText1.normalText()
        binding.textViewNotificationsPermissionText2.normalText()
        binding.buttonSkip.normalText()
        window.decorView.setBackgroundColor(DKColors.backgroundViewColor)
    }
}
