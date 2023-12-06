package com.drivekit.demoapp.onboarding.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.drivekit.demoapp.onboarding.viewmodel.ApiKeyViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivekit.drivekitdemoapp.databinding.ActivitySetApiKeyBinding
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1

internal class ApiKeyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetApiKeyBinding

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, ApiKeyActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetApiKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.root.findViewById(com.drivequant.drivekit.vehicle.ui.R.id.dk_toolbar))
        title = getString(R.string.welcome_header)

        val viewModel = ApiKeyViewModel()
        binding.textViewDescription.text = viewModel.getDescription(this@ApiKeyActivity)
        binding.textViewTitle.apply {
            text = getString(viewModel.getTitle())
            headLine1()
        }
        binding.root.findViewById<Button>(R.id.button_action).apply {
            text = getString(viewModel.getButtonText())
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                if (viewModel.isApiKeyValid()) {
                    UserIdActivity.launchActivity(this@ApiKeyActivity)
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.drivekit_doc_android_github_ui))))
                }
            }
        }
    }
}
