package com.drivequant.drivekit.tripanalysis.tripsharing

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.drivequant.drivekit.common.ui.utils.DurationUnit

internal class DebugTripSharingActivity : TripSharingActivity() {
    @Preview(showBackground = true)
    @Composable
    fun TripSharingPreview(@PreviewParameter(TripSharingPreviewParameterProvider::class) state: TripSharingUiState) {
        TripSharingScreen(state)
    }
}

private class TripSharingPreviewParameterProvider :
    PreviewParameterProvider<TripSharingUiState> {
    override val values = sequenceOf(
        TripSharingUiState(
            isLoading = true,
            isCreatingLink = false,
            link = null,
            linkDuration = null,
            hasError = false
        ),
        TripSharingUiState(
            isLoading = false,
            isCreatingLink = false,
            link = null,
            linkDuration = null,
            hasError = false
        ),
        TripSharingUiState(
            isLoading = false,
            isCreatingLink = true,
            link = null,
            linkDuration = null,
            hasError = false
        ),
        TripSharingUiState(
            isLoading = false,
            isCreatingLink = false,
            link = "test",
            linkDuration = Pair(14, DurationUnit.HOUR),
            hasError = false
        ),
        TripSharingUiState(
            isLoading = false,
            isCreatingLink = true,
            link = null,
            linkDuration = null,
            hasError = true
        )
    )
}
