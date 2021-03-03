package com.drivequant.drivekit.ui.tripdetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.text.HtmlCompat
import android.text.Spannable
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import com.drivequant.drivekit.ui.extension.image
import com.drivequant.drivekit.ui.extension.text

internal class AlternativeTripViewModel(private var trip: Trip) : ViewModel() {

    fun updateTrip(){
        DbTripAccess.findTrip(trip.itinId).executeOneTrip()?.toTrip()?.let {
            trip = it
        }
    }

    fun getAnalyzedTransportationModeTitle(context: Context): Spannable? {
        return if (trip.declaredTransportationMode?.transportationMode == null) {
            DKResource.buildString(
                context,
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.primaryColor(),
                DKResource.convertToString(context, "dk_driverdata_detected_transportation_mode"),
                " ${trip.transportationMode.text(context)}"
            )
        } else {
            SpannableString(
                DKResource.convertToString(
                    context,
                    "dk_driverdata_detected_transportation_mode"
                ) + " " + HtmlCompat.fromHtml(trip.transportationMode.text(context), HtmlCompat.FROM_HTML_MODE_LEGACY)
            )
        }
    }

    fun getDeclaredTransportationModeTitle(context: Context): Spannable? {
        return if (trip.declaredTransportationMode?.transportationMode != null) {
            DKResource.buildString(
                context,
                DriveKitUI.colors.mainFontColor(),
                DriveKitUI.colors.primaryColor(),
                DKResource.convertToString(context, "dk_driverdata_declared_transportation_mode"),
                " ${trip.declaredTransportationMode?.transportationMode.text(context)}"
            )
        } else {
            null
        }
    }

    fun getDescription(context: Context): String {
        val identifier = trip.declaredTransportationMode?.transportationMode?.let {
            "dk_driverdata_alternative_transportation_thanks"
        } ?: run {
            "dk_driverdata_alternative_transportation_remark"
        }
        return DKResource.convertToString(context, identifier)
    }

    fun getConditionValue(context: Context): String {
        val identifier = trip.tripStatistics?.let {
            if (it.day) {
                "dk_driverdata_day"
            } else {
                "dk_driverdata_night"
            }
        } ?: run {
            "dk_driverdata_unknown"
        }
        return DKResource.convertToString(context, identifier)
    }

    fun getWeatherValue(context: Context): String {
        trip.tripStatistics?.let {
            val identifier = when (it.meteo) {
                1 -> "dk_driverdata_weather_sun"
                2 -> "dk_driverdata_weather_cloud"
                3 -> "dk_driverdata_weather_rain"
                4 -> "dk_driverdata_weather_fog"
                5 -> "dk_driverdata_weather_snow"
                6 -> "dk_driverdata_weather_hail"
                else -> "dk_driverdata_unknown"
            }
            return DKResource.convertToString(context, identifier)
        } ?: run {
            return DKResource.convertToString(context, "dk_driverdata_unknown")
        }
    }

    fun getMeanSpeed(context: Context): String {
        trip.tripStatistics?.let {
            return DKDataFormatter.formatSpeedMean(context, it.speedMean)
        } ?: run {
            return DKResource.convertToString(context, "dk_driverdata_unknown")
        }
    }

    fun getIcon(context: Context): Drawable? {
        var transportationMode = trip.transportationMode
        trip.declaredTransportationMode?.transportationMode?.let {
            transportationMode = it
        }
        return transportationMode.image(context)
    }

    @Suppress("UNCHECKED_CAST")
    class AlternativeTripViewModelFactory(private val trip: Trip) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AlternativeTripViewModel(trip) as T
        }
    }
}