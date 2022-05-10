package com.drivekit.demoapp.simulator.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.drivekit.demoapp.config.DriveKitConfig
import com.drivequant.drivekit.databaseutils.entity.DetectionMode
import com.drivequant.drivekit.dbvehicleaccess.DbVehicleAccess
import com.drivequant.drivekit.tripsimulator.DriveKitTripSimulator

internal class TripSimulatorViewModel {
    val presetTripItems = PresetTripType.values()
    var selectedPresetTripType: MutableLiveData<PresetTripType> = MutableLiveData()

    fun shouldShowDeveloperModeErrorMessage() = !DriveKitTripSimulator.isDeveloperModeEnabled()

    fun shouldShowMockLocationErrorMessage() = !DriveKitTripSimulator.isMockLocationEnabled()

    fun isAutoStartEnabled(context: Context) = DriveKitConfig.isTripAnalysisAutoStartedEnabled(context)

    fun hasVehicleAutoStartMode(): Boolean {
        DbVehicleAccess.vehiclesQuery().noFilter().query().execute().forEach {
            if (it.detectionMode != DetectionMode.DISABLED) {
                return true
            }
        }
        return false
    }
}