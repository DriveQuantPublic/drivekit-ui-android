package com.drivekit.demoapp.onboarding.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.onboarding.viewmodel.UserInfoViewModel
import com.drivekit.demoapp.utils.addInfoIconAtTheEnd
import com.drivekit.drivekitdemoapp.R
import com.drivekit.drivekitdemoapp.databinding.ActivityUserInfoBinding
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager

internal class UserInfoActivity : AppCompatActivity() {

    private lateinit var viewModel: UserInfoViewModel
    private lateinit var binding: ActivityUserInfoBinding

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, UserInfoActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.root.findViewById(com.drivequant.drivekit.common.ui.R.id.dk_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]
        }

        setActivityTitle(getString(R.string.user_info_header))
        binding.textViewUserInfoTitle.apply {
            text = getString(R.string.user_info_title)
            headLine1()
            addInfoIconAtTheEnd(this@UserInfoActivity)
        }
        binding.textViewUserInfoDescription.apply {
            text = getString(R.string.user_info_description)
            normalText()
        }

        binding.textInputLayoutFirstname.editText?.setText(viewModel.getFirstName())
        binding.textInputLayoutLastname.editText?.setText(viewModel.getLastName())
        binding.textInputLayoutPseudo.editText?.setText(viewModel.getPseudo())

        binding.textViewUserInfoTitle.setOnClickListener {
            openDriveKitUserInfoDoc()
        }

        val nextStepButton = binding.buttonNextStep
        nextStepButton.setOnClickListener {
            goToNext()
        }

        binding.root.findViewById<Button>(R.id.button_action).apply {
            text = getString(com.drivequant.drivekit.common.ui.R.string.dk_common_validate)
            setOnClickListener {
                val firstName = binding.textViewFirstnameField.editableText.toString()
                val lastName = binding.textViewLastnameField.editableText.toString()
                val pseudo = binding.textViewPseudoField.editableText.toString()
                updateProgressVisibility(true)
                viewModel.updateUser(firstName, lastName, pseudo)
            }
        }

        viewModel.userInfoUpdated.observe(this) {
            if (it) {
                goToNext()
            }
            updateProgressVisibility(false)
        }

        viewModel.shouldDisplayVehicle.observe(this) {
            if (it) {
                VehiclesActivity.launchActivity(this)
            } else {
                DashboardActivity.launchActivity(this)
            }
        }

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            addInsetsPaddings(findViewById(com.drivequant.drivekit.ui.R.id.toolbar))
            addInsetsMargins(nextStepButton)
        }
    }

    private fun goToNext() {
        if (viewModel.shouldDisplayPermissions(this)) {
            PermissionsActivity.launchActivity(this)
        } else {
            viewModel.fetchLocalVehicles()
        }
    }

    private fun openDriveKitUserInfoDoc() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(this.getString(R.string.drivekit_doc_android_update_user_info))))
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        binding.progressCircular.visibility = if (displayProgress) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onBackPressed() {
        viewModel.logout(this)
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
