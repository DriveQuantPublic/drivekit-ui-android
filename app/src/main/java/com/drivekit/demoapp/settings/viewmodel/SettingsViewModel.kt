package com.drivekit.demoapp.settings.viewmodel

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivekit.demoapp.notification.controller.DKNotificationManager
import com.drivekit.demoapp.settings.enum.UserInfoType
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.driver.GetUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UpdateUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UserInfo
import com.drivequant.drivekit.core.driver.UserInfoGetStatus
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.TripAnalysisConfig
import com.drivequant.drivekit.vehicle.DriveKitVehicle

internal class SettingsViewModel: ViewModel() {

    var logoutLiveData = MutableLiveData<Any>()
        private set

    var updateUserInfoLiveData = MutableLiveData<Any>()
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
                updateUserInfoLiveData.postValue(Any())
            }
        })
    }

    fun isAutoStartEnabled() = TripAnalysisConfig.autostart

    fun activateAutoStart(activate: Boolean) = DriveKitTripAnalysis.activateAutoStart(activate)

    fun reset(context: Context) {
        // RESET DRIVEKIT
        DriveKit.reset()
        DriveKitTripAnalysis.reset()
        DriveKitVehicle.reset()
        DriveKitDriverData.reset()
        DriveKitChallenge.reset()
        DriveKitDriverAchievement.reset()

        // DELETE NOTIFICATION CHANNELS
        DKNotificationManager.deleteChannels(context)

        // CLEAR SHARED PREFERENCES OF THE DEMO APP
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().clear().apply()

        logoutLiveData.postValue(Any())
    }
}
