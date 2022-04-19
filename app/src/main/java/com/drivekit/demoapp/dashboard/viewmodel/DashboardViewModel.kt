package com.drivekit.demoapp.dashboard.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivekit.demoapp.drivekit.TripListenerController
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.service.recorder.State
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.SynthesisCardsViewListener
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardViewPagerFragment

internal class DashboardViewModel: ViewModel() {
    var sdkStateObserver: MutableLiveData<Any> = MutableLiveData()
    private var sdkStateChangeListener: TripListenerController.SdkStateChangeListener = object : TripListenerController.SdkStateChangeListener {
        override fun sdkStateChanged(state: State) {
            sdkStateObserver.postValue(Any())
        }
    }

    init {
        TripListenerController.addSdkStateChangeListener(sdkStateChangeListener)
    }

    fun getSynthesisCardsView(listener: SynthesisCardsViewListener) {
        DriverDataUI.getLastTripsSynthesisCardsView(listener = object : SynthesisCardsViewListener {
            override fun onViewLoaded(fragment: DKSynthesisCardViewPagerFragment) {
                listener.onViewLoaded(fragment)
            }
        })
    }

    fun getLastTripsCardsView() = DriverDataUI.getLastTripsView(HeaderDay.DURATION)

    fun startStopTrip() {
        if (DriveKitTripAnalysis.isTripRunning()) {
            DriveKitTripAnalysis.stopTrip()
        } else {
            DriveKitTripAnalysis.startTrip()
        }
    }

    fun getStartStopTripButtonTitleResId() = if (DriveKitTripAnalysis.isTripRunning()) {
        R.string.stop_trip
    } else {
        R.string.start_trip
    }
}