package com.drivekit.demoapp.drivekit

import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.entity.TripPoint
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashInfo
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackSeverity
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackType
import com.drivequant.drivekit.tripanalysis.service.recorder.StartMode
import com.drivequant.drivekit.tripanalysis.service.recorder.State
import java.lang.ref.WeakReference

internal object TripListenerController : TripListener {
    private val listeners: MutableList<WeakReference<TripListener>> = mutableListOf()

    fun addListener(listener: TripListener) {
        listeners.add(WeakReference(listener))
    }

    fun removeListener(listener: TripListener) {
        val iterator = listeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().get()?.let {
                if (it === listener) {
                    iterator.remove()
                }
            }
        }
    }

    override fun sdkStateChanged(state: State) {
        listeners.forEach {
            it.get()?.sdkStateChanged(state)
        }
    }

    override fun beaconDetected() {
        listeners.forEach {
            it.get()?.beaconDetected()
        }
    }

    override fun crashDetected(crashInfo: DKCrashInfo) {
        listeners.forEach {
            it.get()?.crashDetected(crashInfo)
        }
    }

    override fun crashFeedbackSent(
        crashInfo: DKCrashInfo,
        feedbackType: CrashFeedbackType,
        severity: CrashFeedbackSeverity
    ) {
        listeners.forEach {
            it.get()?.crashFeedbackSent(crashInfo, feedbackType, severity)
        }
    }

    override fun potentialTripStart(startMode: StartMode) {
        listeners.forEach {
            it.get()?.potentialTripStart(startMode)
        }
    }

    override fun tripPoint(tripPoint: TripPoint) {
        listeners.forEach {
            it.get()?.tripPoint(tripPoint)
        }
    }

    override fun tripSavedForRepost() {
        listeners.forEach {
            it.get()?.tripSavedForRepost()
        }
    }

    override fun tripStarted(startMode: StartMode) {
        listeners.forEach {
            it.get()?.tripStarted(startMode)
        }
    }
}