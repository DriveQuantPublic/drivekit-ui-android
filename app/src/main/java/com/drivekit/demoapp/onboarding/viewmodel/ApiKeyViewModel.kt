package com.drivekit.demoapp.onboarding.viewmodel

import android.content.Context
import com.drivekit.demoapp.config.DriveKitConfig
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit

class ApiKeyViewModel {

    fun isApiKeyValid() = DriveKit.config.apiKey?.let {
        it.isNotBlank() && it != DriveKitConfig.PLACEHOLDER
    }?: false

    fun getButtonText(context: Context) = if (isApiKeyValid()) {
        "button_next_step"
    } else {
        "button_see_documentation"
    }.let {
        DKResource.convertToString(context, it)
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
        DKResource.convertToString(context, "welcome_ko_description")
    }

    fun getTitle(context: Context) = if (isApiKeyValid()) {
        "welcome_ok_title"
    } else {
        "welcome_ko_title"
    }.let {
        DKResource.convertToString(context, it)
    }
}