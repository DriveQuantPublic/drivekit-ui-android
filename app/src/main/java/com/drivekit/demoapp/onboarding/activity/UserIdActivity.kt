package com.drivekit.demoapp.onboarding.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.drivekit.demoapp.manager.*
import com.drivekit.demoapp.onboarding.viewmodel.UserIdDriveKitListener
import com.drivekit.demoapp.onboarding.viewmodel.UserIdViewModel
import com.drivekit.demoapp.onboarding.viewmodel.getErrorMessage
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
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
        title = getString( R.string.authentication_header)

        text_view_user_id_description.text = getString(R.string.authentication_description)
        text_view_user_id_title.apply {
            text = DKSpannable().append(
                getString(R.string.authentication_title), resSpans {
                    color(DriveKitUI.colors.mainFontColor())
                    size(R.dimen.dk_text_medium)
                }).append(" ").append("ⓘ", resSpans {
                color(DriveKitUI.colors.secondaryColor())
                size(R.dimen.dk_text_medium)
            }).toSpannable()

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
                        SyncModuleManager.syncModules(
                            mutableListOf(
                                DKModule.USER_INFO,
                                DKModule.VEHICLE,
                                DKModule.WORKING_HOURS,
                                DKModule.TRIPS), stepResultListener = object :StepResultListener {
                                override fun onStepFinished(
                                    syncStatus: SyncStatus,
                                    remainingModules: List<DKModule>) {
                                    remainingModules.firstOrNull()?.let {
                                        when (it) {
                                            DKModule.VEHICLE -> R.string.sync_vehicles_loading_message
                                            DKModule.WORKING_HOURS ->  R.string.sync_working_hours_loading_message
                                            DKModule.TRIPS ->  R.string.sync_trips_loading_message
                                            else -> null
                                        }?.let { identifier ->
                                            val message = getString(identifier)
                                            progress_bar_message.show(message)
                                        }
                                    }
                                }
                            }, listener = object: ModulesSyncListener {
                                override fun onModulesSyncResult(results: MutableList<SyncStatus>) {
                                    if (results.isNotEmpty()) {
                                        if (results.first() == SyncStatus.SUCCESS) {
                                            progress_bar_message.hide()
                                            startUserInfoActivity(results.first())
                                        }
                                    }
                                }
                            }
                        )
                    } else {
                        progress_bar_message.hide()
                        val message = requestError?.getErrorMessage(this@UserIdActivity)
                        Toast.makeText(this@UserIdActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    private fun startUserInfoActivity(syncStatus: SyncStatus) {
        if (syncStatus == SyncStatus.SUCCESS) {
            SynchronizationType.CACHE
        } else {
            SynchronizationType.DEFAULT
        }.let {
            //TODO show loader
            DriveKit.getUserInfo(object : GetUserInfoQueryListener {
                override fun onResponse(status: UserInfoGetStatus, userInfo: UserInfo?) {
                    startActivity(Intent(this@UserIdActivity, UserInfoActivity::class.java))
                    //TODO hide loader
                    //TODO Sync userInfo data from local / put userInfoViewModel inside activity with userInfo data
                    //TODO start UserInfo activity
                }
            }, it)
        }
    }
}