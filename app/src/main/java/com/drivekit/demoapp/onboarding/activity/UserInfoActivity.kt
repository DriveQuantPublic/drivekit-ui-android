package com.drivekit.demoapp.onboarding.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.onboarding.viewmodel.UserInfoViewModel
import com.drivekit.demoapp.utils.addInfoIconAtTheEnd
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.activity_user_info.button_validate
import kotlinx.android.synthetic.main.activity_user_info.progress_circular

internal class UserInfoActivity : AppCompatActivity() {

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
        text_view_user_info_title.apply {
            text = getString(R.string.user_info_title)
            headLine1()
            addInfoIconAtTheEnd(this@UserInfoActivity)
        }

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

        button_validate.findViewById<Button>(R.id.button_action).apply {
            text = getString(R.string.dk_common_validate)
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                val firstName = text_view_firstname_field.editableText.toString()
                val lastName = text_view_lastname_field.editableText.toString()
                val pseudo = text_view_pseudo_field.editableText.toString()
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
        progress_circular?.apply {
            visibility = if (displayProgress) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    override fun onBackPressed() {
        viewModel.resetDriveKit(this)
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}