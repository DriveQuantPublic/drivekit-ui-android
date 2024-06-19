package com.drivekit.demoapp.dashboard.enum

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI

internal enum class InfoBannerType {
    DIAGNOSIS;

    fun shouldDisplay(context: Context) = when (this) {
        DIAGNOSIS -> PermissionsUtilsUI.hasError(context)
    }

    @ColorInt
    fun getColor() = when (this) {
        DIAGNOSIS -> Color.WHITE
    }

    @ColorInt
    fun getBackgroundColor() = when (this) {
        DIAGNOSIS -> DKColors.warningColor
    }

    @DrawableRes
    fun getIconResId() = when (this) {
        DIAGNOSIS -> com.drivequant.drivekit.permissionsutils.R.drawable.dk_perm_utils_diagnosis_system
    }

    @StringRes
    fun getTitleResId() = when (this) {
        DIAGNOSIS -> R.string.info_banner_diagnosis_title
    }

    fun displayArrow() = when (this) {
        DIAGNOSIS -> true
    }
}
