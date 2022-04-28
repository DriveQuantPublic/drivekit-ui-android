package com.drivekit.demoapp.simulator.viewmodel

import android.location.Location
import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.entity.TripPoint
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashInfo
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackSeverity
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackType
import com.drivequant.drivekit.tripanalysis.service.recorder.StartMode
import com.drivequant.drivekit.tripsimulator.DriveKitTripSimulator

class TripSimulatorDetailViewModel : DriveKitTripSimulator.DKTripSimulatorListener, TripListener {

    override fun onLocationSent(location: Location, durationSinceStart: Long) {

    }

    override fun beaconDetected() {}
    override fun crashDetected(crashInfo: DKCrashInfo) {}
    override fun crashFeedbackSent(
        crashInfo: DKCrashInfo,
        feedbackType: CrashFeedbackType,
        severity: CrashFeedbackSeverity) {}
    override fun potentialTripStart(startMode: StartMode) {}
    override fun tripPoint(tripPoint: TripPoint) {}
    override fun tripSavedForRepost() {}
    override fun tripStarted(startMode: StartMode) {}
}