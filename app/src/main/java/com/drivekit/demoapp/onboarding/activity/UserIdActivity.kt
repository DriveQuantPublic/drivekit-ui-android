package com.drivekit.demoapp.onboarding.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.drivekit.demoapp.manager.ModulesSyncListener
import com.drivekit.demoapp.manager.StepResultListener
import com.drivekit.demoapp.manager.SyncModuleManager
import com.drivekit.demoapp.onboarding.viewmodel.UserIdDriveKitListener
import com.drivekit.demoapp.onboarding.viewmodel.UserIdViewModel
import com.drivekit.demoapp.onboarding.viewmodel.getErrorMessage
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.driver.GetUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UserInfo
import com.drivequant.drivekit.core.driver.UserInfoGetStatus
import com.drivequant.drivekit.core.networking.RequestError
import kotlinx.android.synthetic.main.activity_set_user_id.*

class UserIdActivity : AppCompatActivity() {

    private val viewModel = UserIdViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_user_id)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        title = DKResource.convertToString(this, "authentication_header")

        text_view_user_id_description.text = viewModel.getDescription(this)
        text_view_user_id_title.apply {
            text = viewModel.getTitle(this@UserIdActivity)
            setOnClickListener {
                openDriveKitUserIdDoc()
            }
        }

        button_validate.setOnClickListener {
            validateUserId()
        }
    }

    private fun openDriveKitUserIdDoc() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(DKResource.convertToString(this@UserIdActivity, "drivekit_doc_android_user_id"))
            )
        )
    }

    private fun validateUserId() {
        val userId = text_view_user_id_field.editableText.toString()
        val isEditTextUserIdBlank = userId.isBlank()
        if (isEditTextUserIdBlank) {
            text_input_layout_user_id.apply {
                isErrorEnabled = true
                error = DKResource.convertToString(this@UserIdActivity, "user_id_error")
            }

        } else {
            text_input_layout_user_id.isErrorEnabled = false
            //TODO show loader
            viewModel.sendUserId(userId, object : UserIdDriveKitListener {
                override fun onSetUserId(status: Boolean, requestError: RequestError?) {
                    //TODO hide loader
                    if (status) {
                        //TODO show loader with message "sync_user_info_loading_message"
                        SyncModuleManager.syncModules(
                            mutableListOf(
                                SyncModuleManager.DKModule.USER_INFO,
                                SyncModuleManager.DKModule.VEHICLE,
                                SyncModuleManager.DKModule.WORKING_HOURS,
                                SyncModuleManager.DKModule.TRIPS), stepResultListener = object :StepResultListener {
                                override fun onStepFinished(
                                    syncStatus: SyncModuleManager.SyncStatus,
                                    remainingModules: List<SyncModuleManager.DKModule>) {
                                    remainingModules.firstOrNull()?.let {
                                        when (it) {
                                            SyncModuleManager.DKModule.VEHICLE -> "sync_vehicles_loading_message"
                                            SyncModuleManager.DKModule.WORKING_HOURS -> "sync_working_hours_loading_message"
                                            SyncModuleManager.DKModule.TRIPS -> "sync_trips_loading_message"
                                            else -> null
                                        }?.let { identifier ->
                                            val message =
                                                DKResource.convertToString(this@UserIdActivity, identifier)
                                            Log.e("OYYYYYY", message)
                                            //TODO show loader with message
                                        }
                                    }
                                }
                            }, listener = object: ModulesSyncListener {
                                override fun onModulesSyncResult(results: MutableList<SyncModuleManager.SyncStatus>) {
                                    if (results.isNotEmpty()) {
                                        if (results.first() == SyncModuleManager.SyncStatus.SUCCESS) {
                                            startUserInfoActivity(results.first())
                                        }
                                    }
                                }
                            }
                        )
                    } else {
                        val message = requestError?.getErrorMessage(this@UserIdActivity)
                        //TODO show error message
                        Toast.makeText(this@UserIdActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    private fun startUserInfoActivity(syncStatus: SyncModuleManager.SyncStatus) {
        if (syncStatus == SyncModuleManager.SyncStatus.SUCCESS) {
            SynchronizationType.CACHE
        } else {
            SynchronizationType.DEFAULT
        }.let {
            //TODO show loader
            DriveKit.getUserInfo(object : GetUserInfoQueryListener {
                override fun onResponse(status: UserInfoGetStatus, userInfo: UserInfo?) {
                    //TODO hide loader
                    //TODO Sync userInfo data from local / put userInfoViewModel inside activity with userInfo data
                    //TODO start UserInfo activity
                }
            }, it)
        }
    }
}