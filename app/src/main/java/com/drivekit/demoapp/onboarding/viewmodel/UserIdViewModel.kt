package com.drivekit.demoapp.onboarding.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivekit.demoapp.manager.DriveKitListenerManager
import com.drivekit.demoapp.manager.*
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.driver.GetUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UpdateUserIdStatus
import com.drivequant.drivekit.core.driver.UserInfo
import com.drivequant.drivekit.core.driver.UserInfoGetStatus
import com.drivequant.drivekit.core.networking.DriveKitListener
import com.drivequant.drivekit.core.networking.RequestError

internal class UserIdViewModel : ViewModel(), DriveKitListener {

    private var listener: UserIdDriveKitListener? = null
    var messageIdentifier: MutableLiveData<Int> = MutableLiveData()
    var syncStatus: MutableLiveData<SyncStatus> = MutableLiveData()
    var syncUserInfo: MutableLiveData<Boolean> = MutableLiveData()

    fun sendUserId(userId: String, listener: UserIdDriveKitListener) {
        DriveKitListenerManager.registerListener(this)
        DriveKit.setUserId(userId)
        this.listener = listener
    }

    override fun onConnected() {
        DriveKitListenerManager.unregisterListener(this)
        this.listener?.onSetUserId(true, null)
        this.listener = null
    }

    override fun onAuthenticationError(errorType: RequestError) {
        DriveKitListenerManager.unregisterListener(this)
        this.listener?.onSetUserId(false, errorType)
        this.listener = null
    }

    override fun onDisconnected() {
        DriveKitListenerManager.unregisterListener(this)
        this.listener?.onSetUserId(false, null)
        this.listener = null
    }

    override fun userIdUpdateStatus(status: UpdateUserIdStatus, userId: String?) {
        // do nothing
    }

    fun syncDriveKitModules() {
        SyncModuleManager.syncModules(
            mutableListOf(
                DKModule.USER_INFO,
                DKModule.VEHICLE,
                DKModule.WORKING_HOURS,
                DKModule.TRIPS), stepResultListener = object : StepResultListener {
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
                            messageIdentifier.postValue(identifier)
                        }
                    }
                }
            }, listener = object: ModulesSyncListener {
                override fun onModulesSyncResult(results: MutableList<SyncStatus>) {
                    if (results.isNotEmpty()) {
                        if (results.first() == SyncStatus.SUCCESS) {
                            syncStatus.postValue(results.first())
                        }
                    }
                }
            }
        )
    }

    fun getUserInfo(syncStatus: SyncStatus) {
        if (syncStatus == SyncStatus.SUCCESS) {
            SynchronizationType.CACHE
        } else {
            SynchronizationType.DEFAULT
        }.let {
            DriveKit.getUserInfo(object : GetUserInfoQueryListener {
                override fun onResponse(status: UserInfoGetStatus, userInfo: UserInfo?) {
                    syncUserInfo.postValue(status == UserInfoGetStatus.SUCCESS || status == UserInfoGetStatus.CACHE_DATA_ONLY)
                }
            }, it)
        }
    }
}

interface UserIdDriveKitListener {
    fun onSetUserId(status: Boolean, requestError: RequestError?)
}

fun RequestError.getErrorMessage(context: Context) = when (this) {
    RequestError.NO_NETWORK -> R.string.network_ko_error
    RequestError.UNAUTHENTICATED -> R.string.authentication_error
    RequestError.FORBIDDEN -> R.string.forbidden_error
    RequestError.SERVER_ERROR -> R.string.server_error
    RequestError.CLIENT_ERROR -> R.string.client_error
    RequestError.UNKNOWN_ERROR -> R.string.unknown_error
    RequestError.LIMIT_REACHED -> R.string.limit_reached_error
}.let {
    context.getString(it)
}
