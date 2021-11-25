package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.challenge.ui.common.ChallengeHeaderView
import com.drivequant.drivekit.challenge.ui.common.toDKTripList
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.triplist.DKTripList
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.DKHeader
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_fragment_challenge_trip_list.*


class ChallengeTripListFragment : Fragment() {

    private lateinit var viewModel: ChallengeDetailViewModel

    companion object {
        fun newInstance(viewModel: ChallengeDetailViewModel): ChallengeTripListFragment {
            val fragment = ChallengeTripListFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_challenge_trip_list, container, false)

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.putString("challengeIdTag", viewModel.getChallengeId())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_challenge_detail_trips"
            ), javaClass.simpleName
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.getString("challengeIdTag")?.let {
            viewModel = ViewModelProviders.of(
                this,
                ChallengeDetailViewModel.ChallengeDetailViewModelFactory(it)
            ).get(ChallengeDetailViewModel::class.java)
        }

        challenge_trips_list.configure(object : DKTripList {
            override fun getTripData(): TripData = viewModel.getTripData()
            override fun getTripsList(): List<DKTripListItem> =
                viewModel.challengeDetailTrips.toDKTripList()

            override fun getCustomHeader(): DKHeader? = null
            override fun getHeaderDay(): HeaderDay = HeaderDay.DURATION_DISTANCE
            override fun getDayTripDescendingOrder(): Boolean = false
            override fun canSwipeToRefresh(): Boolean = false
            override fun onSwipeToRefresh() {}
            override fun onTripClickListener(itinId: String) {
                DriveKitNavigationController.driverDataUIEntryPoint?.startTripDetailActivity(
                    requireContext(),
                    itinId
                )
            }
        }, null)

        val viewModelParticipation = ViewModelProviders.of(
            this,
            ChallengeParticipationViewModel.ChallengeParticipationViewModelFactory(viewModel.getChallengeId())
        ).get(ChallengeParticipationViewModel::class.java)

        val view =
            ChallengeHeaderView(
                requireContext()
            )
        view.configure(viewModelParticipation, requireActivity())
        view.displayRulesText(false)
        view.displayConsultRulesText(false)
        view.displayConditionsDescriptionText(false)
        challenge_header_view_container.addView(view)
    }
}