package com.drivekit.demoapp.onboarding.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.drivekit.demoapp.onboarding.viewmodel.ApiKeyViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivekit.drivekitdemoapp.databinding.ActivitySetApiKeyBinding
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager

internal class ApiKeyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetApiKeyBinding

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, ApiKeyActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySetApiKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.root.findViewById(com.drivequant.drivekit.vehicle.ui.R.id.dk_toolbar))
        setActivityTitle(getString(R.string.welcome_header))

        val viewModel = ApiKeyViewModel()
        binding.textViewDescription.text = viewModel.getDescription(this@ApiKeyActivity)
        binding.textViewTitle.setText(viewModel.getTitleResId())
        binding.root.findViewById<Button>(R.id.button_action).apply {
            setText(viewModel.getButtonTextResId())
            setOnClickListener {
                if (viewModel.isApiKeyValid()) {
                    UserIdActivity.launchActivity(this@ApiKeyActivity)
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.drivekit_doc_android_github_ui))))
                }
            }
        }

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(binding.root) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(com.drivequant.drivekit.ui.R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
        }
    }
}
