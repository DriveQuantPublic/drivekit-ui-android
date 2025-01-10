package com.drivequant.drivekit.tripanalysis.tripsharing

import java.util.Date

internal data class TripSharingUiState(
    val isLoading: Boolean = true,
    val isCreatingLink: Boolean = false,
    val link: String? = null,
    val endDate: Date? = null,
    val errorMessage: String? = null
)
