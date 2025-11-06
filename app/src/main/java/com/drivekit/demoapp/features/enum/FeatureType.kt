package com.drivekit.demoapp.features.enum

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.drivekit.drivekitdemoapp.R

internal enum class FeatureType {
    ALL,
    DRIVERDATA_TRIPS,
    DRIVERDATA_TIMELINE,
    DRIVERDATA_MYSYNTHESIS,
    DRIVERDATA_DRIVERPROFILE,
    PERMISSIONSUTILS_ONBOARDING,
    PERMISSIONSUTILS_DIAGNOSIS,
    VEHICLE_LIST,
    VEHICLE_ODOMETER,
    VEHICLE_FIND_MY_VEHICLE,
    CHALLENGE_LIST,
    DRIVERACHIEVEMENT_RANKING,
    DRIVERACHIEVEMENT_BADGES,
    DRIVERACHIEVEMENT_STREAKS,
    TRIPANALYSIS_WORKINGHOURS,
    TRIPANALYSIS_TRIPSHARING;

    @DrawableRes
    fun getIconResId() = when (this) {
        ALL -> null
        DRIVERDATA_TRIPS -> R.drawable.ic_feature_driverdata_trips
        PERMISSIONSUTILS_ONBOARDING,
        PERMISSIONSUTILS_DIAGNOSIS -> R.drawable.ic_feature_permissionsutils
        VEHICLE_LIST,
        VEHICLE_ODOMETER,
        VEHICLE_FIND_MY_VEHICLE -> R.drawable.ic_feature_vehicle
        CHALLENGE_LIST -> R.drawable.ic_feature_challenge
        DRIVERACHIEVEMENT_RANKING -> R.drawable.ic_feature_driverachievement_ranking
        DRIVERACHIEVEMENT_BADGES -> R.drawable.ic_feature_driverachievement_badges
        DRIVERACHIEVEMENT_STREAKS -> R.drawable.ic_feature_driverachievement_streaks
        TRIPANALYSIS_WORKINGHOURS -> R.drawable.ic_feature_tripanalysis_workinghours
        TRIPANALYSIS_TRIPSHARING -> R.drawable.ic_feature_tripanalysis_location_sharing
        DRIVERDATA_TIMELINE -> R.drawable.ic_feature_driverdata_timeline
        DRIVERDATA_MYSYNTHESIS -> R.drawable.ic_feature_my_driving
        DRIVERDATA_DRIVERPROFILE -> R.drawable.ic_feature_profile
    }

    @StringRes
    fun getTitleResId() = when (this) {
        ALL -> R.string.feature_list
        DRIVERDATA_TRIPS -> R.string.feature_trip_list_title
        PERMISSIONSUTILS_ONBOARDING -> R.string.feature_permission_utils_onboarding_title
        PERMISSIONSUTILS_DIAGNOSIS -> R.string.feature_permission_utils_title
        VEHICLE_LIST -> R.string.feature_vehicle_title
        VEHICLE_ODOMETER -> R.string.feature_vehicle_odometer_title
        VEHICLE_FIND_MY_VEHICLE -> R.string.feature_find_vehicle_title
        CHALLENGE_LIST -> R.string.feature_challenges_title
        DRIVERACHIEVEMENT_RANKING -> R.string.feature_ranking_title
        DRIVERACHIEVEMENT_BADGES -> R.string.feature_badges_title
        DRIVERACHIEVEMENT_STREAKS -> R.string.feature_streaks_title
        TRIPANALYSIS_WORKINGHOURS -> R.string.feature_working_hours_title
        TRIPANALYSIS_TRIPSHARING -> R.string.feature_location_sharing_title
        DRIVERDATA_TIMELINE -> R.string.feature_timeline_title
        DRIVERDATA_MYSYNTHESIS -> R.string.feature_synthesis_title
        DRIVERDATA_DRIVERPROFILE -> R.string.feature_profile_title
    }

    @StringRes
    fun getDescriptionResId() = when (this) {
        ALL -> R.string.feature_list_description
        DRIVERDATA_TRIPS -> R.string.feature_trip_list_description
        PERMISSIONSUTILS_ONBOARDING -> R.string.feature_permission_utils_onboarding_description
        PERMISSIONSUTILS_DIAGNOSIS -> R.string.feature_permission_utils_description
        VEHICLE_LIST -> R.string.feature_vehicle_description
        VEHICLE_ODOMETER -> R.string.feature_vehicle_odometer_description
        VEHICLE_FIND_MY_VEHICLE -> R.string.feature_find_vehicle_description
        CHALLENGE_LIST -> R.string.feature_challenges_description
        DRIVERACHIEVEMENT_RANKING -> R.string.feature_ranking_description
        DRIVERACHIEVEMENT_BADGES -> R.string.feature_badges_description
        DRIVERACHIEVEMENT_STREAKS -> R.string.feature_streaks_description
        TRIPANALYSIS_WORKINGHOURS -> R.string.feature_working_hours_description
        TRIPANALYSIS_TRIPSHARING -> R.string.feature_location_sharing_description
        DRIVERDATA_TIMELINE -> R.string.feature_timeline_description
        DRIVERDATA_MYSYNTHESIS -> R.string.feature_synthesis_description
        DRIVERDATA_DRIVERPROFILE -> R.string.feature_profile_description
    }

    @StringRes
    fun getInfoUrlResId() = when (this) {
        ALL -> null
        DRIVERDATA_TRIPS -> R.string.drivekit_doc_android_driver_data
        PERMISSIONSUTILS_ONBOARDING -> R.string.drivekit_doc_android_permissions_management
        PERMISSIONSUTILS_DIAGNOSIS -> R.string.drivekit_doc_android_diag
        VEHICLE_LIST -> R.string.drivekit_doc_android_vehicle_list
        VEHICLE_ODOMETER -> R.string.drivekit_doc_android_odometer
        VEHICLE_FIND_MY_VEHICLE -> R.string.drivekit_doc_android_find_vehicle
        CHALLENGE_LIST -> R.string.drivekit_doc_android_challenges
        DRIVERACHIEVEMENT_RANKING -> R.string.drivekit_doc_android_ranking
        DRIVERACHIEVEMENT_BADGES -> R.string.drivekit_doc_android_badges
        DRIVERACHIEVEMENT_STREAKS -> R.string.drivekit_doc_android_streaks
        TRIPANALYSIS_WORKINGHOURS -> R.string.drivekit_doc_android_working_hours
        TRIPANALYSIS_TRIPSHARING -> R.string.drivekit_doc_android_location_sharing
        DRIVERDATA_TIMELINE -> R.string.drivekit_doc_android_timeline
        DRIVERDATA_MYSYNTHESIS -> R.string.drivekit_doc_android_synthesis
        DRIVERDATA_DRIVERPROFILE -> R.string.drivekit_doc_android_profile
    }

    @StringRes
    fun getActionButtonTitleResId() = when (this) {
        ALL -> R.string.button_see_features
        else -> R.string.button_see_feature
    }
}
