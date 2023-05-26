package com.drivequant.drivekit.tripanalysis.triprecordingwidget

import android.content.Context
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.tripanalysis.DeviceConfigEvent
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.round

internal class DKTripRecordingButtonViewModel(private val tripRecordingUserMode: DKTripRecordingUserMode) :
    ViewModel(), TripListener {

    var onViewModelUpdate: (() -> Unit)? = null
    private var state: RecordingState = RecordingState.Stopped
        set(value) {
            field = value
            if (value is RecordingState.Stopped) {
                stopTimer()
            } else if (value is RecordingState.Recording && this.timer == null) {
                startTimer()
            }
            this.onViewModelUpdate?.invoke()
        }

    @IdRes
    internal val iconResId: Int? = when (this.state) {
        is RecordingState.Recording -> when (this.tripRecordingUserMode) {
            DKTripRecordingUserMode.NONE -> null
            DKTripRecordingUserMode.START_STOP, DKTripRecordingUserMode.STOP_ONLY -> R.drawable.dk_trip_analysis_stop
            DKTripRecordingUserMode.START_ONLY -> R.drawable.dk_trip_analysis_record
        }

        is RecordingState.Stopped -> when (this.tripRecordingUserMode) {
            DKTripRecordingUserMode.NONE, DKTripRecordingUserMode.STOP_ONLY -> null
            DKTripRecordingUserMode.START_ONLY, DKTripRecordingUserMode.START_STOP -> R.drawable.dk_trip_analysis_play
        }
    }

    internal val hasSubtitle: Boolean
        get() = this.state is RecordingState.Recording
    private var timer: Job? = null
    private val lock = Any()

    init {
        if (DriveKitTripAnalysis.isTripRunning()) {
            DriveKitTripAnalysis.getLastTripPoint()?.let {
                tripPoint(it)
            } ?: DriveKitTripAnalysis.getCurrentTripStartDate()?.let {
                synchronized(this.lock) {
                    if (this.state !is RecordingState.Recording) {
                        this.state = RecordingState.Recording(it, null, durationFromDate(it))
                    }
                }
            }
        }
        DriveKitTripAnalysis.addTripListener(this)
    }

    fun title(context: Context): String = this.state.let {
        when (it) {
            is RecordingState.Recording -> {
                val dateText = it.startingDate.formatDate(DKDatePattern.HOUR_MINUTE_LETTER)
                context.getString(R.string.dk_tripwidget_record_title, dateText)
            }

            is RecordingState.Stopped -> context.getString(R.string.dk_tripwidget_start_title)
        }
    }

    fun distanceSubtitle(context: Context): String? = this.state.let {
        when (it) {
            is RecordingState.Recording -> {
                it.distance?.let { distance ->
                    DKDataFormatter.formatMeterDistanceInKm(
                        context,
                        distance,
                        true,
                        minDistanceToRemoveFractions = Double.MAX_VALUE
                    ).convertToString()
                } ?: "- ${context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_unit_kilometer)}"
            }

            is RecordingState.Stopped -> null
        }
    }

    fun durationSubtitle(): String? = this.state.let {
        when (it) {
            is RecordingState.Recording -> DKDataFormatter.formatDurationWithColons(it.duration).convertToString()
            is RecordingState.Stopped -> null
        }
    }

    fun toggleRecordingState(): Boolean {
        val shouldShowConfirmationDialog: Boolean
        this.state.let {
            when (it) {
                is RecordingState.Recording -> shouldShowConfirmationDialog = true
                is RecordingState.Stopped -> {
                    synchronized(this.lock) {
                        this.state = RecordingState.Recording(Date(), 0.0, 0.0)
                    }
                    DriveKitTripAnalysis.startTrip()
                    shouldShowConfirmationDialog = false
                }
            }
        }
        return shouldShowConfirmationDialog
    }

    fun canShowTripStopConfirmationDialog() = this.state is RecordingState.Recording

    fun isHidden(): Boolean = when (this.tripRecordingUserMode) {
        DKTripRecordingUserMode.NONE -> true
        DKTripRecordingUserMode.START_STOP -> false
        DKTripRecordingUserMode.START_ONLY -> when (this.state) {
            is RecordingState.Recording -> true
            is RecordingState.Stopped -> false
        }
        DKTripRecordingUserMode.STOP_ONLY -> when (this.state) {
            is RecordingState.Recording -> false
            is RecordingState.Stopped -> true
        }
    }

    private fun startTimer() {
        if (this.timer == null) {
            this.timer = viewModelScope.launch {
                while (isActive) {
                    delay(1_000)
                    state.let {
                        synchronized(lock) {
                            if (it is RecordingState.Recording) {
                                it.duration = durationFromDate(it.startingDate)
                                onViewModelUpdate?.invoke()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun stopTimer() {
        this.timer?.cancel()
        this.timer = null
    }

    private fun durationFromDate(date: Date): Double =
        round((System.currentTimeMillis() - date.time) / 1000.0)

    private fun startingDateFromDuration(duration: Double): Date =
        Date(System.currentTimeMillis() - duration.toLong() * 1000L)

    override fun sdkStateChanged(state: State) {
        viewModelScope.launch {
            when (state) {
                State.INACTIVE, State.SENDING -> synchronized(lock) {
                    this@DKTripRecordingButtonViewModel.state = RecordingState.Stopped
                }

                State.RUNNING, State.STOPPING -> {
                    // Nothing to do.
                }

                State.STARTING -> synchronized(lock) {
                    this@DKTripRecordingButtonViewModel.state = RecordingState.Recording(Date(), 0.0, 0.0)
                }
            }
        }
    }

    override fun tripPoint(tripPoint: TripPoint) {
        this.state.let {
            synchronized(this.lock) {
                if (it is RecordingState.Stopped) {
                    this.state = RecordingState.Recording(
                        startingDateFromDuration(tripPoint.duration),
                        tripPoint.distance,
                        tripPoint.duration
                    )
                } else if (it is RecordingState.Recording) {
                    it.distance = tripPoint.distance
                    it.duration = tripPoint.duration
                }
            }
        }
    }

    override fun tripCancelled(cancelTrip: CancelTrip) = synchronized(this.lock) {
        this.state = RecordingState.Stopped
    }

    override fun beaconDetected() {
        // Nothing to do.
    }

    override fun crashDetected(crashInfo: DKCrashInfo) {
        // Nothing to do.
    }

    override fun crashFeedbackSent(
        crashInfo: DKCrashInfo,
        feedbackType: CrashFeedbackType,
        severity: CrashFeedbackSeverity
    ) {
        // Nothing to do.
    }

    override fun onDeviceConfigEvent(deviceConfigEvent: DeviceConfigEvent) {
        // Nothing to do.
    }

    override fun potentialTripStart(startMode: StartMode) {
        // Nothing to do.
    }

    override fun tripFinished(post: PostGeneric, response: PostGenericResponse) {
        // Nothing to do.
    }

    override fun tripSavedForRepost() {
        // Nothing to do.
    }

    override fun tripStarted(startMode: StartMode) {
        // Nothing to do.
    }

    @Suppress("UNCHECKED_CAST")
    class DKTripRecordingButtonViewModelFactory(private val tripRecordingUserMode: DKTripRecordingUserMode) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DKTripRecordingButtonViewModel(tripRecordingUserMode) as T
        }
    }
}
