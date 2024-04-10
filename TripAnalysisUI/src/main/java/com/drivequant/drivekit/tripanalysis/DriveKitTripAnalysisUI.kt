package com.drivequant.drivekit.tripanalysis

import android.content.Context
import android.content.Intent
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.TripAnalysisUIEntryPoint
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.common.DKDay
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashFeedbackConfig
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHours
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHoursDayConfiguration
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHoursTimeSlotStatus
import com.drivequant.drivekit.tripanalysis.triprecordingwidget.recordingbutton.DKTripRecordingButton
import com.drivequant.drivekit.tripanalysis.triprecordingwidget.recordingbutton.DKTripRecordingUserMode
import com.drivequant.drivekit.tripanalysis.workinghours.activity.WorkingHoursActivity

object DriveKitTripAnalysisUI : TripAnalysisUIEntryPoint {
    internal const val TAG = "DriveKit Trip Analysis UI"

    @JvmStatic
    var defaultWorkHours = getDefaultWorkingHours()
    internal var crashFeedbackRoadsideAssistanceNumber: String? = null
    @JvmStatic
    var tripRecordingUserMode: DKTripRecordingUserMode = DKTripRecordingUserMode.START_STOP
    @JvmStatic
    val isUserAllowedToCancelTrip: Boolean
        get() = isUserAllowedToCancelTrip(this.tripRecordingUserMode)
    @JvmStatic
    val isUserAllowedToStartTripManually: Boolean
        get() = when (this.tripRecordingUserMode) {
            DKTripRecordingUserMode.START_STOP, DKTripRecordingUserMode.START_ONLY -> true
            DKTripRecordingUserMode.STOP_ONLY, DKTripRecordingUserMode.NONE -> false
        }

    init {
        DriveKit.checkInitialization()
        DriveKitLog.i(TAG, "Initialization")
        DriveKitNavigationController.tripAnalysisUIEntryPoint = this
    }

    @JvmStatic
    fun initialize() {
        // Nothing to do currently.
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

    @JvmStatic
    fun enableCrashFeedback(roadsideAssistanceNumber: String, config: DKCrashFeedbackConfig) {
        this.crashFeedbackRoadsideAssistanceNumber = roadsideAssistanceNumber
        DriveKitTripAnalysis.enableCrashFeedback(config)
    }

    @JvmStatic
    fun disableCrashFeedback() {
        this.crashFeedbackRoadsideAssistanceNumber = null
        DriveKitTripAnalysis.disableCrashFeedback()
    }

    @JvmStatic
    fun newTripRecordingButtonFragment(): DKTripRecordingButton = DKTripRecordingButton()

    @JvmStatic
    fun isUserAllowedToCancelTrip(tripRecordingUserMode: DKTripRecordingUserMode): Boolean =
        when (tripRecordingUserMode) {
            DKTripRecordingUserMode.NONE, DKTripRecordingUserMode.START_ONLY -> false
            DKTripRecordingUserMode.START_STOP, DKTripRecordingUserMode.STOP_ONLY -> true
        }
}
