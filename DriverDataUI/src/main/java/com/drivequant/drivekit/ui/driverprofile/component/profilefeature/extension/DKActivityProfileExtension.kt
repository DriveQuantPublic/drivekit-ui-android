package com.drivequant.drivekit.ui.driverprofile.component.profilefeature.extension

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.drivequant.drivekit.driverdata.driverprofile.DKActivityProfile
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureDescription
import com.drivequant.drivekit.ui.driverprofile.component.profilefeature.DriverProfileFeatureViewModel

internal fun DKActivityProfile.getViewModel(activeWeekPercentage: Int) = DriverProfileFeatureViewModel(
    this.getTitleId(),
    this.getDescription(activeWeekPercentage),
    this.getIconId()
)

@StringRes
private fun DKActivityProfile.getTitleId(): Int = when (this) {
    DKActivityProfile.LOW -> R.string.dk_driverdata_profile_activity_low_title
    DKActivityProfile.MEDIUM -> R.string.dk_driverdata_profile_activity_medium_title
    DKActivityProfile.HIGH -> R.string.dk_driverdata_profile_activity_high_title
}

private fun DKActivityProfile.getDescription(activeWeekPercentage: Int): DriverProfileFeatureDescription = when (activeWeekPercentage) {
    in 0..10 -> DriverProfileFeatureDescription.SimpleDescription(R.string.dk_driverdata_profile_activity_very_low_text)
    in 11..18 -> DriverProfileFeatureDescription.PluralDescription(R.plurals.dk_driverdata_profile_activity_main_text, 1, "1", "6")
    in 19..22 -> DriverProfileFeatureDescription.PluralDescription(R.plurals.dk_driverdata_profile_activity_main_text, 1, "1", "5")
    in 23..27 -> DriverProfileFeatureDescription.PluralDescription(R.plurals.dk_driverdata_profile_activity_main_text, 1, "1", "4")
    in 28..38 -> DriverProfileFeatureDescription.PluralDescription(R.plurals.dk_driverdata_profile_activity_main_text, 1, "1", "3")
    in 39..57 -> DriverProfileFeatureDescription.PluralDescription(R.plurals.dk_driverdata_profile_activity_main_text, 1, "1", "2")
    in 58..71 -> DriverProfileFeatureDescription.PluralDescription(R.plurals.dk_driverdata_profile_activity_main_text, 1, "2", "3")
    in 72..78 -> DriverProfileFeatureDescription.PluralDescription(R.plurals.dk_driverdata_profile_activity_main_text, 1, "3", "4")
    in 79..82 -> DriverProfileFeatureDescription.PluralDescription(R.plurals.dk_driverdata_profile_activity_main_text, 1, "4", "5")
    in 83..89 -> DriverProfileFeatureDescription.PluralDescription(R.plurals.dk_driverdata_profile_activity_main_text, 1, "5", "6")
    in 90..99 -> DriverProfileFeatureDescription.SimpleDescription(R.string.dk_driverdata_profile_activity_often_text)
    100 -> DriverProfileFeatureDescription.SimpleDescription(R.string.dk_driverdata_profile_activity_always_text)
    else -> throw IllegalArgumentException("Invalid percentage: $activeWeekPercentage")
}

@DrawableRes
private fun DKActivityProfile.getIconId(): Int = R.drawable.dk_profile_activity
