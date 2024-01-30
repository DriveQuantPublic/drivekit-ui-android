package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.drivequant.drivekit.common.ui.component.triplist.views.DKTripListView
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController


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
            outState.putString("challengeIdTag", viewModel.challengeId)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_challenge_detail_trips), javaClass.simpleName)

        savedInstanceState?.getString("challengeIdTag")?.let {
            viewModel = ViewModelProvider(
                this,
                ChallengeDetailViewModel.ChallengeDetailViewModelFactory(it)
            )[ChallengeDetailViewModel::class.java]
        }

        val challengeTripsList: DKTripListView = view.findViewById(R.id.challenge_trips_list)
        val challengeHeaderViewContainer: FrameLayout = view.findViewById(R.id.challenge_header_view_container)

        challengeTripsList.configure(object : DKTripList {
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
        })

        val viewModelParticipation = ViewModelProvider(
            this,
            ChallengeParticipationViewModel.ChallengeParticipationViewModelFactory(viewModel.challengeId)
        )[ChallengeParticipationViewModel::class.java]

        val challengeHeaderView =
            ChallengeHeaderView(
                requireContext()
            )
        challengeHeaderView.configure(viewModelParticipation, requireActivity())
        challengeHeaderView.displayRulesText(false)
        challengeHeaderView.displayConsultRulesText(false)
        challengeHeaderView.displayConditionsDescriptionText(false)
        challengeHeaderViewContainer.addView(challengeHeaderView)
    }
}
