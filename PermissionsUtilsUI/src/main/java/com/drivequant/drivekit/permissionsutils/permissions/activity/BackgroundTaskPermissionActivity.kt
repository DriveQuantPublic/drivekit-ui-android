package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_BATTERY_OPTIMIZATION
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionStatus
import kotlinx.android.synthetic.main.activity_background_task_permission.*

class BackgroundTaskPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background_task_permission)
        setToolbar()
        setStyle()
    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        this.title = DKResource.convertToString(this, "dk_perm_utils_permissions_phone_settings_background_title")
    }

    fun onRequestPermissionClicked(view: View) {
        DiagnosisHelper.requestBatteryOptimization(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BATTERY_OPTIMIZATION) {
            if (DiagnosisHelper.getBatteryOptimizationsStatus(this) == PermissionStatus.VALID) {
                finish()
                next()
            }
        }
    }

    private fun setStyle() {
        text_view_background_task_permission_title.highlightMedium()
        text_view_background_task_permission_text1.normalText()
        text_view_background_task_permission_text2.normalText()
        button_request_background_permission.button()
        window.decorView.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }
}
