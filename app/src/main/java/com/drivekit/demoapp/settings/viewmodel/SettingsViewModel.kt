package com.drivekit.demoapp.settings.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivekit.demoapp.config.DriveKitConfig
import com.drivekit.demoapp.settings.enum.UserInfoType
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.driver.GetUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UpdateUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UserInfo
import com.drivequant.drivekit.core.driver.UserInfoGetStatus

internal class SettingsViewModel: ViewModel() {

    var logoutLiveData = MutableLiveData<Any>()
        private set

    var updateUserInfoLiveData = MutableLiveData<Boolean>()
        private set

    fun getUserId() = DriveKit.config.userId

    fun getUserInfo(listener: GetUserInfoQueryListener) = DriveKit.getUserInfo(object : GetUserInfoQueryListener {
        override fun onResponse(status: UserInfoGetStatus, userInfo: UserInfo?) {
            listener.onResponse(status, userInfo)
        }
    }, SynchronizationType.CACHE)

    fun updateUserInfo(type: UserInfoType, data: String) {
        var firstName: String? = null
        var lastName: String? = null
        var pseudo: String? = null
        when (type) {
            UserInfoType.FIRST_NAME -> firstName = data
            UserInfoType.LAST_NAME -> lastName = data
            UserInfoType.PSEUDO -> pseudo = data
        }
        DriveKit.updateUserInfo(firstName, lastName, pseudo, object : UpdateUserInfoQueryListener {
            override fun onResponse(status: Boolean) {
                updateUserInfoLiveData.postValue(status)
            }
        })
    }

    fun isAutoStartEnabled(context: Context) = DriveKitConfig.isTripAnalysisAutoStartedEnabled(context)

    fun activateAutoStart(context: Context, activate: Boolean) = DriveKitConfig.enableTripAnalysisAutoStart(context, activate)

    fun logout(context: Context) {
        DriveKitConfig.logout(context)
        logoutLiveData.postValue(Any())
    }
}
