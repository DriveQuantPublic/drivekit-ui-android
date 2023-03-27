package com.drivekit.demoapp.drivekit

import com.drivequant.drivekit.tripanalysis.DeviceConfigEvent
import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.entity.PostGeneric
import com.drivequant.drivekit.tripanalysis.entity.PostGenericResponse
import com.drivequant.drivekit.tripanalysis.entity.TripPoint
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashInfo
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackSeverity
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackType
import com.drivequant.drivekit.tripanalysis.service.recorder.CancelTrip
import com.drivequant.drivekit.tripanalysis.service.recorder.StartMode
import com.drivequant.drivekit.tripanalysis.service.recorder.State
import java.lang.ref.WeakReference

internal object TripListenerController : TripListener {
    private val tripListeners: MutableList<WeakReference<TripListener>> = mutableListOf()
    private val sdkStateChangeListeners: MutableList<WeakReference<SdkStateChangeListener>> = mutableListOf()

    fun addTripListener(listener: TripListener) {
        tripListeners.add(WeakReference(listener))
    }

    fun removeTripListener(tripListener: TripListener) {
        val iterator = tripListeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().get()?.let {
                if (it === tripListener) {
                    iterator.remove()
                }
            }
        }
    }

    fun addSdkStateChangeListener(sdkStateChangeListener: SdkStateChangeListener) {
        sdkStateChangeListeners.add(WeakReference(sdkStateChangeListener))
    }

    fun removeSdkStateChangeListener(sdkStateChangeListener: SdkStateChangeListener) {
        val iterator = sdkStateChangeListeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().get()?.let {
                if (it === sdkStateChangeListener) {
                    iterator.remove()
                }
            }
        }
    }

    override fun sdkStateChanged(state: State) {
        tripListeners.forEach {
            it.get()?.sdkStateChanged(state)
        }
        sdkStateChangeListeners.forEach {
            it.get()?.sdkStateChanged(state)
        }
    }

    override fun tripCancelled(cancelTrip: CancelTrip) {
        tripListeners.forEach {
            it.get()?.tripCancelled(cancelTrip)
        }
    }

    override fun beaconDetected() {
        tripListeners.forEach {
            it.get()?.beaconDetected()
        }
    }

    override fun crashDetected(crashInfo: DKCrashInfo) {
        tripListeners.forEach {
            it.get()?.crashDetected(crashInfo)
        }
    }

    override fun crashFeedbackSent(
        crashInfo: DKCrashInfo,
        feedbackType: CrashFeedbackType,
        severity: CrashFeedbackSeverity
    ) {
        tripListeners.forEach {
            it.get()?.crashFeedbackSent(crashInfo, feedbackType, severity)
        }
    }

    override fun potentialTripStart(startMode: StartMode) {
        tripListeners.forEach {
            it.get()?.potentialTripStart(startMode)
        }
    }

    override fun tripPoint(tripPoint: TripPoint) {
        tripListeners.forEach {
            it.get()?.tripPoint(tripPoint)
        }
    }

    override fun tripSavedForRepost() {
        tripListeners.forEach {
            it.get()?.tripSavedForRepost()
        }
    }

    override fun tripStarted(startMode: StartMode) {
        tripListeners.forEach {
            it.get()?.tripStarted(startMode)
        }
    }

    override fun tripFinished(post: PostGeneric, response: PostGenericResponse) {
        tripListeners.forEach {
            it.get()?.tripFinished(post, response)
        }
    }

    override fun onDeviceConfigEvent(deviceConfigEvent: DeviceConfigEvent) {
        tripListeners.forEach {
            it.get()?.onDeviceConfigEvent(deviceConfigEvent)
        }
    }

    interface SdkStateChangeListener {
        fun sdkStateChanged(state: State)
    }
}
