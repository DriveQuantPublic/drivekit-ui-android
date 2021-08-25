package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.highlightMedium
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_BATTERY_OPTIMIZATION
import com.drivequant.drivekit.permissionsutils.diagnosis.model.PermissionStatus
import kotlinx.android.synthetic.main.activity_background_task_permission.*
import kotlinx.android.synthetic.main.activity_background_task_permission.text_view_background_task_permission_text1
import kotlinx.android.synthetic.main.activity_background_task_permission.text_view_background_task_permission_text2
import kotlinx.android.synthetic.main.activity_background_task_permission.text_view_background_task_permission_title
import kotlinx.android.synthetic.main.activity_nearby_devices_permission.*

class NearbyDevicesPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_devices_permission)
        setToolbar("dk_perm_utils_app_diag_nearby_title")
        setStyle()
    }

    fun onRequestPermissionClicked(view: View) {
        DiagnosisHelper.requestBatteryOptimization(this) // TODO wip
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BATTERY_OPTIMIZATION) {
            // We may need to skip that screen because some Chinese OEMs (Xiaomi / Redmi) automatically return RESULT_CANCELED
            if (DiagnosisHelper.getBatteryOptimizationsStatus(this) == PermissionStatus.VALID || resultCode == Activity.RESULT_CANCELED) {
                if (resultCode == Activity.RESULT_CANCELED) {
                    DriveKitLog.i(PermissionsUtilsUI.TAG, "User or system has cancelled the background task permissions")
                }
                finish()
                next()
            }
        }
    }

    private fun setStyle() {
        text_view_nearby_devices_permission_title.highlightMedium()
        text_view_nearby_devices_permission_text1.normalText()
        text_view_nearby_devices_permission_text2.normalText()
        button_request_nearby_devices_permission.button()
        window.decorView.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }
}
