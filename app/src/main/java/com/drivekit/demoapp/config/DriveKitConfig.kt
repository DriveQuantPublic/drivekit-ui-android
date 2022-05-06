package com.drivekit.demoapp.config

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.drivekit.demoapp.drivekit.TripListenerController
import com.drivekit.demoapp.manager.DriveKitListenerManager
import com.drivekit.demoapp.notification.controller.DKNotificationManager
import com.drivekit.demoapp.notification.enum.DKNotificationChannel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.challenge.DriveKitChallenge
import com.drivequant.drivekit.challenge.ui.ChallengeUI
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.analytics.DKAnalyticsEvent
import com.drivequant.drivekit.common.ui.analytics.DKAnalyticsEventKey
import com.drivequant.drivekit.common.ui.analytics.DriveKitAnalyticsListener
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.listener.ContentMail
import com.drivequant.drivekit.common.ui.utils.ContactType
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.databaseutils.entity.*
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorType
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysisUI
import com.drivequant.drivekit.tripanalysis.crashfeedback.activity.CrashFeedbackStep1Activity
import com.drivequant.drivekit.tripanalysis.entity.TripNotification
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashAlert
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashFeedbackConfig
import com.drivequant.drivekit.tripanalysis.model.crashdetection.DKCrashFeedbackNotification
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleType
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import kotlin.random.Random

/**
 * Created by Mohamed on 2020-05-14.
 */

internal object DriveKitConfig {
    // ===============================
    // ↓↓↓ ENTER YOUR API KEY HERE ↓↓↓
    // ===============================
    private const val apiKey = "W4nDvNst9r7Cd1xmIB1eiZiE"

    private const val TRIP_ANALYSIS_AUTO_START_PREF_KEY = "tripAnalysisAutoStartPrefKey"

    private const val enableTripAnalysisCrashDetection: Boolean = true

    private val tripData: TripData = TripData.SAFETY
    private const val enableAlternativeTrips: Boolean = true

    private val vehicleTypes: List<VehicleType> = VehicleType.values().toList()
    private val vehicleBrands: List<VehicleBrand> = VehicleBrand.values().toList()
    private const val enableVehicleOdometer: Boolean = true

    fun configure(application: Application) {
        // Manage trips saved for repost notification
        DKNotificationManager.configure()

        // Internal modules configuration:
        configureCore(application)
        configureTripAnalysis(application)
        configureDriverData()

        // UI modules configuration:
        configureCommonUI()
        configureDriverDataUI()
        configureVehicleUI()
        configureTripAnalysisUI(application)
        configureDriverAchievementUI()
        configurePermissionsUtilsUI()
        configureChallengeUI()
    }

    fun isTripAnalysisAutoStartedEnabled(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(TRIP_ANALYSIS_AUTO_START_PREF_KEY, true)

    fun enableTripAnalysisAutoStart(context: Context, activate: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(TRIP_ANALYSIS_AUTO_START_PREF_KEY, activate).apply()
        DriveKitTripAnalysis.activateAutoStart(activate)
    }

    private fun configureCore(application: Application) {
        DriveKit.initialize(application, DriveKitListenerManager)
        if (apiKey.isNotBlank()) {
            DriveKit.setApiKey(apiKey)
        }
    }

    private fun configureTripAnalysis(context: Context) {
        DriveKitTripAnalysis.initialize(createForegroundNotification(context), TripListenerController)
        DriveKitTripAnalysis.activateAutoStart(isTripAnalysisAutoStartedEnabled(context))
        DriveKitTripAnalysis.activateCrashDetection(enableTripAnalysisCrashDetection)

        // You must call this method if you use DriveKit Vehicle component:
        DriveKitTripAnalysis.setVehiclesConfigTakeover(true)
    }

    private fun configureDriverData() {
        DriveKitDriverData.initialize()
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

    private fun configureDriverDataUI() {
        DriverDataUI.initialize(tripData = tripData)
        DriverDataUI.enableAlternativeTrips(enableAlternativeTrips)
    }

    private fun configureVehicleUI() {
        DriveKitVehicleUI.initialize()
        DriveKitVehicleUI.enableOdometer(enableVehicleOdometer)
        DriveKitVehicleUI.configureVehiclesTypes(vehicleTypes)
        DriveKitVehicleUI.configureBrands(vehicleBrands)
        DriveKitVehicleUI.configureBeaconDetailEmail(object : ContentMail {
            override fun getRecipients() = listOf("recipient_to_configure@mail.com")
            override fun getBccRecipients() = listOf("")
            override fun getSubject() = "Subject to configure"
            override fun getMailBody() = "Mail body to configure"
            override fun overrideMailBodyContent() = true
        })
    }

    private fun configureTripAnalysisUI(context: Context) {
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

    private fun configureDriverAchievementUI() {
        DriverAchievementUI.initialize()
        DriverAchievementUI.configureRankingTypes(RankingType.values().toList())
        val rankingPeriods = listOf(RankingPeriod.WEEKLY, RankingPeriod.MONTHLY, RankingPeriod.ALL_TIME)
        DriverAchievementUI.configureRankingSelector(RankingSelectorType.PERIOD(rankingPeriods))
        DriverAchievementUI.configureBadgeCategories(BadgeCategory.values().toMutableList())
        DriverAchievementUI.configureRankingDepth(rankingDepth = 5)
    }

    private fun configurePermissionsUtilsUI() {
        PermissionsUtilsUI.initialize()
        PermissionsUtilsUI.configureBluetooth(isBluetoothNeeded())
        PermissionsUtilsUI.configureContactType(ContactType.EMAIL(object : ContentMail {
            override fun getBccRecipients(): List<String> = listOf("")
            override fun getMailBody() = "Mail body to configure"
            override fun getRecipients(): List<String> = listOf("recipient_to_configure@mail.com")
            override fun getSubject() = "Subject to configure"
            override fun overrideMailBodyContent(): Boolean = false
        }))
    }

    private fun configureChallengeUI() {
        ChallengeUI.initialize()
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

    private fun isBluetoothNeeded(): Boolean {
        DriveKitVehicle.vehiclesQuery().noFilter().query().execute().forEach {
            if (it.detectionMode == DetectionMode.BEACON || it.detectionMode == DetectionMode.BLUETOOTH) {
                return true
            }
        }
        return false
    }

    fun reset(context: Context) {
        // RESET DRIVEKIT
        DriveKit.reset()
        DriveKitTripAnalysis.reset()
        DriveKitDriverData.reset()
        DriveKitVehicle.reset()
        DriveKitDriverAchievement.reset()
        DriveKitChallenge.reset()

        // DELETE NOTIFICATION CHANNELS
        DKNotificationManager.deleteChannels(context)

        // CLEAR SHARED PREFERENCES OF THE DEMO APP
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().clear().apply()
    }
}