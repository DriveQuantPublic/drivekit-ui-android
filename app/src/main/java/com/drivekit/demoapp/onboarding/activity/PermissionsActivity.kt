package com.drivekit.demoapp.onboarding.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.onboarding.viewmodel.PermissionsViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.permissions.listener.PermissionViewListener
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView
import kotlinx.android.synthetic.main.activity_permissions.*

class PermissionsActivity : AppCompatActivity() {

    private val viewModel = PermissionsViewModel()

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, PermissionsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)

        title = getString(R.string.permissions_intro_header)

        text_view_title.apply {
            text = DKSpannable().append(
                context.getString(R.string.permissions_intro_title), resSpans {
                    color(DriveKitUI.colors.mainFontColor())
                    size(R.dimen.dk_text_medium)
                }).append(" ").append("ⓘ", resSpans {
                color(DriveKitUI.colors.secondaryColor())
                size(R.dimen.dk_text_medium)
            }).toSpannable()

            setOnClickListener {
                openDriveKitPermissionsDoc()
            }
        }
        text_view_description.text = getString(R.string.permissions_intro_description)
        button_request_permissions.apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                val permissions: ArrayList<PermissionView> = ArrayList()
                permissions.addAll(PermissionView.values())
                PermissionsUtilsUI.showPermissionViews(
                    this@PermissionsActivity,
                    permissions,
                    object : PermissionViewListener {
                        override fun onFinish() {
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
    }

    private fun openDriveKitPermissionsDoc() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.drivekit_doc_android_permission_management))
            )
        )
    }
}