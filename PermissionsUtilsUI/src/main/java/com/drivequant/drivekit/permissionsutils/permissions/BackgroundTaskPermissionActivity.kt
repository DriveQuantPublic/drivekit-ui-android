package com.drivequant.drivekit.permissionsutils.permissions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.highlightSmall
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.DiagnosisHelper.REQUEST_BATTERY_OPTIMIZATION
import com.drivequant.drivekit.permissionsutils.diagnosis.PermissionStatus
import kotlinx.android.synthetic.main.activity_background_task_permission.*

class BackgroundTaskPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background_task_permission)
        setStyle()
    }

    fun onRequestPermissionClicked(view: View) {
        requestBatteryOptimization(this)
    }

    private fun requestBatteryOptimization(activity: Activity) {
        val intent = Intent()
        val packageName = activity.packageName
        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = Uri.parse("package:$packageName")
        activity.startActivityForResult(intent,REQUEST_BATTERY_OPTIMIZATION)
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
        text_view_background_task_permission_title.highlightSmall()
        text_view_background_task_permission_text1.normalText()
        text_view_background_task_permission_text2.normalText()
        button_request_background_permission.button()
        window.decorView.setBackgroundColor(DriveKitUI.colors.backgroundViewColor())
    }
}
