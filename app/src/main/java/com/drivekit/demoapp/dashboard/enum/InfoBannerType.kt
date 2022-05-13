package com.drivekit.demoapp.dashboard.enum

import android.content.Context
import android.graphics.Color
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI

internal enum class InfoBannerType {
    DIAGNOSIS;

    fun shouldDisplay(context: Context) = when (this) {
        DIAGNOSIS -> PermissionsUtilsUI.hasError(context)
    }

    fun getColor() = when (this) {
        DIAGNOSIS -> Color.WHITE
    }

    fun getBackgroundColorResId() = when (this) {
        DIAGNOSIS -> DriveKitUI.colors.warningColor()
    }

    fun getIconResId() = when (this) {
        DIAGNOSIS -> R.drawable.dk_perm_utils_diagnosis_system
    }

    fun getTitleResId() = when (this) {
        DIAGNOSIS -> R.string.info_banner_diagnosis_title
    }

    fun displayArrow() = when (this) {
        DIAGNOSIS -> true
    }
}