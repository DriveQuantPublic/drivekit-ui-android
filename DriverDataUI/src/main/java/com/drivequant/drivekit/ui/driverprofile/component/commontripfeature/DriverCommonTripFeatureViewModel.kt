package com.drivequant.drivekit.ui.driverprofile.component.commontripfeature

import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKUnitSystem

internal data class DriverCommonTripFeatureViewModel(
    @StringRes val titleId: Int,
    val distance: Int,
    val duration: Int,
    @StringRes val roadContextId: Int,
    val isRealData: Boolean
) {
    @StringRes val distanceUnitId: Int = when (DriveKitUI.unitSystem) {
        DKUnitSystem.METRIC -> com.drivequant.drivekit.common.ui.R.string.dk_common_unit_kilometer
        DKUnitSystem.IMPERIAL -> com.drivequant.drivekit.common.ui.R.string.dk_common_unit_mile
    }
}
