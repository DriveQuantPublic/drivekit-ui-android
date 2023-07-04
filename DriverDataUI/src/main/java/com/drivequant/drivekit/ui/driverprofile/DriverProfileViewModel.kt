package com.drivequant.drivekit.ui.driverprofile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.startingFrom
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.driverprofile.DKDriverProfile
import com.drivequant.drivekit.driverdata.driverprofile.DKDriverProfileStatus
import com.drivequant.drivekit.driverdata.driverprofile.DKMobilityAreaType
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.driverdata.timeline.TimelineSyncStatus
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.commontripfeature.DriverCommonTripFeatureViewModel
import com.drivequant.drivekit.ui.driverprofile.component.distanceestimation.DriverDistanceEstimationViewModel
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureDescription
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureViewModel
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.extension.getViewModel
import java.util.Date

internal class DriverProfileViewModel(application: Application) : AndroidViewModel(application) {

    val dataUpdated = MutableLiveData<Boolean>()
    private var driverProfile: DKDriverProfile? = null
    private var currentDrivenDistanceByPeriod: Map<DKPeriod, Double> = mapOf()
    private val timelinePeriods = listOf(DKPeriod.WEEK, DKPeriod.MONTH, DKPeriod.YEAR)
    private var dataState: DataState = DataState.NO_DATA_YET

    init {
        DriveKitDriverData.getDriverProfile(SynchronizationType.CACHE) { status, driverProfile ->
            if (status == DKDriverProfileStatus.SUCCESS) {
                this.driverProfile = driverProfile
                DriveKitDriverData.getDriverTimelines(timelinePeriods, SynchronizationType.CACHE) { timelineStatus, timelines ->
                    if (timelineStatus == TimelineSyncStatus.CACHE_DATA_ONLY) {
                        updateDrivenDistances(timelines)
                    }
                    this.dataState = DataState.VALID
                }
            } else {
                this.dataState = DataState.NO_DATA_YET
            }
        }
    }

    fun updateData() {
        DriveKitDriverData.getDriverProfile(SynchronizationType.DEFAULT) { status, driverProfile ->
            when (status) {
                DKDriverProfileStatus.NO_DRIVER_PROFILE_YET -> onNewState(DataState.NO_DATA_YET)
                DKDriverProfileStatus.FORBIDDEN_ACCESS -> onNewState(DataState.FORBIDDEN)
                DKDriverProfileStatus.SUCCESS, DKDriverProfileStatus.FAILED_TO_SYNC_DRIVER_PROFILE_CACHE_ONLY -> {
                    this.driverProfile = driverProfile
                    DriveKitDriverData.getDriverTimelines(timelinePeriods, SynchronizationType.DEFAULT) { timelineStatus, timelines ->
                        if (timelineStatus == TimelineSyncStatus.NO_ERROR) {
                            updateDrivenDistances(timelines)
                        }
                        onNewState(DataState.VALID)
                    }
                }
            }
        }
    }

    fun getDriverProfileFeatureViewModels(): List<DriverProfileFeatureViewModel> =
        this.driverProfile?.let {
            listOf(
                it.distance.getViewModel(),
                it.mobility.getViewModel(it.mobilityAreaRadiusByType[DKMobilityAreaType.PERCENTILE_90TH]),
                it.activity.getViewModel(it.statistics.getActiveWeekPercentage()),
                it.regularity.getViewModel(it.weekRegularity.tripNumberMean, it.weekRegularity.distanceMean),
                it.mainRoadContext.getViewModel(it.roadContextInfoByRoadContext[it.mainRoadContext]?.distancePercentage)
            )
        } ?: run {
            listOf(
                DriverProfileFeatureViewModel(
                    R.string.dk_driverdata_profile_empty_card_title,
                    DriverProfileFeatureDescription.SimpleDescription(R.string.dk_driverdata_profile_empty_card_text),
                    R.drawable.dk_profile_empty
                )
            )
        }

    fun getDriverDistanceEstimationViewModels(): List<DriverDistanceEstimationViewModel> =
        this.driverProfile?.let {
            listOf(
                DriverDistanceEstimationViewModel(
                    R.string.dk_driverdata_distance_card_title_year,
                    R.string.dk_driverdata_distance_card_current_year,
                    this.currentDrivenDistanceByPeriod[DKPeriod.YEAR] ?: 0.0,
                    it.distanceEstimation.yearDistance
                ),
                DriverDistanceEstimationViewModel(
                    R.string.dk_driverdata_distance_card_title_month,
                    R.string.dk_driverdata_distance_card_current_month,
                    this.currentDrivenDistanceByPeriod[DKPeriod.MONTH] ?: 0.0,
                    it.distanceEstimation.monthDistance
                ),
                DriverDistanceEstimationViewModel(
                    R.string.dk_driverdata_distance_card_title_week,
                    R.string.dk_driverdata_distance_card_current_week,
                    this.currentDrivenDistanceByPeriod[DKPeriod.WEEK] ?: 0.0,
                    it.distanceEstimation.weekDistance
                )
            )
        } ?: run {
            listOf(
                DriverDistanceEstimationViewModel(
                    R.string.dk_driverdata_distance_card_title_year,
                    R.string.dk_driverdata_distance_card_current_year,
                    null,
                    null
                )
            )
        }

    fun getDriverCommonTripFeatureViewModels(): List<DriverCommonTripFeatureViewModel> =
        this.driverProfile?.let {
            //TODO
            emptyList()
        } ?: run {
            emptyList()
        }

    private fun onNewState(dataState: DataState) {
        this.dataState = dataState
        this.dataUpdated.postValue(true)
    }

    private fun updateDrivenDistances(timelines: List<DKDriverTimeline>) {
        this.currentDrivenDistanceByPeriod = timelines.associateBy(
            { it.period },
            { driverTimeline ->
                val distance = driverTimeline.allContext.lastOrNull()?.let {
                    val date = when (driverTimeline.period) {
                        DKPeriod.WEEK -> Date().startingFrom(CalendarField.WEEK)
                        DKPeriod.MONTH -> Date().startingFrom(CalendarField.MONTH)
                        DKPeriod.YEAR -> Date().startingFrom(CalendarField.YEAR)
                    }
                    if (it.date == date) {
                        it.distance
                    } else {
                        null
                    }
                } ?: 0.0
                distance
            }
        )
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
    VALID,
    NO_DATA_YET,
    FORBIDDEN
}
