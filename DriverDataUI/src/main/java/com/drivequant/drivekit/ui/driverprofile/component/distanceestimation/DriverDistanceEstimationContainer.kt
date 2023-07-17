package com.drivequant.drivekit.ui.driverprofile.component.distanceestimation

import android.content.Context
import android.util.AttributeSet
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.DriverProfileContainer
import com.drivequant.drivekit.ui.driverprofile.component.DriverProfileContainerViewPagerAdapter

internal class DriverDistanceEstimationContainer(context: Context, attrs: AttributeSet) : DriverProfileContainer<DriverDistanceEstimationViewModel>(context, attrs) {
    fun configure(viewModels: List<DriverDistanceEstimationViewModel>) {
        this.adapter = DriverProfileContainerViewPagerAdapter(R.layout.dk_driver_profile_distance_estimation_view, viewModels)
    }
}
