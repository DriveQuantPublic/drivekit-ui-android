package com.drivekit.demoapp.simulator.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.TripAnalysisConfig
import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.entity.TripPoint
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashInfo
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackSeverity
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackType
import com.drivequant.drivekit.tripanalysis.service.recorder.StartMode
import com.drivequant.drivekit.tripanalysis.service.recorder.State
import com.drivequant.drivekit.tripsimulator.DriveKitTripSimulator
import java.util.*

class TripSimulatorDetailViewModel(private val presetTripType: PresetTripType) : ViewModel(),
    DriveKitTripSimulator.DKTripSimulatorListener, TripListener {

    var currentSpeed: MutableLiveData<Float> = MutableLiveData()
    var currentDuration: Long = 0
    private var isSimulating: Boolean = true
    private var timeWhenEnteredStoppingState: Date? = null

    fun startSimulation() {
        currentDuration = 0
        timeWhenEnteredStoppingState = null
        DriveKitTripSimulator.start(PresetTripType.getPresetTrip(presetTripType), this)
        isSimulating = true
    }

    fun stopSimulation() {
        DriveKitTripSimulator.stop()
        isSimulating = false
    }

    fun getState() = DriveKitTripAnalysis.getRecorderState().name

    private fun formatDuration(duration: Double): String {
        val durationInt = duration.toInt()
        val seconds = durationInt % 60
        val minutes = (durationInt - seconds) / 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    fun shouldDisplayStoppingMessage() = DriveKitTripAnalysis.getRecorderState() == State.STOPPING

    fun getRemainingTimeToStop() = timeWhenEnteredStoppingState?.let {
        val timeout = TripAnalysisConfig.stopTimeOut
        val remainingDuration = timeout.toDouble() - (Date().time - it.time)
        formatDuration(remainingDuration)
    } ?: ""

    fun getTotalDuration() = PresetTripType.getPresetTrip(presetTripType).getSimulationDuration().let { formatDuration(it) }

    fun getSpentDuration() = if (isSimulating) {
        formatDuration(currentDuration.toDouble())
    } else {
        "-"
    }

    fun getVelocity(context: Context) = currentSpeed.value?.let {
        DKDataFormatter.formatSpeedMean(context, it.toDouble())
    } ?: ""

    override fun onLocationSent(location: Location, durationSinceStart: Long) {
        currentDuration = durationSinceStart + 1
        currentSpeed.postValue(location.speed * 3600 / 1000)

        updateStoppingTime(DriveKitTripAnalysis.getRecorderState())
    }

    private fun updateStoppingTime(state: State) {
        if (state == State.STOPPING) {
            timeWhenEnteredStoppingState?.let {
                timeWhenEnteredStoppingState = Date()

            } ?: run {

            }
        }
    }

    override fun beaconDetected() {}
    override fun crashDetected(crashInfo: DKCrashInfo) {}
    override fun crashFeedbackSent(
        crashInfo: DKCrashInfo,
        feedbackType: CrashFeedbackType,
        severity: CrashFeedbackSeverity
    ) {
    }

    override fun potentialTripStart(startMode: StartMode) {}
    override fun tripPoint(tripPoint: TripPoint) {}
    override fun tripSavedForRepost() {}
    override fun tripStarted(startMode: StartMode) {}

    @Suppress("UNCHECKED_CAST")
    class TripSimulatorDetailViewModelFactory(private val presetTripType: PresetTripType) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TripSimulatorDetailViewModel(presetTripType) as T
        }
    }
}