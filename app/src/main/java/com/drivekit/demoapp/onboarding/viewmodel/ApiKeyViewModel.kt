package com.drivekit.demoapp.onboarding.viewmodel

import android.content.Context
import androidx.annotation.StringRes
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit

internal class ApiKeyViewModel {

    fun isApiKeyValid() = DriveKit.config.apiKey?.isNotBlank() ?: false

    @StringRes
    fun getButtonTextResId() = if (isApiKeyValid()) {
        R.string.welcome_ok_button
    } else {
        R.string.button_see_documentation
    }

    fun getDescription(context: Context) = if (isApiKeyValid()) {
        DriveKit.config.apiKey.let {
            DKResource.buildString(
                context,
                DKColors.complementaryFontColor,
                DKColors.primaryColor,
                R.string.welcome_ok_description,
                it!!
            )
        }
    } else {
        context.getString(R.string.welcome_ko_description)
    }

    @StringRes
    fun getTitleResId() = if (isApiKeyValid()) {
        R.string.welcome_ok_title
    } else {
        R.string.welcome_ko_title
    }
}
