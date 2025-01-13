package com.drivequant.drivekit.tripanalysis.tripsharing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.utils.DurationUnit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.service.tripsharing.model.CreateTripSharingLinkStatus
import com.drivequant.drivekit.tripanalysis.service.tripsharing.model.GetTripSharingLinkStatus
import com.drivequant.drivekit.tripanalysis.service.tripsharing.model.RevokeTripSharingLinkStatus
import java.util.Date
import kotlin.math.ceil
import kotlin.math.roundToInt

internal class TripSharingViewModel : ViewModel() {
    var uiState by mutableStateOf(TripSharingUiState())
        private set

    init {
        updateState(true, SynchronizationType.CACHE)
        updateState(false, SynchronizationType.DEFAULT)
    }

    private fun updateState(isLoading: Boolean, synchronizationType: SynchronizationType) {
        DriveKitTripAnalysis.tripSharing.getLink(synchronizationType = synchronizationType) { status, link ->
            uiState = when (status) {
                GetTripSharingLinkStatus.SUCCESS, GetTripSharingLinkStatus.FAILED_TO_GET_CACHE_ONLY -> TripSharingUiState(isLoading, false, link?.url, computeLinkDuration(link?.endDate), null)
                GetTripSharingLinkStatus.NO_ACTIVE_LINK -> uiState.copy(isLoading = isLoading)
                else -> uiState.copy(isLoading = isLoading, errorMessage = "TODO")
            }
        }
    }

    fun setupTripSharing() {
        uiState = uiState.copy(isCreatingLink = true)
    }

    fun cancelSetupTripSharing() {
        uiState = uiState.copy(isCreatingLink = false)
    }

    fun activateTripSharing(durationInSeconds: Int) {
        uiState = uiState.copy(isLoading = true)
        DriveKitTripAnalysis.tripSharing.createLink(durationInSeconds) { status, link ->
            when (status) {
                CreateTripSharingLinkStatus.SUCCESS -> uiState = TripSharingUiState(isLoading = false, link = link?.url, linkDuration = computeLinkDuration(link?.endDate))
                CreateTripSharingLinkStatus.ACTIVE_LINK_ALREADY_EXISTS -> updateState(false, SynchronizationType.DEFAULT)
                CreateTripSharingLinkStatus.USER_NOT_CONNECTED -> uiState = uiState.copy(errorMessage = "TODO: no connection")
                else -> uiState = uiState.copy(isLoading = false, errorMessage = "TODO: ERROR")
            }
        }
    }

    fun revokeLink() {
        uiState = uiState.copy(isLoading = true)
        DriveKitTripAnalysis.tripSharing.revokeLink { status ->
            uiState = when (status) {
                RevokeTripSharingLinkStatus.SUCCESS, RevokeTripSharingLinkStatus.NO_ACTIVE_LINK -> TripSharingUiState(isLoading = false)
                else -> uiState.copy(isLoading = false, errorMessage = "TODO: ERROR")
            }
        }
    }

    private fun computeLinkDuration(endDate: Date?): Pair<Int, DurationUnit>? {
        return endDate?.let { date ->
            val remainingTimeInSeconds = (date.time - System.currentTimeMillis()) / 1000
            val oneMinuteInSeconds = 60.0
            val hourInMinutes = 60
            val remainingMinutes = ceil(remainingTimeInSeconds / oneMinuteInSeconds).toInt()
            if (remainingMinutes < hourInMinutes) {
                Pair(remainingMinutes, DurationUnit.MINUTE)
            } else {
                val oneHourInSeconds = 3_600.0 // 60 * 60
                val dayInHours = 24
                val remainingHours = (remainingTimeInSeconds / oneHourInSeconds).roundToInt()
                if (remainingHours < dayInHours) {
                    Pair(remainingHours, DurationUnit.HOUR)
                } else {
                    val oneDayInSeconds = 86_400.0 // 60 * 60 * 24
                    val remainingDays = (remainingTimeInSeconds / oneDayInSeconds).roundToInt()
                    Pair(remainingDays, DurationUnit.DAY)
                }
            }
        }
    }
}
