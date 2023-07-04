package com.drivequant.drivekit.ui.driverprofile.component.commontripfeature

import android.content.Context
import android.util.AttributeSet
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.DriverProfileContainer
import com.drivequant.drivekit.ui.driverprofile.component.DriverProfileContainerViewPagerAdapter

internal class DriverCommonTripFeatureContainer(context: Context, attrs: AttributeSet) : DriverProfileContainer<DriverCommonTripFeatureViewModel>(context, attrs) {
    fun configure(viewModels: List<DriverCommonTripFeatureViewModel>) {
        this.adapter = DriverProfileContainerViewPagerAdapter(R.layout.dk_driver_profile_common_trip_feature_view, viewModels)
    }
}
