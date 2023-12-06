package com.drivekit.demoapp.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import com.drivekit.demoapp.features.enum.FeatureType
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.SynthesisCardsViewListener
import com.drivequant.drivekit.ui.synthesiscards.LastTripsSynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardViewPagerFragment

internal class DashboardViewModel: ViewModel() {
    fun getSynthesisCardsView(listener: SynthesisCardsViewListener) {
        DriverDataUI.getLastTripsSynthesisCardsView(
            listOf(
                LastTripsSynthesisCard.SAFETY,
                LastTripsSynthesisCard.ECO_DRIVING,
                LastTripsSynthesisCard.DISTRACTION,
                LastTripsSynthesisCard.SPEEDING
            ),
            listener = object : SynthesisCardsViewListener {
                override fun onViewLoaded(fragment: DKSynthesisCardViewPagerFragment) {
                    listener.onViewLoaded(fragment)
                }
            })
    }

    fun getLastTripsCardsView() = DriverDataUI.getLastTripsView(HeaderDay.DISTANCE)

    fun getFeatureCardTitleResId() = FeatureType.ALL.getTitleResId()

    fun getFeatureCardDescriptionResId() = FeatureType.ALL.getDescriptionResId()

    fun getFeatureCardTextButtonButtonResId() = FeatureType.ALL.getActionButtonTitleResId()
}
