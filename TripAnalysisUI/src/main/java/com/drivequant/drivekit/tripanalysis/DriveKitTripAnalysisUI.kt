package com.drivequant.drivekit.tripanalysis

import android.content.Context
import android.content.Intent
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.TripAnalysisUIEntryPoint
import com.drivequant.drivekit.tripanalysis.activationhours.activity.ActivationHoursActivity

object DriveKitTripAnalysisUI : TripAnalysisUIEntryPoint {

    internal const val TAG = "DriveKit TripAnalysis UI"

    private var logbookSorting = false

    fun initialize() {
        DriveKitNavigationController.tripAnalysisUIEntryPoint = this
    }

    fun configureLogbookSorting(enable: Boolean) {
        logbookSorting = enable
    }

    override fun startActivationHoursActivity(context: Context) {
        val intent = Intent(context, ActivationHoursActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}