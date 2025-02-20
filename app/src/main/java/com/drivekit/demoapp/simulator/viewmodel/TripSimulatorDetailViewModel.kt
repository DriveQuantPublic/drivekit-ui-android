package com.drivekit.demoapp.simulator.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.TripAnalysisConfig
import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.model.triplistener.DKTripRecordingCanceledState
import com.drivequant.drivekit.tripanalysis.service.recorder.State
import com.drivequant.drivekit.tripanalysis.utils.TripResult
import com.drivequant.drivekit.tripsimulator.DriveKitTripSimulator
import com.drivequant.drivekit.tripsimulatorapi.DKTripSimulatorListener
import com.drivequant.drivekit.tripsimulatorapi.PresetTrip
import com.drivequant.drivekit.tripsimulatorapi.PresetTrip.BOAT_TRIP
import com.drivequant.drivekit.tripsimulatorapi.PresetTrip.BUS_TRIP
import com.drivequant.drivekit.tripsimulatorapi.PresetTrip.CITY_TRIP
import com.drivequant.drivekit.tripsimulatorapi.PresetTrip.HIGHWAY_TRIP
import com.drivequant.drivekit.tripsimulatorapi.PresetTrip.MIXED_TRIP
import com.drivequant.drivekit.tripsimulatorapi.PresetTrip.SHORT_TRIP
import com.drivequant.drivekit.tripsimulatorapi.PresetTrip.SUBURBAN_TRIP
import com.drivequant.drivekit.tripsimulatorapi.PresetTrip.TRAIN_TRIP
import com.drivequant.drivekit.tripsimulatorapi.PresetTrip.TRIP_WITH_CRASH_1
import com.drivequant.drivekit.tripsimulatorapi.PresetTrip.TRIP_WITH_CRASH_2_STILL_DRIVING
import java.util.*
import java.util.concurrent.TimeUnit

internal interface TripSimulatorDetailViewModelListener {
    fun updateNeeded(updatedValue: Double?, timestamp: Double?)
}

internal class TripSimulatorDetailViewModel(private val presetTripType: PresetTripType) :
    ViewModel(),
    DKTripSimulatorListener, TripListener {

    var isSimulating: Boolean = false
    private var listener: TripSimulatorDetailViewModelListener? = null
    private var currentDuration: Long = 0
    private var currentSpeed: Double? = 0.0
    private var timeWhenEnteredStoppingState: Date? = null
    private var stoppingTimer: Timer? = null

    init {
        DriveKitTripAnalysis.addTripListener(this)
        startSimulation()
    }

    fun registerListener(listener: TripSimulatorDetailViewModelListener) {
        this.listener = listener
    }

    fun unregisterListener() {
        this.listener = null
        DriveKitTripAnalysis.removeTripListener(this)
    }

    fun startSimulation() {
        DriveKitTripSimulator.start(PresetTripType.getPresetTrip(presetTripType), this)
        isSimulating = true
    }

    fun stopSimulation() {
        DriveKitTripSimulator.stop()
        isSimulating = false
        currentSpeed = null
        timeWhenEnteredStoppingState = null
        stoppingTimer = null
        currentDuration = 0
        updateNeeded()
    }

    fun getState() = DriveKitTripAnalysis.getRecorderState().name

    fun shouldDisplayStoppingMessage() = DriveKitTripAnalysis.getRecorderState() == State.STOPPING

    fun getRemainingTimeToStop() = timeWhenEnteredStoppingState?.let {
        val timeout = TripAnalysisConfig.stopTimeOut
        val diff = TimeUnit.SECONDS.convert(Date().time - it.time, TimeUnit.MILLISECONDS)
        val remainingDuration = TimeUnit.SECONDS.convert(timeout - diff, TimeUnit.SECONDS)
        remainingDuration.toDouble().formatSimulatorDuration()
    } ?: ""

    private fun updateStoppingTime(state: State) {
        if (state == State.STOPPING) {
            if (timeWhenEnteredStoppingState == null) {
                timeWhenEnteredStoppingState = Date()
                val timerTask = object : TimerTask() {
                    override fun run() {
                        updateNeeded()
                    }
                }
                if (stoppingTimer == null) {
                    stoppingTimer = Timer()
                    stoppingTimer?.scheduleAtFixedRate(timerTask, 0, 1000)
                }
            }
            if (currentDuration >= PresetTripType.getPresetTrip(presetTripType).getSimulationDuration()) {
                currentSpeed = null
            }
        } else {
            stoppingTimer?.cancel()
            stoppingTimer = null
            timeWhenEnteredStoppingState = null
        }
    }

    fun getTotalDuration() = PresetTripType.getPresetTrip(presetTripType).getSimulationDuration().formatSimulatorDuration()

    fun getSpentDuration() = if (isSimulating) {
        currentDuration.toDouble().formatSimulatorDuration()
    } else {
        "-"
    }

    fun getVelocity(context: Context) = if (isSimulating) {
        currentSpeed?.let {
            DKDataFormatter.formatSpeedMean(context, it)
        } ?: "-"
    } else {
        "-"
    }

    private fun updateNeeded(updatedValue: Double? = null, timestamp: Double? = null) {
        listener?.updateNeeded(updatedValue, timestamp)
    }

    override fun tripSavedForRepost() {
        stopSimulation()
    }

    override fun tripFinished(result: TripResult) {
        stopSimulation()
    }

    override fun sdkStateChanged(state: State) {
        updateStoppingTime(state)
        updateNeeded()
    }

    override fun tripRecordingCanceled(state: DKTripRecordingCanceledState) {
        stopSimulation()
    }

    override fun onLocationSent(location: Location, durationSinceStart: Long) {
        currentDuration = durationSinceStart + 1
        currentSpeed = (location.speed.toDouble() * 3600).div(1000)

        updateStoppingTime(DriveKitTripAnalysis.getRecorderState())
        updateNeeded(currentSpeed, currentDuration.toDouble())
    }

    @Suppress("UNCHECKED_CAST")
    class TripSimulatorDetailViewModelFactory(private val presetTripType: PresetTripType) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TripSimulatorDetailViewModel(presetTripType) as T
        }
    }
}

fun Double.formatSimulatorDuration(): String {
    val durationInt = this.toInt()
    val seconds = durationInt % 60
    val minutes = (durationInt - seconds) / 60
    return String.format("%02d:%02d", minutes, seconds)
}
