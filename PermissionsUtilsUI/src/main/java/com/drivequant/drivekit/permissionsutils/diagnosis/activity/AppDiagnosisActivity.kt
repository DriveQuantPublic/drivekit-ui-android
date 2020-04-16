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

class AppDiagnosisActivity : RequestPermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_diagnosis)
        setStyle()
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
}
