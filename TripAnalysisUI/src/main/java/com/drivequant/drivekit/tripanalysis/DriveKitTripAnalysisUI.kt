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

    var defaultWorkHours = getDefaultWorkingHours()

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
            insideHours = TripStatus.BUSINESS,
            outsideHours = TripStatus.PERSONAL,
            dayConfiguration = daysConfig
        )
    }
}