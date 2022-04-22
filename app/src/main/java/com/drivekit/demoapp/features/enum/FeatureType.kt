package com.drivekit.demoapp.features.enum

import com.drivekit.drivekitdemoapp.R

internal enum class FeatureType {
    ALL,
    DRIVERDATA_TRIPS,
    PERMISSIONSUTILS_ONBOARDING,
    PERMISSIONSUTILS_DIAGNOSIS,
    VEHICLE_LIST,
    VEHICLE_ODOMETER,
    CHALLENGE_LIST,
    DRIVERACHIEVEMENT_RANKING,
    DRIVERACHIEVEMENT_BADGES,
    DRIVERACHIEVEMENT_STREAKS,
    TRIPANALYSIS_WORKINGHOURS;

    fun getIcon() = when (this) {
        ALL -> null
        DRIVERDATA_TRIPS -> R.drawable.dk_leaf_tab_icon
        PERMISSIONSUTILS_ONBOARDING -> R.drawable.dk_leaf_tab_icon
        PERMISSIONSUTILS_DIAGNOSIS -> R.drawable.dk_leaf_tab_icon
        VEHICLE_LIST -> R.drawable.dk_leaf_tab_icon
        VEHICLE_ODOMETER -> R.drawable.dk_leaf_tab_icon
        CHALLENGE_LIST -> R.drawable.dk_leaf_tab_icon
        DRIVERACHIEVEMENT_RANKING -> R.drawable.dk_leaf_tab_icon
        DRIVERACHIEVEMENT_BADGES -> R.drawable.dk_leaf_tab_icon
        DRIVERACHIEVEMENT_STREAKS -> R.drawable.dk_leaf_tab_icon
        TRIPANALYSIS_WORKINGHOURS -> R.drawable.dk_leaf_tab_icon
    }

    fun getTitleResId() = when (this) {
        ALL -> R.string.feature_list
        DRIVERDATA_TRIPS -> R.string.feature_trip_list_title
        PERMISSIONSUTILS_ONBOARDING -> R.string.feature_permission_utils_onboarding_title
        PERMISSIONSUTILS_DIAGNOSIS -> R.string.feature_permission_utils_title
        VEHICLE_LIST -> R.string.feature_vehicle_title
        VEHICLE_ODOMETER -> R.string.feature_vehicle_odometer_title
        CHALLENGE_LIST -> R.string.feature_challenges_title
        DRIVERACHIEVEMENT_RANKING -> R.string.feature_ranking_title
        DRIVERACHIEVEMENT_BADGES -> R.string.feature_badges_title
        DRIVERACHIEVEMENT_STREAKS -> R.string.feature_streaks_title
        TRIPANALYSIS_WORKINGHOURS -> R.string.feature_working_hours_title
    }
    fun getDescriptionResId() = when (this) {
        ALL -> R.string.feature_list_description
        DRIVERDATA_TRIPS -> R.string.feature_trip_list_description
        PERMISSIONSUTILS_ONBOARDING -> R.string.feature_permission_utils_onboarding_description
        PERMISSIONSUTILS_DIAGNOSIS -> R.string.feature_permission_utils_description
        VEHICLE_LIST -> R.string.feature_vehicle_description
        VEHICLE_ODOMETER -> R.string.feature_vehicle_odometer_description
        CHALLENGE_LIST -> R.string.feature_challenges_description
        DRIVERACHIEVEMENT_RANKING -> R.string.feature_ranking_description
        DRIVERACHIEVEMENT_BADGES -> R.string.feature_badges_description
        DRIVERACHIEVEMENT_STREAKS -> R.string.feature_streaks_description
        TRIPANALYSIS_WORKINGHOURS -> R.string.feature_working_hours_description
    }

    fun getActionButtonTitleResId() = R.string.button_see_feature
}