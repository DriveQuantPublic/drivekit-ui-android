package com.drivequant.drivekit.tripanalysis

import android.content.Context
import android.content.Intent
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.TripAnalysisUIEntryPoint
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKDay
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHours
import com.drivequant.drivekit.tripanalysis.service.workinghours.DKWorkingHoursDayConfiguration
import com.drivequant.drivekit.tripanalysis.service.workinghours.TripStatus
import com.drivequant.drivekit.tripanalysis.workinghours.activity.WorkingHoursActivity

object DriveKitTripAnalysisUI : TripAnalysisUIEntryPoint {

    internal const val TAG = "DriveKit TripAnalysis UI"

    var defaultWorkHoursConfig = buildDefaultWorkingHours()

    fun initialize() {
        DriveKitNavigationController.tripAnalysisUIEntryPoint = this
    }

    override fun startWorkingHoursActivity(context: Context) {
        val intent = Intent(context, WorkingHoursActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun buildDefaultWorkingHours(): DKWorkingHours {
        val daysConfig = mutableListOf<DKWorkingHoursDayConfiguration>()
        DKDay.values().forEach {
            daysConfig.add(
                DKWorkingHoursDayConfiguration(
                    it,
                    entireDayOff = setOf(DKDay.SATURDAY, DKDay.SUNDAY).contains(it),
                    reverse = false,
                    startTime = 8.0f,
                    endTime = 18.0f
                )
            )
        }
        return DKWorkingHours(
            enable = false,
            insideHours = TripStatus.BUSINESS,
            outsideHours = TripStatus.PERSONAL,
            dayConfiguration = daysConfig
        )
    }
}