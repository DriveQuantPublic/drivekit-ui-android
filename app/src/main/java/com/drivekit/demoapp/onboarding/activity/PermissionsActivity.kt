package com.drivekit.demoapp.onboarding.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.onboarding.viewmodel.PermissionsViewModel
import com.drivekit.demoapp.utils.addInfoIconAtTheEnd
import com.drivekit.drivekitdemoapp.R
import com.drivekit.drivekitdemoapp.databinding.ActivityPermissionsBinding
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.permissions.listener.PermissionViewListener

internal class PermissionsActivity : AppCompatActivity() {

    private val viewModel = PermissionsViewModel()
    private lateinit var binding: ActivityPermissionsBinding

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, PermissionsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.root.findViewById(com.drivequant.drivekit.common.ui.R.id.dk_toolbar))

        setActivityTitle(getString(R.string.permissions_intro_header))

        binding.textViewTitle.apply {
            text = getString(R.string.permissions_intro_title)
            headLine1()
            addInfoIconAtTheEnd(this@PermissionsActivity)
            setOnClickListener {
                openDriveKitPermissionsDoc()
            }
        }
        binding.textViewDescription.apply {
            text = getString(R.string.permissions_intro_description)
            normalText()
        }

        val actionButton = binding.root.findViewById<Button>(R.id.button_action)
        actionButton.apply {
            text = getString(R.string.permissions_intro_button)
            setOnClickListener {
                PermissionsUtilsUI.showPermissionViews(
                    this@PermissionsActivity, object : PermissionViewListener {
                        override fun onFinish() {
                            viewModel.configureDriveKit(this@PermissionsActivity)
                            viewModel.shouldDisplayVehicle()
                        }
                    })
            }
        }

        viewModel.shouldDisplayVehicle.observe(this) { displayVehicle ->
            if (displayVehicle) {
                VehiclesActivity.launchActivity(this)
            } else {
                DashboardActivity.launchActivity(this)
            }
        }

        DKEdgeToEdgeManager.apply {
            addSystemStatusBarTopPadding(findViewById(com.drivequant.drivekit.ui.R.id.toolbar))
            addSystemNavigationBarBottomMargin(actionButton)
        }
    }

    private fun openDriveKitPermissionsDoc() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.drivekit_doc_android_permissions_management))
            )
        )
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onBackPressed() {
        //Do nothing
    }
}
