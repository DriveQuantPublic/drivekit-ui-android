package com.drivequant.drivekit.permissionsutils.permissions

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.permissionsutils.R

class ActivityRecognitionPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognition_permission)

        val textViewActivityPermissionTitle = findViewById<TextView>(R.id.text_view_activity_permission_title)
        val textViewActivityPermissionText = findViewById<TextView>(R.id.text_view_activity_permission_text)
        val buttonActivityPermissionRequest = findViewById<Button>(R.id.button_request_activity_permission)

        textViewActivityPermissionTitle.headLine1()
        textViewActivityPermissionText.normalText()
        buttonActivityPermissionRequest.button()

        buttonActivityPermissionRequest.setOnClickListener {
            next()
        }
    }
}
