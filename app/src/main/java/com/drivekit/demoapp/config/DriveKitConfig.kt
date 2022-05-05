package com.drivekit.demoapp.config

import android.app.Application
import android.content.Context
import com.drivekit.demoapp.DriveKitDemoApplication
import com.drivekit.demoapp.drivekit.TripListenerController
import com.drivekit.demoapp.manager.DriveKitListenerManager
import com.drivekit.demoapp.notification.enum.DKNotificationChannel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.analytics.DKAnalyticsEvent
import com.drivequant.drivekit.common.ui.analytics.DKAnalyticsEventKey
import com.drivequant.drivekit.common.ui.analytics.DriveKitAnalyticsListener
import com.drivequant.drivekit.common.ui.listener.ContentMail
import com.drivequant.drivekit.common.ui.utils.ContactType
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.databaseutils.entity.*
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorType
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysisUI
import com.drivequant.drivekit.tripanalysis.TripListener
import com.drivequant.drivekit.tripanalysis.crashfeedback.activity.CrashFeedbackStep1Activity
import com.drivequant.drivekit.tripanalysis.entity.PostGeneric
import com.drivequant.drivekit.tripanalysis.entity.PostGenericResponse
import com.drivequant.drivekit.tripanalysis.entity.TripNotification
import com.drivequant.drivekit.tripanalysis.entity.TripPoint
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashAlert
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashFeedbackConfig
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashFeedbackNotification
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashInfo
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackSeverity
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackType
import com.drivequant.drivekit.tripanalysis.service.recorder.StartMode
import com.drivequant.drivekit.tripanalysis.service.recorder.State
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import kotlin.random.Random

/**
 * Created by Mohamed on 2020-05-14.
 */

internal object DriveKitConfig {

    // TODO: ENTER YOUR API KEY HERE
    private const val apiKey = ""

    fun configure(application: Application) {
        configureCore(application)
        configureCommonUI()
        configureTripAnalysis(application)
        configureDriverData()
        configureDriverAchievement()
        configurePermissionsUtils()
        configureVehicle()
    }

    private fun configureCore(application: Application) {
        DriveKit.initialize(application, DriveKitListenerManager)
        if (apiKey.isNotBlank()) {
            DriveKit.setApiKey(apiKey)
        }
    }

    private fun configureCommonUI() {
        DriveKitUI.initialize()
        DriveKitUI.configureAnalytics(object: DriveKitAnalyticsListener{
            override fun trackScreen(screen: String, className: String) {
                // TODO: manage screen tracking here
            }

            override fun trackEvent(event: DKAnalyticsEvent, parameters: Map<DKAnalyticsEventKey, Any>?) {
                // TODO: manage event tracking here
            }
        })
    }

    private fun configureTripAnalysis(context: Context) {
        TripListenerController.addTripListener(object : TripListener {
            override fun tripStarted(startMode: StartMode) {
                // Call when a trip start
            }

            override fun tripPoint(tripPoint: TripPoint) {
                // Call for each location registered during a trip
            }
            override fun tripSavedForRepost() {
                DriveKitDemoApplication.showNotification(
                    context,
                    context.getString(R.string.notif_trip_save_for_repost)
                )
            }
            override fun tripFinished(post: PostGeneric, response: PostGenericResponse) {}
            override fun beaconDetected() {}
            override fun sdkStateChanged(state: State) {}
            override fun potentialTripStart(startMode: StartMode) {}
            override fun crashDetected(crashInfo: DKCrashInfo) {}
            override fun crashFeedbackSent(crashInfo: DKCrashInfo, feedbackType: CrashFeedbackType, severity: CrashFeedbackSeverity) {}
        })
        DriveKitTripAnalysis.initialize(createForegroundNotification(context), TripListenerController)
        DriveKitTripAnalysis.activateAutoStart(true)
        DriveKitTripAnalysis.setVehiclesConfigTakeover(true)
        DriveKitTripAnalysisUI.initialize()
        DriveKitTripAnalysisUI.enableCrashFeedback(
            roadsideAssistanceNumber = "0000000000",
            DKCrashFeedbackConfig(
                notification = DKCrashFeedbackNotification(
                    icon = R.drawable.ic_launcher_background,
                    channelName = "${R.string.app_name} - Crash Detection Feedback",
                    notificationId = Random.nextInt(1, Integer.MAX_VALUE),
                    title = context.getString(R.string.dk_crash_detection_feedback_notif_title),
                    message = context.getString(R.string.dk_crash_detection_feedback_notif_message),
                    activity = CrashFeedbackStep1Activity::class.java,
                    crashAlert = DKCrashAlert.SILENCE
                ),
                crashVelocityThreshold = 0.0
            )
        )
    }

    private fun configureDriverData() {
        DriveKitDriverData.initialize()
        DriverDataUI.initialize()
        DriverDataUI.enableAlternativeTrips(true)
    }

    private fun configureDriverAchievement() {
        DriverAchievementUI.initialize()
        DriverAchievementUI.configureBadgeCategories(BadgeCategory.values().toMutableList())
        DriverAchievementUI.configureRankingTypes(RankingType.values().toList())

        val rankingPeriods = listOf(RankingPeriod.WEEKLY, RankingPeriod.MONTHLY, RankingPeriod.ALL_TIME)
        val rankingSelectorType = RankingSelectorType.PERIOD(rankingPeriods)
        DriverAchievementUI.configureRankingSelector(rankingSelectorType)
        DriverAchievementUI.configureRankingDepth(rankingDepth = 5)
    }

    private fun configurePermissionsUtils() {
        PermissionsUtilsUI.initialize()
        PermissionsUtilsUI.configureBluetooth(true)
        PermissionsUtilsUI.configureContactType(ContactType.EMAIL(object : ContentMail {
            override fun getBccRecipients(): List<String> = listOf("")
            override fun getMailBody() = "Mock mail body"
            override fun getRecipients(): List<String> = listOf("")
            override fun getSubject() = "Mock subject"
            override fun overrideMailBodyContent(): Boolean = false
        }))
    }

    private fun configureVehicle() {
        DriveKitVehicleUI.initialize()
        DriveKitVehicleUI.configureVehiclesTypes(listOf(VehicleType.CAR, VehicleType.TRUCK))
        DriveKitVehicleUI.configureBrands(VehicleBrand.values().asList())
        DriveKitVehicleUI.configureBeaconDetailEmail(object : ContentMail {
            override fun getRecipients() = listOf("")
            override fun getBccRecipients() = listOf("")
            override fun getSubject() = "Mock subject"
            override fun getMailBody() = "Mock mail body"
            override fun overrideMailBodyContent() = true
        })
        DriveKitVehicleUI.enableOdometer(true)
    }

    private fun createForegroundNotification(context: Context): TripNotification {
        val notification = TripNotification(
            context.getString(R.string.app_name),
            context.getString(R.string.notif_trip_started),
            R.drawable.ic_launcher_background
        )
        notification.enableCancel = true
        notification.cancel = context.getString(R.string.cancel_trip)
        notification.cancelIconId = R.drawable.ic_launcher_background
        notification.channelId = DKNotificationChannel.TRIP_STARTED.getChannelId()
        return notification
    }
}