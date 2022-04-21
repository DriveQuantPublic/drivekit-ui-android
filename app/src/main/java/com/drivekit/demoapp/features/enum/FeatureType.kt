package com.drivekit.demoapp.features.enum

import com.drivekit.drivekitdemoapp.R

internal enum class FeatureType {
    ALL, // TODO reuse it in dashboard viewmodel
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

    fun getIcon() = R.drawable.dk_leaf_tab_icon

    fun getTitleResId() = R.string.add_beacon_title

    fun getDescriptionResId() = R.string.user_id_description

    fun getActionButtonTitleResId() = R.string.autostart_title

    fun hasInfo() = true
}