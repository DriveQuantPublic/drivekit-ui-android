package com.drivekit.demoapp.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.SynthesisCardsViewListener
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardViewPagerFragment

internal class DashboardViewModel: ViewModel() {

    fun getTitleResId() = R.string.dashboard_header

    fun getSynthesisCardsView(listener: SynthesisCardsViewListener) {
        DriverDataUI.getLastTripsSynthesisCardsView(listener = object : SynthesisCardsViewListener {
            override fun onViewLoaded(fragment: DKSynthesisCardViewPagerFragment) {
                listener.onViewLoaded(fragment)
            }
        })
    }

    fun getLastTripsCardsView() = DriverDataUI.getLastTripsView(HeaderDay.DURATION)



}