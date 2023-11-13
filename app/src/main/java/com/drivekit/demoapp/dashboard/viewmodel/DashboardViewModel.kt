package com.drivekit.demoapp.dashboard.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivekit.demoapp.features.enum.FeatureType
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.entity.PostGeneric
import com.drivequant.drivekit.tripanalysis.entity.PostGenericResponse
import com.drivequant.drivekit.tripanalysis.service.recorder.CancelTrip
import com.drivequant.drivekit.tripanalysis.service.recorder.StartMode
import com.drivequant.drivekit.tripanalysis.service.recorder.State
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.SynthesisCardsViewListener
import com.drivequant.drivekit.ui.synthesiscards.LastTripsSynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardViewPagerFragment

internal class DashboardViewModel: ViewModel() {
    var sdkStateObserver: MutableLiveData<Any> = MutableLiveData()
    val tripListener = object : TripListener {
        override fun tripCancelled(cancelTrip: CancelTrip) {}
        override fun tripFinished(post: PostGeneric, response: PostGenericResponse) {}
        override fun tripSavedForRepost() {}
        override fun tripStarted(startMode: StartMode) {}
        override fun sdkStateChanged(state: State) { sdkStateObserver.postValue(Any()) }
    }

    init {
        DriveKitTripAnalysis.addTripListener(tripListener)
    }

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
