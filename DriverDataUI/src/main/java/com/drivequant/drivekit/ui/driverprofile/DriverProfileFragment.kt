package com.drivequant.drivekit.ui.driverprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.commontrips.CommonTripsContainer
import com.drivequant.drivekit.ui.driverprofile.component.distanceestimation.DistanceEstimationContainer
import com.drivequant.drivekit.ui.driverprofile.component.driverprofiledescription.DriverProfileDescriptionContainer

internal class DriverProfileFragment : Fragment() {

    companion object {
        fun newInstance() = DriverProfileFragment()
    }

    private lateinit var viewModel: DriverProfileViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var profileDescriptionContainer: DriverProfileDescriptionContainer
    private lateinit var distanceEstimationContainer: DistanceEstimationContainer
    private lateinit var commonTripsContainer: CommonTripsContainer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_driverprofile, container, false).setDKStyle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.swipeRefreshLayout = view.findViewById(R.id.dk_swipe_refresh_driverprofile)
        this.profileDescriptionContainer = view.findViewById(R.id.profileDescriptionContainer)
        this.distanceEstimationContainer = view.findViewById(R.id.distanceEstimationContainer)
        this.commonTripsContainer = view.findViewById(R.id.commonTripsContainer)

        checkViewModelInitialization()
        setupSwipeToRefresh()

        this.viewModel.dataUpdated.observe(viewLifecycleOwner) {
            updateSwipeRefreshTripsVisibility(false)
        }
    }

    override fun onResume() {
        super.onResume()
        tagScreen()
        checkViewModelInitialization()
    }

    private fun setupSwipeToRefresh() {
        updateSwipeRefreshTripsVisibility(false)
        this.swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
    }

    private fun updateData() {
        updateSwipeRefreshTripsVisibility(true)
        this.viewModel.updateData()
    }

    private fun updateSwipeRefreshTripsVisibility(display: Boolean) {
        this.swipeRefreshLayout.isRefreshing = display
    }

    private fun tagScreen() {
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                requireContext(),
                "dk_tag_driver_profile"
            ), javaClass.simpleName
        )
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            activity?.application?.let { application ->
                if (!this::viewModel.isInitialized) {
                    viewModel = ViewModelProvider(
                        this,
                        DriverProfileViewModel.DriverProfileViewModelFactory(application)
                    )[DriverProfileViewModel::class.java]
                }
            }
        }
    }
}
