package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.core.utils.DiagnosisHelper.REQUEST_BATTERY_OPTIMIZATION
import com.drivequant.drivekit.core.utils.PermissionStatus
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.databinding.ActivityBackgroundTaskPermissionBinding

class BackgroundTaskPermissionActivity : BasePermissionActivity() {

    private lateinit var binding: ActivityBackgroundTaskPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityBackgroundTaskPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar(R.string.dk_perm_utils_permissions_phone_settings_background_title)
        setStyle()
        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            addInsetsPaddings(findViewById(R.id.toolbar))
            addInsetsMargins(findViewById(R.id.scrollview))
        }
    }

    fun onRequestPermissionClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        DiagnosisHelper.requestBatteryOptimization(this)
    }

    @Suppress("OverrideDeprecatedMigration")
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
        window.decorView.setBackgroundColor(DKColors.backgroundViewColor)
    }
}
