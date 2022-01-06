package com.drivequant.drivekit.tripanalysis.workinghours.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysisUI
import com.drivequant.drivekit.tripanalysis.service.workinghours.*

internal class WorkingHoursViewModel : ViewModel() {
    var dataChanged: Boolean = false
    var config: DKWorkingHours? = null
    var syncDataStatus: MutableLiveData<Boolean> = MutableLiveData()
        private set
    var updateDataStatus: MutableLiveData<UpdateDataStatus> = MutableLiveData()
        private set

    fun synchronizeData() {
        if (DriveKit.isConfigured()) {
            DriveKitTripAnalysis.getWorkingHours(object : SyncWorkingHoursQueryListener {
                override fun onResponse(
                    status: SyncWorkingHoursStatus,
                    workingHours: DKWorkingHours?
                ) {
                    val value = when (status) {
                        SyncWorkingHoursStatus.SUCCESS -> true
                        SyncWorkingHoursStatus.FAILED_TO_SYNC_CACHE_ONLY -> false
                    }
                    config = if (workingHours?.dayConfiguration?.isNullOrEmpty() == true) {
                        DriveKitTripAnalysisUI.defaultWorkHoursConfig
                    } else {
                        workingHours
                    }
                    syncDataStatus.postValue(value)
                }
            })
        }
    }

    fun updateConfig(
        enable: Boolean,
        insideHours: TripStatus,
        outsideHours: TripStatus,
        days: List<DKWorkingHoursDayConfiguration>,
        back: Boolean = false
    ) {
        if (DriveKit.isConfigured()) {
            val data = DKWorkingHours(
                enable = enable,
                insideHours = insideHours,
                outsideHours = outsideHours,
                dayConfiguration = days.toMutableList()
            )
            DriveKitTripAnalysis.updateWorkingHours(data,
                object : UpdateWorkingHoursQueryListener {
                    override fun onResponse(status: UpdateWorkingHoursStatus) {
                        val success = when (status) {
                            UpdateWorkingHoursStatus.SUCCESS -> true
                            UpdateWorkingHoursStatus.FAILED,
                            UpdateWorkingHoursStatus.INVALID_DAY_OF_WEEK,
                            UpdateWorkingHoursStatus.INVALID_START_OR_END_TIME,
                            UpdateWorkingHoursStatus.START_TIME_GREATER_THAN_END_TIME,
                            UpdateWorkingHoursStatus.DUPLICATED_DAY,
                            UpdateWorkingHoursStatus.INVALID_DAY_COUNT -> false
                        }
                        updateDataStatus.postValue(UpdateDataStatus(success, back))
                    }
                })
        }
    }
}

internal data class UpdateDataStatus(
    val status: Boolean,
    val fromBackButton: Boolean
)