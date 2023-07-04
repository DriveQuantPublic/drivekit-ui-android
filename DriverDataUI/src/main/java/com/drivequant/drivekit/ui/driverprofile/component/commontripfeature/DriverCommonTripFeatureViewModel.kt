package com.drivequant.drivekit.ui.driverprofile.component.commontripfeature

import androidx.annotation.StringRes
import com.drivequant.drivekit.ui.R

internal data class DriverCommonTripFeatureViewModel(
    @StringRes val titleId: Int,
    val distance: Int?,
    val duration: Int?,
    @StringRes val roadContextId: Int
) {
    @StringRes val distanceUnitId: Int = R.string.dk_common_unit_kilometer
    val hasData: Boolean
        get() = this.distance != null && this.duration != null
}
