package com.drivekit.demoapp.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.driver.GetUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UserInfo
import com.drivequant.drivekit.core.driver.UserInfoGetStatus

internal class SettingsViewModel: ViewModel() {

    fun getUserId() = DriveKit.config.userId

    fun getUserInfo(listener: GetUserInfoQueryListener) = DriveKit.getUserInfo(object : GetUserInfoQueryListener {
        override fun onResponse(status: UserInfoGetStatus, userInfo: UserInfo?) {
            listener.onResponse(status, userInfo)
        }
    }, SynchronizationType.CACHE)
}
