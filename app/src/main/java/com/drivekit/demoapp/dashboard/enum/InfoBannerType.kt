package com.drivekit.demoapp.dashboard.enum

import android.content.Context
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI

internal enum class InfoBannerType {
    DIAGNOSIS;

    fun shouldDisplay(context: Context) = PermissionsUtilsUI.hasError(context)

    fun getBackgroundColorResId() = DriveKitUI.colors.warningColor()

    fun getIconResId() = R.drawable.dk_perm_utils_diagnosis_system

    fun getTitleResId() = R.string.info_banner_diagnosis_title

    fun getArrowIconResId() = R.drawable.dk_common_arrow_forward
}