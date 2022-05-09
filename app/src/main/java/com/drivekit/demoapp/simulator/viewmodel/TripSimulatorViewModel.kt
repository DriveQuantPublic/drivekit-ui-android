package com.drivekit.demoapp.simulator.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.drivekit.demoapp.config.DriveKitConfig
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripsimulator.DriveKitTripSimulator

internal class TripSimulatorViewModel {
    val presetTripItems = PresetTripType.values()
    var selectedPresetTripType: MutableLiveData<PresetTripType> = MutableLiveData()

    fun shouldShowDeveloperModeErrorMessage() = !DriveKitTripSimulator.isDeveloperModeEnabled()

    fun shouldShowMockLocationErrorMessage() = !DriveKitTripSimulator.isMockLocationEnabled()

    fun isAutoStartEnabled(context: Context) = DriveKitConfig.isTripAnalysisAutoStartedEnabled(context)

    fun hasGpsVehicle() = DriveKitTripAnalysis.getConfig().vehicle.detectionMode == DetectionMode.GPS
}