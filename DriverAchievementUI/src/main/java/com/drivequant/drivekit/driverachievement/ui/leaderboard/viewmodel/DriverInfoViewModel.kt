package com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.driver.UpdateUserInfosQueryListener

class DriverInfoViewModel : ViewModel() {

    var syncStatus: MutableLiveData<Boolean> = MutableLiveData()

    fun updateUserInfo(nickname: String) {
        DriveKit.updateUserInfos(nickname, listener = object :UpdateUserInfosQueryListener{
            override fun onResponse(status: Boolean) {
                if (status) {
                    syncStatus.postValue(status)
                }
            }
        })
    }
}