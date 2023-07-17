package com.drivequant.drivekit.ui.driverprofile.component.profilefeature.extension

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.driverdata.driverprofile.DKRegularityProfile
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureDescription
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureViewModel

internal fun DKRegularityProfile.getViewModel(tripNumberMean: Int, distanceMean: Int) = DriverProfileFeatureViewModel(
    this.getTitleId(),
    this.getDescription(tripNumberMean, distanceMean),
    this.getIconId()
)

@StringRes
internal fun DKRegularityProfile.getTitleId(): Int = when (this) {
    DKRegularityProfile.REGULAR -> R.string.dk_driverdata_profile_regularity_regular_title
    DKRegularityProfile.INTERMITTENT -> R.string.dk_driverdata_profile_regularity_intermittent_title
}

internal fun DKRegularityProfile.getDescription(
    tripNumberMean: Int,
    distanceMean: Int
): DriverProfileFeatureDescription = when (this) {
    DKRegularityProfile.REGULAR -> DriverProfileFeatureDescription.ParameterizedDescription(
        R.string.dk_driverdata_profile_regularity_regular_text,
        tripNumberMean.toDouble().format(0),
        distanceMean.toDouble().format(0)
    )

    DKRegularityProfile.INTERMITTENT -> DriverProfileFeatureDescription.SimpleDescription(R.string.dk_driverdata_profile_regularity_intermittent_text)
}

@DrawableRes
internal fun DKRegularityProfile.getIconId(): Int = R.drawable.dk_profile_regularity
