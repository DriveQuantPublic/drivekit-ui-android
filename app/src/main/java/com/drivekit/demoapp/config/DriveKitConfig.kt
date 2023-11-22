package com.drivekit.demoapp.config

import android.app.Application
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.notification.controller.DKNotificationManager
import com.drivekit.demoapp.notification.enum.DKNotificationChannel
import com.drivekit.demoapp.receiver.TripReceiver
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
import com.drivequant.drivekit.core.DriveKitListenerManager
import com.drivequant.drivekit.core.deviceconfiguration.DKDeviceConfigurationEvent
import com.drivequant.drivekit.core.deviceconfiguration.DKDeviceConfigurationListener
import com.drivequant.drivekit.core.driver.UpdateUserIdStatus
import com.drivequant.drivekit.core.driver.deletion.DeleteAccountStatus
import com.drivequant.drivekit.core.networking.DriveKitListener
import com.drivequant.drivekit.core.networking.RequestError
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.*
import com.drivequant.drivekit.driverachievement.DriveKitDriverAchievement
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel.RankingSelectorType
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.timeline.ui.DriveKitDriverDataTimelineUI
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
import com.drivequant.drivekit.tripanalysis.service.recorder.CancelTrip
import com.drivequant.drivekit.tripanalysis.service.recorder.StartMode
import com.drivequant.drivekit.tripanalysis.service.recorder.State
import com.drivequant.drivekit.tripanalysis.triprecordingwidget.recordingbutton.DKTripRecordingUserMode
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
    private const val apiKey = ""

    private const val TRIP_ANALYSIS_AUTO_START_PREF_KEY = "tripAnalysisAutoStartPrefKey"

    private const val enableTripAnalysisCrashDetection: Boolean = true

    private val tripData: TripData = TripData.SAFETY
    private const val enableAlternativeTrips: Boolean = true
    private val tripRecordingUserMode: DKTripRecordingUserMode = DKTripRecordingUserMode.START_STOP

    private val vehicleTypes: List<VehicleType> = VehicleType.values().toList()
    private val vehicleBrands: List<VehicleBrand> = VehicleBrand.values().toList()
    private const val enableVehicleOdometer: Boolean = true

    fun initialize(application: Application) {
        // DriveKit modules initialization:
        initializeModules(application)

        // DriveKit modules configuration:
        configureModules(application)

        // Manage notifications for DriveKit SDK events, for example when a trip is finished, cancelled or saved for repost
        DKNotificationManager.configure()
    }

    private fun initializeModules(application: Application) {
        // DriveKit Core Initialization:
        DriveKit.initialize(application)
        DriveKitListenerManager.addListener(object : DriveKitListener {
            override fun onAccountDeleted(status: DeleteAccountStatus) {}

            override fun onAuthenticationError(errorType: RequestError) {}

            override fun onConnected() {}

            override fun onDisconnected() {
                // Data needs to be cleaned
                logout(application)
            }

            override fun userIdUpdateStatus(status: UpdateUserIdStatus, userId: String?) {}
        })
        DriveKit.addDeviceConfigurationListener(object : DKDeviceConfigurationListener {
            override fun onDeviceConfigurationChanged(event: DKDeviceConfigurationEvent) {
                DKNotificationManager.onDeviceConfigurationChanged(application, event)
            }
        })

        // TripAnalysis initialization:
        DriveKitTripAnalysis.initialize(createForegroundNotification(application))
        DriveKitTripAnalysis.addTripListener(object : TripListener {
            override fun beaconDetected() { }

            override fun crashDetected(crashInfo: DKCrashInfo) {}

            override fun crashFeedbackSent(
                crashInfo: DKCrashInfo,
                feedbackType: CrashFeedbackType,
                severity: CrashFeedbackSeverity
            ) {}

            override fun potentialTripStart(startMode: StartMode) {}

            override fun sdkStateChanged(state: State) {}

            override fun tripCancelled(cancelTrip: CancelTrip) {
                TripReceiver.tripCancelled(application, cancelTrip)
            }

            override fun tripFinished(post: PostGeneric, response: PostGenericResponse) {
                TripReceiver.tripFinished(application, response)
            }

            override fun tripPoint(tripPoint: TripPoint) { }

            override fun tripSavedForRepost() { }

            override fun tripStarted(startMode: StartMode) { }
        })

        // Initialize DriverData:
        DriveKitDriverData.initialize()
    }

    fun configureModules(context: Context) {
        // Internal modules configuration:
        configureCore(context)
        configureTripAnalysis(context)

        // UI modules configuration:
        configureCommonUI()
        configureDriverDataUI()
        configureDriverDataTimelineUI()
        configureVehicleUI()
        configureTripAnalysisUI(context)
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

    private fun configureCore(context: Context) {
        if (apiKey.isNotBlank()) {
            if (DriveKit.config.apiKey != null && apiKey != DriveKit.config.apiKey) {
                reset(context)
            }
            DriveKit.setApiKey(apiKey)
        }
    }

    private fun configureTripAnalysis(context: Context) {
        DriveKitTripAnalysis.activateAutoStart(isTripAnalysisAutoStartedEnabled(context))
        DriveKitTripAnalysis.activateCrashDetection(enableTripAnalysisCrashDetection)

        // You must call this method if you use DriveKit Vehicle component:
        DriveKitTripAnalysis.setVehiclesConfigTakeover(true)
    }

    private fun configureCommonUI() {
        DriveKitUI.initialize()
        DriveKitUI.scores = listOf(
            DKScoreType.SAFETY,
            DKScoreType.ECO_DRIVING,
            DKScoreType.DISTRACTION,
            DKScoreType.SPEEDING
        )
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

    private fun configureDriverDataTimelineUI() {
        DriveKitDriverDataTimelineUI.initialize()
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
                    icon = R.drawable.ic_notification,
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
        DriveKitTripAnalysisUI.tripRecordingUserMode = this.tripRecordingUserMode
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
            R.drawable.ic_notification
        )
        if (DriveKitTripAnalysisUI.isUserAllowedToCancelTrip(this.tripRecordingUserMode)) {
            notification.enableCancel = true
            notification.cancel = context.getString(R.string.cancel_trip)
            notification.cancelIconId = R.drawable.ic_notification
        }
        notification.channelId = DKNotificationChannel.TRIP_STARTED.getChannelId()

        val intent = Intent(context, DashboardActivity::class.java)
        DKNotificationManager.configureTripAnalysisNotificationIntent(intent)
        val contentIntent = TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(intent)
            .getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        notification.contentIntent = contentIntent

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

    fun logout(context: Context) {
        // Reset DriveKit modules:
        reset(context)

        // Reconfigure modules:
        configureModules(context)
    }

    private fun reset(context: Context) {
        // Reset DriveKit
        DriveKit.reset()
        DriveKitTripAnalysis.reset()
        DriveKitDriverData.reset()
        DriveKitVehicle.reset()
        DriveKitDriverAchievement.reset()
        DriveKitChallenge.reset()

        // Clear Shared Preferences of the Demo App
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().clear().apply()
    }
}
