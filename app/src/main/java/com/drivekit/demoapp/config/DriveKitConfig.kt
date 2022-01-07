package com.drivekit.demoapp.config

import android.content.Context
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.analytics.DKAnalyticsEvent
import com.drivequant.drivekit.common.ui.analytics.DKAnalyticsEventKey
import com.drivequant.drivekit.common.ui.analytics.DriveKitAnalyticsListener
import com.drivequant.drivekit.common.ui.listener.ContentMail
import com.drivequant.drivekit.common.ui.utils.ContactType
import com.drivequant.drivekit.databaseutils.entity.*
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorType
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI

/**
 * Created by Mohamed on 2020-05-14.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

object DriveKitConfig {

    fun configureDriveKitUI() {
        DriveKitUI.initialize()
        DriveKitUI.configureAnalytics(object: DriveKitAnalyticsListener{
            override fun trackScreen(screen: String, className: String) {
                // TODO: manage screen tracking here
            }

            override fun trackEvent(event: DKAnalyticsEvent, parameters: Map<DKAnalyticsEventKey, Any>?) {
                // TODO: manage event tracking here
            }
        })
        //DriveKitUI.initialize(fonts = FontConfig(), colors = ColorConfig(context))
    }

    fun configureDriverAchievement() {
        DriverAchievementUI.initialize()
        DriverAchievementUI.configureBadgeCategories(BadgeCategory.values().toMutableList())
        DriverAchievementUI.configureRankingTypes(RankingType.values().toList())

        val rankingPeriods = listOf(RankingPeriod.WEEKLY, RankingPeriod.MONTHLY, RankingPeriod.ALL_TIME)
        val rankingSelectorType = RankingSelectorType.PERIOD(rankingPeriods)
        DriverAchievementUI.configureRankingSelector(rankingSelectorType)

        val rankingDepthValue = 5
        DriverAchievementUI.configureRankingDepth(rankingDepthValue)
    }

    fun configurePermissionsUtils(context: Context) {
        PermissionsUtilsUI.initialize()
        PermissionsUtilsUI.configureBluetooth(true)
        PermissionsUtilsUI.configureContactType(ContactType.EMAIL(object : ContentMail {
            override fun getBccRecipients(): List<String> = listOf("support@drivequant.com")
            override fun getMailBody(): String = "Mail body"
            override fun getRecipients(): List<String> = listOf("support@drivequant.com")
            override fun getSubject(): String = "${context.getString(R.string.app_name)} - ${context.getString(R.string.ask_for_request)}"
            override fun overrideMailBodyContent(): Boolean = false
        }))
    }
}