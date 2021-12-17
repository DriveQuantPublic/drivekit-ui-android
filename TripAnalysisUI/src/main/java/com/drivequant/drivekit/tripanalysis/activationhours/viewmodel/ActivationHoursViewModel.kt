package com.drivequant.drivekit.tripanalysis.activationhours.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysisUI
import com.drivequant.drivekit.tripanalysis.service.activationhours.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal class ActivationHoursViewModel : ViewModel() {

    private var shouldUpdate = false
    var config: DKActivationHours? = null
    var updatedDaysConfig: MutableList<DKActivationHoursDayConfiguration>? = mutableListOf()
    var syncDataStatus: MutableLiveData<Boolean> = MutableLiveData()
        private set
    var updateDataStatus: MutableLiveData<Boolean> = MutableLiveData()
        private set

    fun fetchData(syncType: SynchronizationType = SynchronizationType.DEFAULT) {
        if (DriveKit.isConfigured()) {
            DriveKitTripAnalysis.getActivationHours(object : SyncActivationHoursQueryListener {
                override fun onResponse(
                    status: SyncActivationHoursStatus,
                    activationHours: DKActivationHours?
                ) {
                    val value = when (status) {
                        SyncActivationHoursStatus.SUCCESS -> true
                        SyncActivationHoursStatus.FAILED_TO_SYNC_CACHE_ONLY -> false
                    }
                    config = activationHours
                    updatedDaysConfig = activationHours?.dayConfiguration?.toMutableList()
                    syncDataStatus.postValue(value)
                }
            }, syncType)
        }
    }

    fun updateConfig(enable: Boolean, enableSorting: Boolean) {
        if (DriveKit.isConfigured()) {
            if (shouldUpdate) {
                buildData(enable, enableSorting)?.let {
                    DriveKitTripAnalysis.updateActivationHours(it,
                        object : UpdateActivationHoursQueryListener {
                            override fun onResponse(status: UpdateActivationHoursStatus) {
                                val value = when (status) {
                                    UpdateActivationHoursStatus.SUCCESS -> true
                                    UpdateActivationHoursStatus.FAILED -> false
                                }
                                updateDataStatus.postValue(value)
                            }
                        })
                }
            } else {
                DriveKitLog.i(DriveKitTripAnalysisUI.TAG, "No need to update activation hours")
            }
        }
    }

    fun updateDayConfig(dayConfiguration: DKActivationHoursDayConfiguration) {
        shouldUpdate = true
        config?.dayConfiguration?.indexOfFirst {
            it.day == dayConfiguration.day
        }?.let { index ->
            updatedDaysConfig?.set(index, dayConfiguration)
        }
    }

    fun displayLogbook() = DriveKitTripAnalysisUI.logbookSorting

    private fun buildData(enable: Boolean, outsideHours: Boolean): DKActivationHours? {
        return updatedDaysConfig?.let {
            DKActivationHours(
                enable = enable,
                outsideHours = outsideHours,
                dayConfiguration = it.toList()
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