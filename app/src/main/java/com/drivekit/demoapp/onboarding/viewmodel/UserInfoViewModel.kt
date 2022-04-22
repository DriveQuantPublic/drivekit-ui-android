package com.drivekit.demoapp.onboarding.viewmodel

import android.content.Context
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.driver.GetUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UpdateUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UserInfo
import com.drivequant.drivekit.core.driver.UserInfoGetStatus
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.core.utils.PermissionStatus
import com.drivequant.drivekit.core.utils.PermissionType
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.manager.VehicleListQueryListener
import com.drivequant.drivekit.vehicle.manager.VehicleSyncStatus

class UserInfoViewModel: ViewModel() {

    private var userInfo: UserInfo? = null
    var shouldDisplayVehicle: MutableLiveData<Boolean> = MutableLiveData()
    var userInfoUpdated: MutableLiveData<Boolean> = MutableLiveData()

    init {
        DriveKit.getUserInfo(object : GetUserInfoQueryListener {
            override fun onResponse(status: UserInfoGetStatus, userInfo: UserInfo?) {
                this@UserInfoViewModel.userInfo = userInfo
            }
        }, SynchronizationType.CACHE)
    }

    fun getTitle(context: Context) = DKSpannable().append(
        context.getString(R.string.user_info_title), context.resSpans {
            color(DriveKitUI.colors.mainFontColor())
        }).append(" ").append("ⓘ", context.resSpans {
        color(DriveKitUI.colors.secondaryColor())
    }).toSpannable()

    fun getDescriptionContent(context: Context) = context.getString(R.string.user_info_description)

    fun updateUser(firstName: String, lastName: String, pseudo: String) {
        DriveKit.updateUserInfo(firstName, lastName, pseudo, object : UpdateUserInfoQueryListener {
            override fun onResponse(status: Boolean) {
                if (status) {
                    this@UserInfoViewModel.userInfo = UserInfo(firstName, lastName, pseudo)
                }
                userInfoUpdated.postValue(status)
            }
        })
    }

    fun getFirstName() = userInfo?.firstname

    fun getLastName() = userInfo?.lastname

    fun getPseudo() = userInfo?.pseudo

    fun shouldDisplayPermissions(context: Context): Boolean {
        listOf(PermissionType.ACTIVITY, PermissionType.LOCATION, PermissionType.NEARBY).forEach {
            if (DiagnosisHelper.getPermissionStatus(context, it) == PermissionStatus.NOT_VALID)
                return true
        }

        if (DiagnosisHelper.getBatteryOptimizationsStatus(context) == PermissionStatus.NOT_VALID
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return true
        }
        return false
    }

    fun shouldDisplayVehicle() {
        DriveKitVehicle.getVehiclesOrderByNameAsc(object : VehicleListQueryListener {
            override fun onResponse(status: VehicleSyncStatus, vehicles: List<Vehicle>) {
                shouldDisplayVehicle.postValue(vehicles.isEmpty())
            }
        }, SynchronizationType.CACHE)
    }
}