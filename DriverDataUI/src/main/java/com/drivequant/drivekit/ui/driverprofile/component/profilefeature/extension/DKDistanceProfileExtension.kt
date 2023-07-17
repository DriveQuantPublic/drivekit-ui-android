package com.drivequant.drivekit.ui.driverprofile.component.profilefeature.extension

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.drivequant.drivekit.driverdata.driverprofile.DKDistanceProfile
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureDescription
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureViewModel

internal fun DKDistanceProfile.getViewModel() = DriverProfileFeatureViewModel(
    this.getTitleId(),
    this.getDescription(),
    this.getIconId()
)

@StringRes
private fun DKDistanceProfile.getTitleId(): Int = when (this) {
    DKDistanceProfile.VERY_SHORT -> R.string.dk_driverdata_profile_distance_very_short_title
    DKDistanceProfile.SHORT -> R.string.dk_driverdata_profile_distance_short_title
    DKDistanceProfile.MEDIUM -> R.string.dk_driverdata_profile_distance_medium_title
    DKDistanceProfile.LONG -> R.string.dk_driverdata_profile_distance_long_title
    DKDistanceProfile.VERY_LONG -> R.string.dk_driverdata_profile_distance_very_long_title
}

private fun DKDistanceProfile.getDescription(): DriverProfileFeatureDescription {
    val stringId = when (this) {
        DKDistanceProfile.VERY_SHORT -> R.string.dk_driverdata_profile_distance_very_short_text
        DKDistanceProfile.SHORT -> R.string.dk_driverdata_profile_distance_short_text
        DKDistanceProfile.MEDIUM -> R.string.dk_driverdata_profile_distance_medium_text
        DKDistanceProfile.LONG -> R.string.dk_driverdata_profile_distance_long_text
        DKDistanceProfile.VERY_LONG -> R.string.dk_driverdata_profile_distance_very_long_text
    }
    return DriverProfileFeatureDescription.SimpleDescription(stringId)
}

@DrawableRes
private fun DKDistanceProfile.getIconId(): Int = R.drawable.dk_profile_distance
