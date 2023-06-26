package com.drivequant.drivekit.ui.driverprofile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.driverprofile.DKDriverProfile
import com.drivequant.drivekit.driverdata.driverprofile.DKDriverProfileStatus

internal class DriverProfileViewModel(application: Application) : AndroidViewModel(application) {

    var driverProfile: DKDriverProfile? = null
        private set
    val dataUpdated = MutableLiveData<DataState>()

    init {
        DriveKitDriverData.getDriverProfile(SynchronizationType.CACHE) { status, driverProfile ->
            if (status == DKDriverProfileStatus.SUCCESS) {
                this.driverProfile = driverProfile
                this.dataUpdated.postValue(DataState.UPDATED)
            } else {
                this.dataUpdated.postValue(DataState.NO_DATA_YET)
            }
        }
        updateData()
    }

    fun updateData() {
        DriveKitDriverData.getDriverProfile(SynchronizationType.DEFAULT) { status, driverProfile ->
            val dataState: DataState = when (status) {
                DKDriverProfileStatus.NO_DRIVER_PROFILE_YET -> DataState.NO_DATA_YET
                DKDriverProfileStatus.FORBIDDEN_ACCESS -> DataState.FORBIDDEN
                DKDriverProfileStatus.SUCCESS, DKDriverProfileStatus.FAILED_TO_SYNC_DRIVER_PROFILE_CACHE_ONLY -> {
                    this.driverProfile = driverProfile
                    DataState.UPDATED
                }
            }
            this.dataUpdated.postValue(dataState)
        }
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
