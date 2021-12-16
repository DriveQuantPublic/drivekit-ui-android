package com.drivequant.drivekit.tripanalysis.activationhours.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType

internal class ActivationHoursViewModel : ViewModel() {

    var dayConfigList: List<DKDay>? = null
    var syncDataError: MutableLiveData<Boolean> = MutableLiveData()
        private set

    fun fetchData(syncType: SynchronizationType = SynchronizationType.DEFAULT) {

        dayConfigList = listOf(DKDay("testAU"), DKDay("testAU"), DKDay("testAU"), DKDay("testAU"))


        if (DriveKit.isConfigured()) {
            // DriveKitTripAnalysis.getActivationHours(syncType, object : …)
            //syncDataError.postValue(value)
        }
    }

    //  mock
    data class DKDay(
        val jour: String
    )
}