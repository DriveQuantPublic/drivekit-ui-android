package com.drivekit.demoapp.features.viewmodel

import androidx.lifecycle.ViewModel
import com.drivekit.demoapp.features.enum.FeatureType
import com.drivequant.drivekit.core.access.AccessType
import com.drivequant.drivekit.core.access.DriveKitAccess

internal class FeatureListViewModel : ViewModel() {
    val features = FeatureType.values().filterNot {
        it == FeatureType.ALL || (it == FeatureType.DRIVERDATA_DRIVERPROFILE && !DriveKitAccess.hasAccess(
            AccessType.DRIVER_PROFILE
        ))
    }
}
