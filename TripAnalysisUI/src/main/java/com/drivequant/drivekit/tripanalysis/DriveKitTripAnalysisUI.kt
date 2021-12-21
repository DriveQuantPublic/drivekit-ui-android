package com.drivequant.drivekit.tripanalysis

import android.content.Context
import android.content.Intent
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.TripAnalysisUIEntryPoint
import com.drivequant.drivekit.tripanalysis.workinghours.activity.WorkingHoursActivity

object DriveKitTripAnalysisUI : TripAnalysisUIEntryPoint {

    internal const val TAG = "DriveKit TripAnalysis UI"

    fun initialize() {
        DriveKitNavigationController.tripAnalysisUIEntryPoint = this
    }

    override fun startWorkingHoursActivity(context: Context) {
        val intent = Intent(context, WorkingHoursActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}