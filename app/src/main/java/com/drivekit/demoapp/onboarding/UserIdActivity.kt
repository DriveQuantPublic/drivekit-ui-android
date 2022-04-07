package com.drivekit.demoapp.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit
import kotlinx.android.synthetic.main.activity_set_user_id.*

class UserIdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_user_id)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        title = DKResource.convertToString(this, "create_user_menu")

        button_validate.setOnClickListener {
            val userId = text_view_user_id_field.editableText.toString()
            val isEditTextUserIdBlank = userId.isBlank()
            if (isEditTextUserIdBlank) {
                text_input_layout_user_id.apply {
                    isErrorEnabled = true
                    //error = DKResource.convertToString(context, "")
                    //TODO string key
                    error = "Veuillez renseigner votre identifiant"
                }

            } else {
                text_input_layout_user_id.isErrorEnabled = false
                DriveKit.setUserId(userId)
            }
        }
    }
}