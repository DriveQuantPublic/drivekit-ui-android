package com.drivequant.drivekit.tripanalysis.triprecordingwidget.stopconfirmation

import androidx.annotation.StringRes
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis

internal class TripStopConfirmationViewModel {
    @StringRes val endTripTitleId: Int = R.string.dk_tripwidget_confirm_endtrip_title
    @StringRes val endTripSubtitleId: Int = R.string.dk_tripwidget_confirm_endtrip_subtitle
    @StringRes val continueTripTitleId: Int = R.string.dk_tripwidget_confirm_continuetrip_title
    @StringRes val continueTripSubtitleId: Int = R.string.dk_tripwidget_confirm_continuetrip_subtitle
    @StringRes val cancelTripTitleId: Int = R.string.dk_tripwidget_confirm_canceltrip_title
    @StringRes val cancelTripSubtitleId: Int = R.string.dk_tripwidget_confirm_canceltrip_subtitle

    fun stopTrip() {
        DriveKitTripAnalysis.stopTrip()
    }

    fun cancelTrip() {
        DriveKitTripAnalysis.cancelTrip()
    }
}
