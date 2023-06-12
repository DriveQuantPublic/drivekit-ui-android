package com.drivequant.drivekit.tripanalysis.triprecordingwidget.recordingbutton

import java.util.Date

internal sealed class RecordingState {

    object Stopped : RecordingState()

    data class Recording(
        val startingDate: Date,
        var distance: Double,
        var duration: Double
    ) : RecordingState()

}
