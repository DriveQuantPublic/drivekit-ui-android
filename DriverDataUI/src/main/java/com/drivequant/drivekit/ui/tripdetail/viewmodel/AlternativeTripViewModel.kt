package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.KilometerPerHour
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.image
import com.drivequant.drivekit.ui.extension.text

internal class AlternativeTripViewModel(private var trip: Trip) : ViewModel() {

    fun updateTrip() {
        DbTripAccess.findTrip(trip.itinId).executeOneTrip()?.toTrip()?.let {
            trip = it
        }
    }

    fun getItinId() = trip.itinId

    fun getAnalyzedTransportationModeTitle(context: Context): Spannable {
        return if (trip.declaredTransportationMode?.transportationMode == null) {
            DKResource.buildString(
                context,
                DKColors.mainFontColor,
                DKColors.primaryColor,
                R.string.dk_driverdata_detected_transportation_mode,
                " ${trip.transportationMode.text(context)}"
            )
        } else {
            SpannableString(
                context.getString(R.string.dk_driverdata_detected_transportation_mode) + " " + HtmlCompat.fromHtml(
                    trip.transportationMode.text(context),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
        }
    }

    fun getDeclaredTransportationModeTitle(context: Context): Spannable? {
        return if (trip.declaredTransportationMode?.transportationMode != null) {
            DKResource.buildString(
                context,
                DKColors.mainFontColor,
                DKColors.primaryColor,
                R.string.dk_driverdata_declared_transportation_mode,
                " ${trip.declaredTransportationMode?.transportationMode.text(context)}"
            )
        } else {
            null
        }
    }

    fun getDescription(context: Context): String {
        val identifier = trip.declaredTransportationMode?.transportationMode?.let {
            R.string.dk_driverdata_alternative_transportation_thanks
        } ?: run {
            R.string.dk_driverdata_alternative_transportation_remark
        }
        return context.getString(identifier)
    }

    fun getConditionValue(context: Context): String {
        val identifier = trip.tripStatistics?.let {
            if (it.day) {
                R.string.dk_driverdata_day
            } else {
                R.string.dk_driverdata_night
            }
        } ?: run {
            R.string.dk_driverdata_unknown
        }
        return context.getString(identifier)
    }

    fun getWeatherValue(context: Context): String {
        trip.tripStatistics?.let {
            val identifier = when (it.meteo) {
                1 -> R.string.dk_driverdata_weather_sun
                2 -> R.string.dk_driverdata_weather_cloud
                3 -> R.string.dk_driverdata_weather_fog
                4 -> R.string.dk_driverdata_weather_rain
                5 -> R.string.dk_driverdata_weather_snow
                6 -> R.string.dk_driverdata_weather_hail
                else -> R.string.dk_driverdata_unknown
            }
            return context.getString(identifier)
        } ?: run {
            return context.getString(R.string.dk_driverdata_unknown)
        }
    }

    fun getMeanSpeed(context: Context): String {
        return if (isIdleTrip()) {
            context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_no_value)
        } else {
            trip.tripStatistics?.let {
                DKDataFormatter.formatMeanSpeed(context, KilometerPerHour(it.speedMean))
            } ?: run {
                context.getString(R.string.dk_driverdata_unknown)
            }
        }
    }

    fun getIcon(context: Context): Drawable? {
        var transportationMode = trip.transportationMode
        trip.declaredTransportationMode?.transportationMode?.let {
            transportationMode = it
        }
        return transportationMode.image(context)
    }

    private fun isIdleTrip() = trip.transportationMode == TransportationMode.IDLE
        || trip.declaredTransportationMode?.transportationMode == TransportationMode.IDLE

    @Suppress("UNCHECKED_CAST")
    class AlternativeTripViewModelFactory(private val trip: Trip) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlternativeTripViewModel(trip) as T
        }
    }
}
