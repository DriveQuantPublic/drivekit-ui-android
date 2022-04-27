package com.drivekit.demoapp.onboarding.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.manager.DriveKitListenerManager
import com.drivekit.demoapp.onboarding.viewmodel.UserInfoViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.DriveKit
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.activity_user_info.button_validate
import kotlinx.android.synthetic.main.activity_user_info.progress_circular

class UserInfoActivity : AppCompatActivity() {

    private lateinit var viewModel: UserInfoViewModel

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, UserInfoActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(UserInfoViewModel::class.java)
        }

        title = getString(R.string.user_info_header)
        text_view_user_info_title.text = DKSpannable().append(
            getString(R.string.user_info_title), resSpans {
                color(DriveKitUI.colors.mainFontColor())
                size(R.dimen.dk_text_medium)
            }).append(" ").append("ⓘ", resSpans {
            color(DriveKitUI.colors.secondaryColor())
            size(R.dimen.dk_text_medium)
        }).toSpannable()

        text_view_user_info_description.text = getString(R.string.user_info_description)

        text_input_layout_firstname.editText?.setText(viewModel.getFirstName())
        text_input_layout_lastname.editText?.setText(viewModel.getLastName())
        text_input_layout_pseudo.editText?.setText(viewModel.getPseudo())

        text_view_user_info_title.setOnClickListener {
            openDriveKitUserInfoDoc()
        }

        button_next_step.setOnClickListener {
            goToNext()
        }

        button_validate.setOnClickListener {
            val firstName = text_view_firstname_field.editableText.toString()
            val lastName = text_view_lastname_field.editableText.toString()
            val pseudo = text_view_pseudo_field.editableText.toString()

            if (firstName.isNotBlank() ||
                lastName.isNotBlank() ||
                pseudo.isNotBlank()
            ) {
                text_input_layout_firstname.isErrorEnabled = false
                text_input_layout_lastname.isErrorEnabled = false
                text_input_layout_pseudo.isErrorEnabled = false
                updateProgressVisibility(true)
                viewModel.updateUser(firstName, lastName, pseudo)
            } else {
                text_input_layout_firstname.apply {
                    isErrorEnabled = true
                    error = getString(R.string.empty_firstname_error)
                }
                text_input_layout_lastname.apply {
                    isErrorEnabled = true
                    error = getString(R.string.empty_lastname_error)
                }
                text_input_layout_pseudo.apply {
                    isErrorEnabled = true
                    error = getString(R.string.empty_pseudo_error)
                }
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
    }

    private fun goToNext() {
        if (viewModel.shouldDisplayPermissions(this)) {
            PermissionsActivity.launchActivity(this)
        } else {
            viewModel.fetchLocalVehicles()
        }
    }

    private fun openDriveKitUserInfoDoc() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    this.getString(R.string.drivekit_doc_android_update_user_info)
                )
            )
        )
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        progress_circular?.apply {
            visibility = if (displayProgress) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        DriveKitListenerManager.reset()
        DriveKit.config.apiKey?.let {
            DriveKit.setApiKey(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}