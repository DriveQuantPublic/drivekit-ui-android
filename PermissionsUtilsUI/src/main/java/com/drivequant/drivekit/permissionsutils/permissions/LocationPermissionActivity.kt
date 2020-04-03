package com.drivequant.drivekit.permissionsutils.permissions

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.drivequant.drivekit.common.ui.extension.button
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.permissionsutils.R

class LocationPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_permission)

        val textViewLocationPermissionTitle = findViewById<TextView>(R.id.text_view_permission_location_title)
        val textViewLocationPermissionText1 = findViewById<TextView>(R.id.text_view_location_permission_text1)
        val textViewLocationPermissionText2 = findViewById<TextView>(R.id.text_view_location_permission_text2)
        val buttonLocationPermissionRequest = findViewById<Button>(R.id.button_request_location_permission)

        textViewLocationPermissionTitle.headLine1()
        textViewLocationPermissionText1.normalText()
        textViewLocationPermissionText2.normalText()
        buttonLocationPermissionRequest.button()

        buttonLocationPermissionRequest.setOnClickListener {
            next()
        }
    }
}
