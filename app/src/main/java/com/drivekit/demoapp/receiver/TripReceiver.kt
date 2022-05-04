package com.drivekit.demoapp.receiver

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.drivekit.demoapp.DriveKitDemoApplication.Companion.showNotification
import com.drivekit.demoapp.dashboard.activity.DashboardActivity
import com.drivekit.demoapp.notification.controller.DKNotificationManager
import com.drivekit.demoapp.notification.enum.NotificationType
import com.drivekit.demoapp.notification.enum.TripAnalysisError
import com.drivekit.demoapp.notification.enum.TripCancellationReason
import com.drivekit.demoapp.utils.getImmutableFlag
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.component.triplist.TripData
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.databaseutils.entity.TransportationMode.Companion.getEnum
import com.drivequant.drivekit.databaseutils.entity.TripAdvice
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import com.drivequant.drivekit.tripanalysis.entity.PostGeneric
import com.drivequant.drivekit.tripanalysis.entity.PostGenericResponse
import com.drivequant.drivekit.tripanalysis.receiver.TripAnalysedReceiver
import com.drivequant.drivekit.tripanalysis.service.recorder.CancelTrip
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import com.google.android.gms.common.util.CollectionUtils
import java.util.*

internal class TripReceiver : TripAnalysedReceiver() {

    override fun onTripReceived(
        context: Context,
        post: PostGeneric,
        response: PostGenericResponse
    ) {
        if (response.isTripValid()) {
            var errorCode = 0
            if (response.safety != null && response.safety!!.safetyScore > 10
                || response.ecoDriving != null && response.ecoDriving!!.score > 10
                || response.driverDistraction != null && response.driverDistraction!!.score > 10
            ) {
                errorCode = 1
            }
            manageTrip(context, response, errorCode)
        } else {
            lateinit var notificationType: NotificationType
            for (comment in response.comments) {
                if (comment.errorCode == 21) {
                    notificationType =
                        NotificationType.TRIP_ANALYSIS_ERROR(TripAnalysisError.NO_API_KEY)
                    break
                }
                if (comment.errorCode == 29 || comment.errorCode == 30) {
                    notificationType =
                        NotificationType.TRIP_ANALYSIS_ERROR(TripAnalysisError.NO_BEACON)
                    break
                }
                if (comment.errorCode == 31) {
                    notificationType =
                        NotificationType.TRIP_ANALYSIS_ERROR(TripAnalysisError.DUPLICATE_TRIP)
                    break
                }
            }
            val contentIntent = buildContentIntent(
                context,
                getEnum(response.itineraryStatistics!!.transportationMode),
                null,
                response.itinId
            )
            DKNotificationManager.sendNotification(context, notificationType, contentIntent)
        }
    }

    override fun onTripCancelled(context: Context, status: CancelTrip) {
        when (status) {
            CancelTrip.NO_GPS_DATA -> R.string.notif_trip_cancelled_no_gps_data
            CancelTrip.NO_BEACON -> R.string.notif_trip_cancelled_no_beacon
            else -> R.string.trip_cancelled_reset
        }.let { messageResId ->
            showNotification(context, context.getString(messageResId))
        }
        when (status) {
            CancelTrip.HIGHSPEED -> TripCancellationReason.HIGH_SPEED
            CancelTrip.NO_BEACON -> TripCancellationReason.NO_BEACON
            CancelTrip.NO_GPS_DATA -> TripCancellationReason.NO_GPS_POINT
            CancelTrip.USER,
            CancelTrip.NO_SPEED,
            CancelTrip.MISSING_CONFIGURATION,
            CancelTrip.RESET,
            CancelTrip.BEACON_NO_SPEED -> null
        }?.let {
            DKNotificationManager.sendNotification(context, NotificationType.TRIP_CANCELLED(it))
        }
    }

    private fun manageTrip(context: Context, response: PostGenericResponse, errorCode: Int) {
        val dkTrip = DbTripAccess.findTrip(response.itinId).executeOneTrip()?.toTrip()
        dkTrip?.let {
            val contentIntent = buildContentIntent(context, dkTrip.transportationMode, dkTrip.tripAdvices, dkTrip.itinId)
            if (errorCode == 0) {
                if (dkTrip.transportationMode.isAlternative() && dkTrip.transportationMode.isManaged()) {
                    DKNotificationManager.sendNotification(context, NotificationType.TRIP_ENDED(dkTrip.transportationMode, dkTrip.tripAdvices.size), contentIntent)
                } else {
                    val additionalBody = when (DriverDataUI.tripData) {
                        TripData.SAFETY -> "${context.getString(R.string.notif_trip_finished_safety)} : ${dkTrip.safety!!.safetyScore.removeZeroDecimal()}/10"
                        TripData.ECO_DRIVING -> "${context.getString(R.string.notif_trip_finished_efficiency)} : ${dkTrip.ecoDriving!!.score.removeZeroDecimal()}/10"
                        TripData.DISTRACTION -> "${context.getString(R.string.notif_trip_finished_distraction)} : ${dkTrip.driverDistraction!!.score.removeZeroDecimal()}/10"
                        TripData.SPEEDING -> null
                        TripData.DURATION -> null
                        TripData.DISTANCE -> null
                    }
                    DKNotificationManager.sendNotification(context, NotificationType.TRIP_ENDED(dkTrip.transportationMode, dkTrip.tripAdvices.size), contentIntent, additionalBody)
                }
            } else {
                DKNotificationManager.sendNotification(context, NotificationType.TRIP_TOO_SHORT, contentIntent)
            }
        }
    }

    private fun buildContentIntent(
        context: Context,
        transportationMode: TransportationMode,
        tripAdvices: List<TripAdvice>?,
        itinId: String
    ): PendingIntent? {
        val intent = Intent(context, DashboardActivity::class.java)
        if (!TextUtils.isEmpty(itinId)) {
            val hasTripAdvices = !CollectionUtils.isEmpty(tripAdvices)
            intent.putExtra(TripDetailActivity.ITINID_EXTRA, itinId)
            intent.putExtra(TripDetailActivity.OPEN_ADVICE_EXTRA, hasTripAdvices)
            if (transportationMode.isAlternative()) {
                if (DriverDataUI.enableAlternativeTrips) {
                    intent.putExtra(
                        TripDetailActivity.TRIP_LIST_CONFIGURATION_TYPE_EXTRA,
                        TripListConfigurationType.ALTERNATIVE
                    )
                }
            } else {
                intent.putExtra(
                    TripDetailActivity.TRIP_LIST_CONFIGURATION_TYPE_EXTRA,
                    TripListConfigurationType.MOTORIZED
                )
            }
        }
        return TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(intent)
            .getPendingIntent(Random().nextInt(Int.MAX_VALUE), getImmutableFlag())
    }
}

fun TransportationMode.isManaged() = when (this) {
    TransportationMode.TRAIN, TransportationMode.BOAT, TransportationMode.BIKE, TransportationMode.SKIING, TransportationMode.IDLE -> true
    TransportationMode.UNKNOWN,
    TransportationMode.CAR,
    TransportationMode.MOTO,
    TransportationMode.TRUCK,
    TransportationMode.BUS,
    TransportationMode.FLIGHT,
    TransportationMode.ON_FOOT,
    TransportationMode.OTHER -> false
}

fun PostGenericResponse.isTripValid() =
    this.comments.map { it.errorCode == 0 }.isNotEmpty()
            && this.itineraryStatistics != null
            && this.itineraryStatistics!!.distance > 0