package com.drivekit.demoapp.onboarding.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.drivekit.demoapp.manager.SyncStatus
import com.drivekit.demoapp.onboarding.viewmodel.UserIdDriveKitListener
import com.drivekit.demoapp.onboarding.viewmodel.UserIdViewModel
import com.drivekit.demoapp.onboarding.viewmodel.getErrorMessage
import com.drivekit.demoapp.utils.addInfoIconAtTheEnd
import com.drivekit.drivekitdemoapp.R
import com.drivekit.drivekitdemoapp.databinding.ActivitySetUserIdBinding
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.core.networking.RequestError

class UserIdActivity : AppCompatActivity() {

    private lateinit var viewModel: UserIdViewModel
    private lateinit var binding: ActivitySetUserIdBinding

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, UserIdActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUserIdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.dkToolbar)
        title = getString( R.string.authentication_header)

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this)[UserIdViewModel::class.java]
        }

        binding.textViewUserIdDescription.apply {
            text = getString(R.string.authentication_description)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
        binding.textViewUserIdTitle.apply {
            text = getString(R.string.authentication_title)
            this.addInfoIconAtTheEnd(this@UserIdActivity)
            headLine1()
            setOnClickListener {
                openDriveKitUserIdDoc()
            }
        }
        binding.buttonValidate.buttonAction.apply {
            text = getString(R.string.dk_common_validate)
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                validateUserId()
            }
        }

        viewModel.messageIdentifier.observe(this) {
            binding.progressBarMessage.show(getString(it))
        }
        viewModel.syncStatus.observe(this) {
            syncUserInfo(it)
            binding.progressBarMessage.hide()
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
        val userId = binding.textViewUserIdField.editableText.toString()
        val isEditTextUserIdBlank = userId.isBlank()
        if (isEditTextUserIdBlank) {
            binding.textInputLayoutUserId.apply {
                isErrorEnabled = true
                error = getString(R.string.user_id_error)
            }
        } else {
            binding.textInputLayoutUserId.isErrorEnabled = false
            binding.progressBarMessage.show(null)
            viewModel.sendUserId(userId, object : UserIdDriveKitListener {
                override fun onSetUserId(status: Boolean, requestError: RequestError?) {
                    if (status) {
                        binding.progressBarMessage.show(getString(R.string.sync_user_info_loading_message))
                        viewModel.syncDriveKitModules()
                    } else {
                        binding.progressBarMessage.hide()
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
