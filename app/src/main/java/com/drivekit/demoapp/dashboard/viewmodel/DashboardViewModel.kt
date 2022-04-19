package com.drivekit.demoapp.dashboard.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivekit.demoapp.drivekit.TripListenerController
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.entity.TripPoint
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashInfo
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackSeverity
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackType
import com.drivequant.drivekit.tripanalysis.service.recorder.StartMode
import com.drivequant.drivekit.tripanalysis.service.recorder.State
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.SynthesisCardsViewListener
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardViewPagerFragment

internal class DashboardViewModel: ViewModel() {
    var sdkStateObserver: MutableLiveData<Any> = MutableLiveData()
    private var tripListener: TripListener = object : TripListener {
        override fun beaconDetected() {

        }

        override fun crashDetected(crashInfo: DKCrashInfo) {
        }

        override fun crashFeedbackSent(
            crashInfo: DKCrashInfo,
            feedbackType: CrashFeedbackType,
            severity: CrashFeedbackSeverity
        ) {
        }

        override fun potentialTripStart(startMode: StartMode) {
        }

        override fun tripPoint(tripPoint: TripPoint) {
        }

        override fun tripSavedForRepost() {
        }

        override fun tripStarted(startMode: StartMode) {
        }

        override fun sdkStateChanged(state: State) {
            sdkStateObserver.postValue(Any())
        }
    }

    init {
        TripListenerController.addListener(tripListener)
    }

    fun getSynthesisCardsView(listener: SynthesisCardsViewListener) {
        DriverDataUI.getLastTripsSynthesisCardsView(listener = object : SynthesisCardsViewListener {
            override fun onViewLoaded(fragment: DKSynthesisCardViewPagerFragment) {
                listener.onViewLoaded(fragment)
            }
        })
    }

    fun getLastTripsCardsView() = DriverDataUI.getLastTripsView(HeaderDay.DURATION)

    fun manageStartStopTripButton() {
        if (DriveKitTripAnalysis.isTripRunning()) {
            DriveKitTripAnalysis.stopTrip()
        } else {
            DriveKitTripAnalysis.startTrip()
        }
    }

    fun getStartStopTripButtonTitleResId() = if (DriveKitTripAnalysis.isTripRunning()) {
        R.string.stop_trip
    } else {
        R.string.start_trip
    }

    fun manageStartStopTripSimulatorButton() {
        // TODO launch trip simulator activity
    }
}