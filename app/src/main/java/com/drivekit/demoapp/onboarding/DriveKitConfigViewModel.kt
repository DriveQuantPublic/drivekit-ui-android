package com.drivekit.demoapp.onboarding

import android.content.Context
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitSharedPreferencesUtils

class DriveKitConfigViewModel {

    fun isApiKeyValid() = DriveKit.isApiKey()

    fun getButtonText(context: Context) = if (isApiKeyValid()) {
        "text_button_next"
    } else {
        "text_button_documentation"
    }.let {
        DKResource.convertToString(context, it)
    }

    fun getDescription(context: Context) = if (isApiKeyValid()) {
        val apiKey = DriveKitSharedPreferencesUtils.getString("drivekit-api-key")
        DKResource.buildString(
            context,
            DriveKitUI.colors.complementaryFontColor(),
            DriveKitUI.colors.complementaryFontColor(),
            "text_description_api_key",
            apiKey!!
        )
    } else {
        DKResource.convertToString(context, "text_descrption_api_key_error")
    }

    fun getTitle(context: Context) = if (isApiKeyValid()) {
        "text_title_api_key"
    } else {
        "text_title_api_key_error"
    }.let {
        DKResource.convertToString(context, it)
    }
}