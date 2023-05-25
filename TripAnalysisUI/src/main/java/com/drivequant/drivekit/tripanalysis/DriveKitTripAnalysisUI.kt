package com.drivequant.drivekit.tripanalysis

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.TripAnalysisUIEntryPoint
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashFeedbackConfig
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKDay
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHours
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHoursDayConfiguration
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHoursTimeSlotStatus
import com.drivequant.drivekit.tripanalysis.triprecordingwidget.DKTripRecordingButton
import com.drivequant.drivekit.tripanalysis.triprecordingwidget.DKTripRecordingButtonViewModel
import com.drivequant.drivekit.tripanalysis.workinghours.activity.WorkingHoursActivity

object DriveKitTripAnalysisUI : TripAnalysisUIEntryPoint {

    internal const val TAG = "DriveKit TripAnalysis UI"

    var defaultWorkHours = getDefaultWorkingHours()
    internal var crashFeedbackRoadsideAssistanceNumber: String? = null
    var tripRecordingUserMode: DKTripRecordingUserMode = DKTripRecordingUserMode.START_STOP
    val isUserAllowedToCancelTrip: Boolean
        get() = when (this.tripRecordingUserMode) {
            DKTripRecordingUserMode.NONE, DKTripRecordingUserMode.START_ONLY -> false
            DKTripRecordingUserMode.START_STOP, DKTripRecordingUserMode.STOP_ONLY -> true
        }
    val isUserAllowedToStartTripManually: Boolean
        get() = when (this.tripRecordingUserMode) {
            DKTripRecordingUserMode.START_STOP, DKTripRecordingUserMode.START_ONLY -> true
            DKTripRecordingUserMode.STOP_ONLY, DKTripRecordingUserMode.NONE -> false
        }

    fun initialize() {
        DriveKitNavigationController.tripAnalysisUIEntryPoint = this
    }

    override fun startWorkingHoursActivity(context: Context) {
        val intent = Intent(context, WorkingHoursActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun getDefaultWorkingHours(): DKWorkingHours {
        val daysConfig = mutableListOf<DKWorkingHoursDayConfiguration>()
        DKDay.values().forEach {
            daysConfig.add(
                DKWorkingHoursDayConfiguration(
                    it,
                    entireDayOff = it == DKDay.SATURDAY || it == DKDay.SUNDAY,
                    reverse = false,
                    startTime = 8.0,
                    endTime = 18.0
                )
            )
        }
        return DKWorkingHours(
            enable = false,
            insideHours = DKWorkingHoursTimeSlotStatus.BUSINESS,
            outsideHours = DKWorkingHoursTimeSlotStatus.PERSONAL,
            dayConfiguration = daysConfig
        )
    }

    fun enableCrashFeedback(roadsideAssistanceNumber: String, config: DKCrashFeedbackConfig) {
        this.crashFeedbackRoadsideAssistanceNumber = roadsideAssistanceNumber
        DriveKitTripAnalysis.enableCrashFeedback(config)
    }

    fun disableCrashFeedback() {
        this.crashFeedbackRoadsideAssistanceNumber = null
        DriveKitTripAnalysis.disableCrashFeedback()
    }

    fun getTripRecordingButton(context: Context, viewParent: ViewGroup?): DKTripRecordingButton {
        val tripRecordingButton: DKTripRecordingButton = LayoutInflater.from(context).inflate(R.layout.dk_tripanalysis_trip_recording_button, viewParent, false) as DKTripRecordingButton
        configureTripRecordingButton(tripRecordingButton)
        return tripRecordingButton
    }

    fun configureTripRecordingButton(tripRecordingButton: DKTripRecordingButton) {
        val viewModel = DKTripRecordingButtonViewModel(this.tripRecordingUserMode)
        tripRecordingButton.configure(viewModel)
    }

}
