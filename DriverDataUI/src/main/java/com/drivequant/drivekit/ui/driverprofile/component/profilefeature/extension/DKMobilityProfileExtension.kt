package com.drivequant.drivekit.ui.driverprofile.component.profilefeature.extension

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.driverdata.driverprofile.DKMobilityProfile
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureDescription
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureViewModel

internal fun DKMobilityProfile.getViewModel(radius: Int?) = DriverProfileFeatureViewModel(
    this.getTitleId(),
    this.getDescription(radius),
    this.getIconId()
)

@StringRes
private fun DKMobilityProfile.getTitleId(): Int = when (this) {
    DKMobilityProfile.NARROW -> R.string.dk_driverdata_profile_mobility_narrow
    DKMobilityProfile.SMALL -> R.string.dk_driverdata_profile_mobility_small
    DKMobilityProfile.MEDIUM -> R.string.dk_driverdata_profile_mobility_moderate
    DKMobilityProfile.LARGE -> R.string.dk_driverdata_profile_mobility_large
    DKMobilityProfile.WIDE -> R.string.dk_driverdata_profile_mobility_wide
    DKMobilityProfile.VAST -> R.string.dk_driverdata_profile_mobility_vast
}

private fun DKMobilityProfile.getDescription(radius: Int?): DriverProfileFeatureDescription {
    return DriverProfileFeatureDescription.ParameterizedDescription(
        R.string.dk_driverdata_profile_mobility_text,
        radius?.toDouble()?.format(0) ?: "-"
    )
}

@DrawableRes
private fun DKMobilityProfile.getIconId(): Int = R.drawable.dk_profile_mobility
