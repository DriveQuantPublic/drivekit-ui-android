package com.drivekit.demoapp.config

import android.content.Context
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.listener.ContentMail
import com.drivequant.drivekit.common.ui.utils.ContactType
import com.drivequant.drivekit.databaseutils.entity.BadgeCategory
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI

/**
 * Created by Mohamed on 2020-05-14.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

object DriveKitConfig {

    fun configureDriveKitUI(context: Context) {
        DriveKitUI.initialize()
        //DriveKitUI.initialize(fonts = FontConfig(), colors = ColorConfig(context))
    }

    fun configureDriverAchievement() {
        DriverAchievementUI.initialize()
        DriverAchievementUI.configureBadgeCategories(
            mutableListOf(
                BadgeCategory.ECO_DRIVING,
                BadgeCategory.GENERIC,
                BadgeCategory.SAFETY,
                BadgeCategory.PHONE_DISTRACTION
            )
        )
    }

    fun configurePermissionsUtils(context: Context) {
        PermissionsUtilsUI.initialize()
        PermissionsUtilsUI.configureBluetooth(true)
        PermissionsUtilsUI.configureDiagnosisLogs(true)
        PermissionsUtilsUI.configureLogPathFile("/DQ-demo-test/")
        PermissionsUtilsUI.configureContactType(ContactType.EMAIL(object : ContentMail {
            override fun getBccRecipients(): List<String> = listOf("support@drivequant.com")
            override fun getMailBody(): String = "Mail body"
            override fun getRecipients(): List<String> = listOf("support@drivequant.com")
            override fun getSubject(): String = "${context.getString(R.string.app_name)} - ${context.getString(R.string.ask_for_request)}"
            override fun overrideMailBodyContent(): Boolean = false
        }))
    }
}