package com.drivekit.demoapp.simulator.viewmodel

import androidx.lifecycle.MutableLiveData
import com.drivequant.drivekit.tripsimulator.DriveKitTripSimulator

class TripSimulatorViewModel {
    val presetTripItems = PresetTripType.values()
    var selectedPresetTripType: MutableLiveData<PresetTripType> = MutableLiveData()

    fun shouldShowDeveloperModeErrorMessage() = !DriveKitTripSimulator.isDeveloperModeEnabled()

    fun shouldShowMockLocationErrorMessage() = !DriveKitTripSimulator.isMockLocationEnabled()
}