package com.drivequant.drivekit.tripanalysis.workinghours.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysisUI
import com.drivequant.drivekit.tripanalysis.service.workinghours.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal class WorkingHoursViewModel : ViewModel() {

    var config: DKWorkingHours? = null
    var updatedDaysConfig: MutableList<DKWorkingHoursDayConfiguration>? = mutableListOf()
    var syncDataStatus: MutableLiveData<Boolean> = MutableLiveData()
        private set
    var updateDataStatus: MutableLiveData<Boolean> = MutableLiveData()
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
                    config = workingHours
                    updatedDaysConfig = workingHours?.dayConfiguration?.toMutableList()
                    syncDataStatus.postValue(value)
                }
            })
        }
    }

    fun updateConfig(enable: Boolean, insideHours: TripStatus, outsideHours: TripStatus) {
        if (DriveKit.isConfigured()) {
            buildData(enable, insideHours, outsideHours)?.let {
                if (it != config) {
                    DriveKitTripAnalysis.updateWorkingHours(it,
                        object : UpdateWorkingHoursQueryListener {
                            override fun onResponse(status: UpdateWorkingHoursStatus) {
                                val value = when (status) {
                                    UpdateWorkingHoursStatus.SUCCESS -> true
                                    UpdateWorkingHoursStatus.FAILED,
                                    UpdateWorkingHoursStatus.INVALID_DAY_OF_WEEK,
                                    UpdateWorkingHoursStatus.INVALID_START_OR_END_TIME,
                                    UpdateWorkingHoursStatus.START_TIME_GREATER_THAN_END_TIME,
                                    UpdateWorkingHoursStatus.DUPLICATED_DAY,
                                    UpdateWorkingHoursStatus.INVALID_DAY_COUNT -> false
                                }
                                updateDataStatus.postValue(value)
                            }
                        })
                } else {
                    DriveKitLog.i(DriveKitTripAnalysisUI.TAG, "No need to update working hours hours")
                }
            }
        }
    }

    fun updateDayConfig(dayConfiguration: DKWorkingHoursDayConfiguration) {
        config?.dayConfiguration?.indexOfFirst {
            it.day == dayConfiguration.day
        }?.let { index ->
            updatedDaysConfig?.set(index, dayConfiguration)
        }
    }

    private fun buildData(enable: Boolean, insideHours: TripStatus, outsideHours: TripStatus): DKWorkingHours? {
        return updatedDaysConfig?.let {
            DKWorkingHours(
                enable = enable,
                insideHours = insideHours,
                outsideHours = outsideHours,
                dayConfiguration = it
            )
        }
    }

    fun rawHoursValueToDate(hours: Float): String? {
        val df: DateFormat = getDateFormat(hours)
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal.add(Calendar.MINUTE, (60 * hours).toInt())
        return df.format(cal.time)
    }

    private fun getDateFormat(hours: Float): SimpleDateFormat {
        val pattern = if (hasMinutes(hours)) {
            DKDatePattern.HOUR_MINUTE_LETTER
        } else {
            DKDatePattern.HOUR_ONLY
        }
        return SimpleDateFormat(pattern.getPattern(), Locale.getDefault())
    }

    private fun hasMinutes(hours: Float): Boolean {
        return (hours % 1) != 0.0f
    }
}