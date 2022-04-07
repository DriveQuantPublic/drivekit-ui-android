package com.drivekit.demoapp.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import kotlinx.android.synthetic.main.activity_set_user_id.*

class UserIdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_user_id)

        button_validate.setOnClickListener {
            val userId = text_view_user_id_field.editableText.toString()
            val isEditTextUserIdBlank = userId.isBlank()
            if (isEditTextUserIdBlank) {
                text_input_layout_user_id.apply {
                    isErrorEnabled = true
                    //error = DKResource.convertToString(context, "")
                    //TODO Create string key
                    error = "Veuillez renseigner votre identifiant"
                }

            } else {
                text_input_layout_user_id.isErrorEnabled = false
                reconfigureDriveKit(userId)
            }
        }
    }

    private fun reconfigureDriveKit(userId: String) {
        val apiKey = DriveKit.config.apiKey
        DriveKit.reset()
        DriveKitDriverData.reset()
        DriveKitVehicle.reset()
        DriveKitDriverAchievement.reset()
        DriveKitChallenge.reset()
        apiKey?.let {
            DriveKit.setApiKey(it)
        }
        DriveKit.setUserId(userId)
    }
}