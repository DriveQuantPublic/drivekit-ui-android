package com.drivekit.demoapp.onboarding.viewmodel

import android.content.Context
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit

class ApiKeyViewModel {

    fun isApiKeyValid() = DriveKit.config.apiKey?.let {
        it.isNotBlank() && !it.isKeyPlaceHolder()
    }?: false

    fun getButtonText() = if (isApiKeyValid()) {
        R.string.button_next_step
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

fun String.isKeyPlaceHolder(): Boolean {
    val keyPlaceHolderRegEx = "[A-Z]{5}(_[A-Z]{3,4}){4}"
    return keyPlaceHolderRegEx.toRegex().matches(this)
}