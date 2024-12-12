package com.drivekit.demoapp.notification.enum

import androidx.annotation.StringRes
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.tripanalysis.entity.TripResponseError

internal enum class TripResponseErrorNotification(@StringRes val descriptionKey: Int) {
    INVALID_ROUTE_DEFINITION(R.string.dk_trip_service_insufficient_gps_data),
    INVALID_SAMPLING_PERIOD(R.string.dk_trip_service_invalid_sample_period),
    INVALID_CUSTOMER_ID(R.string.dk_trip_service_error_invalid_customer_id),
    MAX_DAILY_REQUEST_NUMBER_REACHED(R.string.dk_trip_service_request_limit_reached),
    DATA_ERROR(R.string.dk_trip_service_analysis_failed),
    INVALID_ROUTE_VECTORS(R.string.dk_trip_service_analysis_failed),
    DUPLICATE_TRIP(R.string.dk_trip_service_error_trip_duplicate),
    INSUFFICIENT_GPS_DATA(R.string.dk_trip_service_insufficient_gps_data),
    USER_DISABLED(R.string.dk_trip_service_user_disabled),
    INVALID_USER(R.string.dk_trip_service_user_invalid),
    INVALID_GPS_DATA(R.string.dk_trip_service_invalid_gps_data),
    INVALID_TRIP(R.string.dk_trip_service_invalid_gps_data),
    ACCOUNT_LIMIT_REACHED(R.string.dk_trip_service_account_limit_reached),
    MISSING_BEACON(R.string.dk_trip_service_error_beacon_missing),
    INVALID_BEACON(R.string.dk_trip_service_error_beacon_invalid);

    companion object {
        fun fromTripResponseError(tripResponseError: TripResponseError): TripResponseErrorNotification? =
            when (tripResponseError) {
                TripResponseError.INVALID_ROUTE_DEFINITION -> INVALID_ROUTE_DEFINITION
                TripResponseError.INVALID_SAMPLING_PERIOD -> INVALID_SAMPLING_PERIOD
                TripResponseError.INVALID_CUSTOMER_ID -> INVALID_CUSTOMER_ID
                TripResponseError.MAX_DAILY_REQUEST_NUMBER_REACHED -> MAX_DAILY_REQUEST_NUMBER_REACHED
                TripResponseError.DATA_ERROR -> DATA_ERROR
                TripResponseError.INVALID_ROUTE_VECTORS -> INVALID_ROUTE_VECTORS
                TripResponseError.DUPLICATE_TRIP -> DUPLICATE_TRIP
                TripResponseError.INSUFFICIENT_GPS_DATA -> INSUFFICIENT_GPS_DATA
                TripResponseError.USER_DISABLED -> USER_DISABLED
                TripResponseError.INVALID_USER -> INVALID_USER
                TripResponseError.INVALID_GPS_DATA -> INVALID_GPS_DATA
                TripResponseError.INVALID_TRIP -> INVALID_TRIP
                TripResponseError.ACCOUNT_LIMIT_REACHED -> ACCOUNT_LIMIT_REACHED
                TripResponseError.MISSING_BEACON -> MISSING_BEACON
                TripResponseError.INVALID_BEACON -> INVALID_BEACON

                TripResponseError.NO_ACCOUNT_SET,
                TripResponseError.NO_ROUTE_OBJECT_FOUND,
                TripResponseError.NO_VELOCITY_DATA,
                TripResponseError.NO_DATE_FOUND,
                TripResponseError.UNKNOWN_ERROR -> null
            }
    }

    fun getNotificationChannel(): DKNotificationChannel =
        when (this) {
            INVALID_ROUTE_DEFINITION,
            INVALID_SAMPLING_PERIOD,
            INVALID_CUSTOMER_ID,
            MAX_DAILY_REQUEST_NUMBER_REACHED,
            DATA_ERROR,
            INVALID_ROUTE_VECTORS,
            DUPLICATE_TRIP,
            INSUFFICIENT_GPS_DATA,
            USER_DISABLED,
            INVALID_USER,
            INVALID_GPS_DATA,
            INVALID_TRIP,
            ACCOUNT_LIMIT_REACHED -> DKNotificationChannel.TRIP_ENDED

            MISSING_BEACON,
            INVALID_BEACON -> DKNotificationChannel.TRIP_CANCELED
        }

    fun getNotificationId() =
        when (this) {
            INVALID_ROUTE_DEFINITION -> 208
            INVALID_SAMPLING_PERIOD -> 209
            INVALID_CUSTOMER_ID -> 204
            MAX_DAILY_REQUEST_NUMBER_REACHED -> 210

            DUPLICATE_TRIP -> 203
            DATA_ERROR -> 211
            INVALID_ROUTE_VECTORS -> 212
            INSUFFICIENT_GPS_DATA -> 213
            USER_DISABLED -> 214
            INVALID_USER -> 215
            INVALID_GPS_DATA -> 216
            INVALID_TRIP -> 217
            ACCOUNT_LIMIT_REACHED -> 218

            MISSING_BEACON,
            INVALID_BEACON -> 252
        }
}
