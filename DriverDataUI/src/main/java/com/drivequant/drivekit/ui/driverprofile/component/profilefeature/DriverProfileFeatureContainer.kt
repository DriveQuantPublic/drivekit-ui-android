package com.drivequant.drivekit.ui.driverprofile.component.profilefeature

import android.content.Context
import android.util.AttributeSet
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.DriverProfileContainer
import com.drivequant.drivekit.ui.driverprofile.component.DriverProfileContainerViewPagerAdapter

internal class DriverProfileFeatureContainer(context: Context, attrs: AttributeSet) : DriverProfileContainer<DriverProfileFeatureViewModel>(context, attrs) {
    fun configure(viewModels: List<DriverProfileFeatureViewModel>) {
        this.adapter = DriverProfileContainerViewPagerAdapter(R.layout.dk_driver_profile_feature_view, viewModels)
    }
}
