package com.drivequant.drivekit.ui.driverprofile.component.profilefeature.extension

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureDescription
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureViewModel

internal fun RoadContext.getViewModel(distancePercentage: Double?) = DriverProfileFeatureViewModel(
    this.getTitleId(),
    this.getDescription(distancePercentage),
    this.getIconId()
)

@StringRes
private fun RoadContext.getTitleId(): Int = when (this) {
    RoadContext.CITY -> R.string.dk_driverdata_profile_roadcontext_city_title
    RoadContext.EXPRESSWAYS -> R.string.dk_driverdata_profile_roadcontext_expressways_title
    RoadContext.HEAVY_URBAN_TRAFFIC -> R.string.dk_driverdata_profile_roadcontext_heavy_urban_title
    RoadContext.SUBURBAN -> R.string.dk_driverdata_profile_roadcontext_suburban_title
    RoadContext.TRAFFIC_JAM -> R.string.dk_driverdata_profile_roadcontext_traffic_jam_title
}

private fun RoadContext.getDescription(
    distancePercentage: Double?
): DriverProfileFeatureDescription {
    val formattedDistance = distancePercentage?.format(0) ?: "-"
    @StringRes val stringId = when (this) {
        RoadContext.CITY -> R.string.dk_driverdata_profile_roadcontext_city_text
        RoadContext.EXPRESSWAYS -> R.string.dk_driverdata_profile_roadcontext_expressways_text
        RoadContext.HEAVY_URBAN_TRAFFIC -> R.string.dk_driverdata_profile_roadcontext_heavy_urban_text
        RoadContext.SUBURBAN -> R.string.dk_driverdata_profile_roadcontext_suburban_text
        RoadContext.TRAFFIC_JAM -> R.string.dk_driverdata_profile_roadcontext_traffic_jam_text
    }
    return DriverProfileFeatureDescription.ParametrizedDescription(stringId, formattedDistance)
}

@DrawableRes
private fun RoadContext.getIconId(): Int = R.drawable.dk_profile_context
