package com.drivekit.demoapp.onboarding.viewmodel

import android.content.Context
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit

internal class ApiKeyViewModel {

    fun isApiKeyValid() = DriveKit.config.apiKey?.isNotBlank() ?: false

    fun getButtonText() = if (isApiKeyValid()) {
        R.string.welcome_ok_button
    } else {
        R.string.button_see_documentation
    }

    fun getDescription(context: Context) = if (isApiKeyValid()) {
        DriveKit.config.apiKey.let {
            DKResource.buildString(
                context,
                DriveKitUI.colors.complementaryFontColor(),
                DriveKitUI.colors.primaryColor(),
                "welcome_ok_description",
                it!!
            )
        }
    } else {
        context.getString(R.string.welcome_ko_description)
    }

    fun getTitle() = if (isApiKeyValid()) {
        R.string.welcome_ok_title
    } else {
        R.string.welcome_ko_title
    }
}