package com.drivequant.drivekit.ui.driverprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.DKSecondaryButton
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.DriverProfileScrollState
import com.drivequant.drivekit.ui.driverprofile.component.commontripfeature.DriverCommonTripFeatureContainer
import com.drivequant.drivekit.ui.driverprofile.component.distanceestimation.DriverDistanceEstimationContainer
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureContainer

internal class DriverProfileFragment : Fragment() {

    companion object {
        fun newInstance() = DriverProfileFragment()
    }

    private lateinit var viewModel: DriverProfileViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var driverProfileFeatureContainer: DriverProfileFeatureContainer
    private lateinit var driverDistanceEstimationContainer: DriverDistanceEstimationContainer
    private lateinit var driverCommonTripFeatureContainer: DriverCommonTripFeatureContainer
    private lateinit var drivingConditionsButton: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_driverprofile, container, false).setDKStyle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.swipeRefreshLayout = view.findViewById(R.id.dk_swipe_refresh_driverprofile)
        this.driverProfileFeatureContainer = view.findViewById(R.id.driverProfileFeatureContainer)
        this.driverDistanceEstimationContainer = view.findViewById(R.id.driverDistanceEstimationContainer)
        this.driverCommonTripFeatureContainer = view.findViewById(R.id.driverCommonTripFeatureContainer)
        this.drivingConditionsButton = view.findViewById(R.id.button_detail)

        checkViewModelInitialization()
        setupSwipeToRefresh()
        configureDrivingConditionsButton()

        this.driverProfileFeatureContainer.onScrollStateChangeCallback = {
            this.swipeRefreshLayout.isEnabled = it != DriverProfileScrollState.MOVING
        }
        this.driverDistanceEstimationContainer.onScrollStateChangeCallback = {
            this.swipeRefreshLayout.isEnabled = it != DriverProfileScrollState.MOVING
        }
        this.driverCommonTripFeatureContainer.onScrollStateChangeCallback = {
            this.swipeRefreshLayout.isEnabled = it != DriverProfileScrollState.MOVING
        }

        this.viewModel.dataUpdated.observe(viewLifecycleOwner) {
            onDataUpdated()
        }

        onDataUpdated()
        updateData()
    }

    override fun onResume() {
        super.onResume()
        tagScreen()
        checkViewModelInitialization()
    }

    private fun onDataUpdated() {
        this.driverProfileFeatureContainer.configure(this.viewModel.getDriverProfileFeatureViewModels())
        this.driverDistanceEstimationContainer.configure(this.viewModel.getDriverDistanceEstimationViewModels())
        this.driverCommonTripFeatureContainer.configure(this.viewModel.getDriverCommonTripFeatureViewModels())

        updateSwipeRefreshTripsVisibility(false)
    }

    private fun setupSwipeToRefresh() {
        updateSwipeRefreshTripsVisibility(false)
        this.swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
    }

    private fun configureDrivingConditionsButton() {
        this.drivingConditionsButton.setContent {
            DKSecondaryButton(getString(R.string.dk_driverdata_drivingconditions_show)) {
                context?.let {
                    DriverDataUI.startDrivingConditionsActivity(it)
                }
            }
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
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_driver_profile), javaClass.simpleName)
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
