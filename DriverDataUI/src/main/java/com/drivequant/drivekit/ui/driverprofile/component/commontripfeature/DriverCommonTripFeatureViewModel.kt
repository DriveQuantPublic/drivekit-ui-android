package com.drivequant.drivekit.ui.driverprofile.component.commontripfeature

import androidx.annotation.StringRes
import com.drivequant.drivekit.ui.R

internal data class DriverCommonTripFeatureViewModel(
    @StringRes val titleId: Int,
    private val distanceInKm: Int?,
    private val durationInMin: Int?,
    @StringRes val roadContextId: Int
) {
    val distance: Int = this.distanceInKm ?: 36
    val duration: Int = this.durationInMin ?: 34
    @StringRes val distanceUnitId: Int = R.string.dk_common_unit_kilometer
    val hasData = this.distanceInKm != null && this.durationInMin != null
}
