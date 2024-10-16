package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.core.utils.DiagnosisHelper.REQUEST_PERMISSIONS_OPEN_SETTINGS
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.databinding.ActivityRecognitionPermissionBinding
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback

class ActivityRecognitionPermissionActivity : BasePermissionActivity() {

    private lateinit var binding: ActivityRecognitionPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityRecognitionPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar(R.string.dk_perm_utils_permissions_phone_settings_activity_title)
        setStyle()
        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(binding.root) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
        }
    }

    fun onRequestPermissionClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        checkRequiredPermissions()
    }

    private fun checkRequiredPermissions() {
        permissionCallback = object :
            OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                forward()
            }

            override fun onPermissionDeclined(permissionName: Array<String>) {
               handlePermissionDeclined(this@ActivityRecognitionPermissionActivity,R.string.dk_perm_utils_app_diag_activity_ko,
                   this@ActivityRecognitionPermissionActivity::checkRequiredPermissions)
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                binding.buttonRequestActivityPermission.text = getString(R.string.dk_perm_utils_permissions_text_button_activity_settings)
                handlePermissionTotallyDeclined(this@ActivityRecognitionPermissionActivity, R.string.dk_perm_utils_app_diag_activity_ko)
            }
        }
        request(this, Manifest.permission.ACTIVITY_RECOGNITION)
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSIONS_OPEN_SETTINGS) {
            checkRequiredPermissions()
        }
    }

    private fun setStyle() {
        binding.textViewActivityPermissionTitle.highlightMedium()
        binding.textViewActivityPermissionText.normalText()
        window.decorView.setBackgroundColor(DKColors.backgroundViewColor)
    }
}
