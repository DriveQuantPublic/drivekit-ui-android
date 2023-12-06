package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.core.utils.DiagnosisHelper.REQUEST_BATTERY_OPTIMIZATION
import com.drivequant.drivekit.core.utils.PermissionStatus
import com.drivequant.drivekit.permissionsutils.databinding.ActivityBackgroundTaskPermissionBinding

class BackgroundTaskPermissionActivity : BasePermissionActivity() {

    private lateinit var binding: ActivityBackgroundTaskPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackgroundTaskPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar("dk_perm_utils_permissions_phone_settings_background_title")
        setStyle()
    }

    fun onRequestPermissionClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        DiagnosisHelper.requestBatteryOptimization(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BATTERY_OPTIMIZATION && DiagnosisHelper.getBatteryOptimizationsStatus(this) == PermissionStatus.VALID) {
            forward()
        }
    }

    private fun setStyle() {
        binding.textViewBackgroundTaskPermissionTitle.highlightMedium()
        binding.textViewBackgroundTaskPermissionText1.normalText()
        binding.textViewBackgroundTaskPermissionText2.normalText()
        binding.buttonRequestBackgroundPermission.button()
        window.decorView.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }
}
