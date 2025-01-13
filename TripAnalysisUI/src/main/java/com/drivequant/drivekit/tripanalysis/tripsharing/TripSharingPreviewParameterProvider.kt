package com.drivequant.drivekit.tripanalysis.tripsharing

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.drivequant.drivekit.common.ui.utils.DurationUnit

internal class TripSharingPreviewParameterProvider :
    PreviewParameterProvider<TripSharingUiState> {
    override val values = sequenceOf(
        TripSharingUiState(
            isLoading = true,
            isCreatingLink = false,
            link = null,
            linkDuration = null,
            errorMessage = null
        ),
        TripSharingUiState(
            isLoading = false,
            isCreatingLink = false,
            link = null,
            linkDuration = null,
            errorMessage = null
        ),
        TripSharingUiState(
            isLoading = false,
            isCreatingLink = true,
            link = null,
            linkDuration = null,
            errorMessage = null
        ),
        TripSharingUiState(
            isLoading = false,
            isCreatingLink = false,
            link = "test",
            linkDuration = Pair(14, DurationUnit.HOUR),
            errorMessage = null
        )
    )
}
