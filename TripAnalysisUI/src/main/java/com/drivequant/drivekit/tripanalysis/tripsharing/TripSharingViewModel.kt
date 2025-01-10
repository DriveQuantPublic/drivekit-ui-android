package com.drivequant.drivekit.tripanalysis.tripsharing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.service.tripsharing.model.CreateTripSharingLinkStatus
import com.drivequant.drivekit.tripanalysis.service.tripsharing.model.GetTripSharingLinkStatus
import com.drivequant.drivekit.tripanalysis.service.tripsharing.model.RevokeTripSharingLinkStatus

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
                GetTripSharingLinkStatus.SUCCESS, GetTripSharingLinkStatus.FAILED_TO_GET_CACHE_ONLY -> TripSharingUiState(isLoading, false, link?.url, link?.endDate, null)
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
                CreateTripSharingLinkStatus.SUCCESS -> uiState = TripSharingUiState(isLoading = false, link = link?.url, endDate = link?.endDate)
                CreateTripSharingLinkStatus.ACTIVE_LINK_ALREADY_EXISTS -> updateState(false, SynchronizationType.DEFAULT)
                CreateTripSharingLinkStatus.USER_NOT_CONNECTED -> uiState = uiState.copy(errorMessage = "TODO: no connection")
                else -> uiState = uiState.copy(isLoading = false, errorMessage = "TODO: ERROR")
            }
        }
    }

    fun revokeLink() {
        uiState = uiState.copy(isLoading = true)
        DriveKitTripAnalysis.tripSharing.revokeLink { status ->
            when (status) {
                RevokeTripSharingLinkStatus.SUCCESS, RevokeTripSharingLinkStatus.NO_ACTIVE_LINK -> uiState = TripSharingUiState(isLoading = false)
                else -> uiState = uiState.copy(isLoading = false, errorMessage = "TODO: ERROR")
            }
        }
    }
}
