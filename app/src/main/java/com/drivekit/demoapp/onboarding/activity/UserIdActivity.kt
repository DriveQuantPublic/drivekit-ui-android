package com.drivekit.demoapp.onboarding.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivekit.demoapp.manager.*
import com.drivekit.demoapp.onboarding.viewmodel.UserIdDriveKitListener
import com.drivekit.demoapp.onboarding.viewmodel.UserIdViewModel
import com.drivekit.demoapp.onboarding.viewmodel.getErrorMessage
import com.drivekit.demoapp.utils.addInfoIconAtTheEnd
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.networking.RequestError
import kotlinx.android.synthetic.main.activity_set_user_id.*

internal class UserIdActivity : AppCompatActivity() {

    private lateinit var viewModel: UserIdViewModel

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, UserIdActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_user_id)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        title = getString( R.string.authentication_header)

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(UserIdViewModel::class.java)
        }

        text_view_user_id_description.text = getString(R.string.authentication_description)
        text_view_user_id_title.apply {
            text = getString(R.string.authentication_title)
            this.addInfoIconAtTheEnd(this@UserIdActivity)
            headLine1()

            setOnClickListener {
                openDriveKitUserIdDoc()
            }
        }

        button_validate.apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                validateUserId()
            }
        }

        viewModel.messageIdentifier.observe(this) {
            progress_bar_message.show(getString(it))
        }
        viewModel.syncStatus.observe(this) {
            syncUserInfo(it)
            progress_bar_message.hide()
        }

        viewModel.syncUserInfo.observe(this) {
            if (it) {
                UserInfoActivity.launchActivity(this@UserIdActivity)
            }
        }
    }

    private fun openDriveKitUserIdDoc() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.drivekit_doc_android_user_id))
            )
        )
    }

    private fun validateUserId() {
        val userId = text_view_user_id_field.editableText.toString()
        val isEditTextUserIdBlank = userId.isBlank()
        if (isEditTextUserIdBlank) {
            text_input_layout_user_id.apply {
                isErrorEnabled = true
                error = getString(R.string.user_id_error)
            }
        } else {
            text_input_layout_user_id.isErrorEnabled = false
            viewModel.sendUserId(userId, object : UserIdDriveKitListener {
                override fun onSetUserId(status: Boolean, requestError: RequestError?) {
                    if (status) {
                        progress_bar_message.show(getString(R.string.sync_user_info_loading_message))
                        viewModel.syncDriveKitModules()
                    } else {
                        progress_bar_message.hide()
                        val message = requestError?.getErrorMessage(this@UserIdActivity)
                        Toast.makeText(this@UserIdActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    private fun syncUserInfo(syncStatus: SyncStatus) {
        viewModel.getUserInfo(syncStatus)
    }

    override fun onBackPressed() {
        //Do nothing
    }
}