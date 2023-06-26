package com.drivequant.drivekit.ui.driverprofile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.driverprofile.DKDriverProfile
import com.drivequant.drivekit.driverdata.driverprofile.DKDriverProfileStatus
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus

internal class DriverProfileViewModel(application: Application) : AndroidViewModel(application) {

    val dataUpdated = MutableLiveData<DataState>()
    private var driverProfile: DKDriverProfile? = null
    private var currentDrivenDistanceByPeriod: Map<DKPeriod, Double> = mapOf()
    private val timelinePeriods = listOf(DKPeriod.WEEK, DKPeriod.MONTH, DKPeriod.YEAR)

    init {
        DriveKitDriverData.getDriverProfile(SynchronizationType.CACHE) { status, driverProfile ->
            if (status == DKDriverProfileStatus.SUCCESS) {
                this.driverProfile = driverProfile
                DriveKitDriverData.getDriverTimelines(timelinePeriods, SynchronizationType.CACHE) { timelineStatus, timelines ->
                    if (timelineStatus == TimelineSyncStatus.CACHE_DATA_ONLY) {
                        updateDrivenDistances(timelines)
                    }
                    this.dataUpdated.postValue(DataState.UPDATED)
                }
            } else {
                this.dataUpdated.postValue(DataState.NO_DATA_YET)
            }
        }
        updateData()
    }

    fun updateData() {
        DriveKitDriverData.getDriverProfile(SynchronizationType.DEFAULT) { status, driverProfile ->
            when (status) {
                DKDriverProfileStatus.NO_DRIVER_PROFILE_YET -> this.dataUpdated.postValue(DataState.NO_DATA_YET)
                DKDriverProfileStatus.FORBIDDEN_ACCESS -> this.dataUpdated.postValue(DataState.FORBIDDEN)
                DKDriverProfileStatus.SUCCESS, DKDriverProfileStatus.FAILED_TO_SYNC_DRIVER_PROFILE_CACHE_ONLY -> {
                    this.driverProfile = driverProfile
                    DriveKitDriverData.getDriverTimelines(timelinePeriods, SynchronizationType.DEFAULT) { timelineStatus, timelines ->
                        if (timelineStatus == TimelineSyncStatus.NO_ERROR) {
                            updateDrivenDistances(timelines)
                        }
                        this.dataUpdated.postValue(DataState.UPDATED)
                    }
                }
            }
        }
    }

    private fun updateDrivenDistances(timelines: List<DKDriverTimeline>) {
        this.currentDrivenDistanceByPeriod = timelines.associateBy({ it.period }, { it.allContext.lastOrNull()?.distance ?: 0.0 })
    }

    class DriverProfileViewModelFactory(private val application: Application) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DriverProfileViewModel(application) as T
        }
    }
}

internal enum class DataState {
    UPDATED,
    NO_DATA_YET,
    FORBIDDEN
}
