package com.drivekit.demoapp.onboarding.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivekit.demoapp.onboarding.viewmodel.ApiKeyViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.activity_set_api_key.*

class ApiKeyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_api_key)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        title = DKResource.convertToString(this, "welcome_header")

        val viewModel = ApiKeyViewModel()
        text_view_description.text = viewModel.getDescription(this)
        text_view_title.text = viewModel.getTitle(this)

        button_next.apply {
            text = viewModel.getButtonText(context)
            setOnClickListener {
                if (viewModel.isApiKeyValid()) {
                    startActivity(Intent(this@ApiKeyActivity, UserIdActivity::class.java))
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DKResource.convertToString(this@ApiKeyActivity, "drivekit_doc_android_github_ui"))))
                }
            }
        }
    }
}