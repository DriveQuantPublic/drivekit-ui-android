package com.drivequant.drivekit.challenge.ui.challengedetail.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.toDKTripList
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.common.ui.component.triplist.DKTripList
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.DKHeader
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
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
        challenge_trips_list.configure(object :DKTripList {
            override fun getTripData(): TripData = viewModel.getTripData()
            override fun getTripsList(): List<DKTripListItem> {
                val trip =  viewModel.challengeTrips.toDKTripList()
                return trip
            }
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.getString("challengeIdTag")?.let {
            viewModel = ViewModelProviders.of(
                this,
                ChallengeDetailViewModel.ChallengeDetailViewModelFactory(it)
            ).get(ChallengeDetailViewModel::class.java)
        }
    }
}