package com.drivequant.drivekit.tripanalysis.triprecordingwidget.recordingbutton

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
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysisUI
import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.entity.TripPoint
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

    @get:IdRes
    val iconResId: Int?
        get() = when (this.state) {
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

    val hasSubtitle: Boolean
        get() = this.state is RecordingState.Recording
    private var timer: Job? = null
    private val lock = Any()

    init {
        if (DriveKitTripAnalysis.isTripRunning()) {
            DriveKitTripAnalysis.getLastTripPointOfCurrentTrip()?.let {
                tripPoint(it)
            } ?: DriveKitTripAnalysis.getCurrentTripStartDate()?.let {
                synchronized(this.lock) {
                    if (this.state !is RecordingState.Recording) {
                        this.state = RecordingState.Recording(it, 0.0, durationFromDate(it))
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
            is RecordingState.Recording -> DKDataFormatter.formatMeterDistanceInKm(
                context,
                it.distance,
                true,
                minDistanceToRemoveFractions = Double.MAX_VALUE
            ).convertToString()

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
                is RecordingState.Recording -> shouldShowConfirmationDialog = when (this.tripRecordingUserMode) {
                    DKTripRecordingUserMode.START_STOP,
                    DKTripRecordingUserMode.STOP_ONLY -> true

                    DKTripRecordingUserMode.NONE, DKTripRecordingUserMode.START_ONLY -> false
                }

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

    fun canClick(): Boolean = when (this.tripRecordingUserMode) {
        DKTripRecordingUserMode.NONE -> false
        DKTripRecordingUserMode.START_ONLY -> when (this.state) {
            is RecordingState.Recording -> false
            is RecordingState.Stopped -> true
        }

        DKTripRecordingUserMode.START_STOP -> true
        DKTripRecordingUserMode.STOP_ONLY -> when (this.state) {
            is RecordingState.Recording -> true
            is RecordingState.Stopped -> false
        }
    }

    fun canShowTripStopConfirmationDialog(): Boolean =
        this.state is RecordingState.Recording && DriveKitTripAnalysisUI.isUserAllowedToCancelTrip

    fun isHidden(): Boolean = when (this.tripRecordingUserMode) {
        DKTripRecordingUserMode.NONE -> true
        DKTripRecordingUserMode.START_STOP,
        DKTripRecordingUserMode.START_ONLY-> false
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
                    this@DKTripRecordingButtonViewModel.state =
                        RecordingState.Recording(Date(), 0.0, 0.0)
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

    override fun tripCancelled(cancelTrip: CancelTrip) {
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

    override fun onCleared() {
        super.onCleared()
        DriveKitTripAnalysis.removeTripListener(this)
    }
}
