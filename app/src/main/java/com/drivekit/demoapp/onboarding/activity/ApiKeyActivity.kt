package com.drivekit.demoapp.onboarding.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.drivekit.demoapp.onboarding.viewmodel.ApiKeyViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import kotlinx.android.synthetic.main.activity_set_api_key.*

internal class ApiKeyActivity : AppCompatActivity() {

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, ApiKeyActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_api_key)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        title = getString(R.string.welcome_header)

        val viewModel = ApiKeyViewModel()
        text_view_description.apply {
            text = viewModel.getDescription(this@ApiKeyActivity)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
        text_view_title.apply {
            text = getString(viewModel.getTitle())
            headLine1()
        }
        button_next.findViewById<Button>(R.id.button_action).apply {
            text = getString(viewModel.getButtonText())
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                if (viewModel.isApiKeyValid()) {
                    UserIdActivity.launchActivity(this@ApiKeyActivity)
                } else {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                getString(R.string.drivekit_doc_android_github_ui)
                            )
                        )
                    )
                }
            }
        }
    }
}