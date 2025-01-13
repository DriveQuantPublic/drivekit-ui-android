package com.drivequant.drivekit.tripanalysis.tripsharing

import com.drivequant.drivekit.common.ui.utils.DurationUnit

internal data class TripSharingUiState(
    val isLoading: Boolean = true,
    val isCreatingLink: Boolean = false,
    val link: String? = null,
    val linkDuration: Pair<Int, DurationUnit>? = null,
    val errorMessage: String? = null
)
