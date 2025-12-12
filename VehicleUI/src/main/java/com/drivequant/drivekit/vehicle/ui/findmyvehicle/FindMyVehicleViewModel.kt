package com.drivequant.drivekit.vehicle.ui.findmyvehicle

import android.Manifest
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.utils.Meter
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.common.model.DKCoordinateAccuracy
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.diffWith
import com.drivequant.drivekit.core.geocoder.DKAddress
import com.drivequant.drivekit.core.geocoder.DKLocation
import com.drivequant.drivekit.core.geocoder.DKReverseGeocoderListener
import com.drivequant.drivekit.core.geocoder.ReverseGeocoder
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Date

internal class FindMyVehicleViewModel(
    private val vehicleId: String?
) : ViewModel() {

    private var cancellationTokenSource = CancellationTokenSource()
    private val lastTripProvider = lazy {
        if (vehicleId != null) {
            DriveKitVehicle.getVehicleLocation(vehicleId)
        } else {
            DriveKitTripAnalysis.getLastTripLocation()
        }
    }

    fun getVehicleLastKnownCoordinates(): LatLng? {
        val lastTrip = lastTripProvider.value
        if (lastTrip != null) {
            return LatLng(lastTrip.latitude, lastTrip.longitude)
        }
        return null
    }

    fun getVehicleLastKnownLocationAccuracyLevel(): DKCoordinateAccuracy? {
        val lastTrip = lastTripProvider.value
        if (lastTrip != null) {
            return lastTrip.getAccuracyLevel()
        }
        return null
    }

    fun getVehicleLastKnownLocationAccuracyMeters(): Double? {
        val lastTrip = lastTripProvider.value
        if (lastTrip != null) {
            return lastTrip.accuracyMeter
        }
        return null
    }

    fun getVehicleLastKnownLocationDate(): Date? {
        val lastTrip = lastTripProvider.value
        if (lastTrip != null) {
            return lastTrip.date
        }
        return null
    }

    fun getDistanceToVehicleLastKnownLocation(
        userLocation: Location,
        callback: (Meter?) -> Unit
    ) {
        val lastTrip = lastTripProvider.value
        if (lastTrip != null) {
            val distance = userLocation.distanceTo(Location("").apply {
                latitude = lastTrip.latitude
                longitude = lastTrip.longitude
            })
            callback(Meter(distance.toDouble()))
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun getUserCurrentLocation(
        locationClient: FusedLocationProviderClient, callback: (Location?) -> Unit
    ) {
        locationClient.lastLocation.addOnSuccessListener { location ->
            if (isLocationAcceptable(location)) {
                DriveKitLog.i(
                    DriveKitVehicleUI.TAG,
                    "Using cached location"
                )
                callback(location)
            } else {
                DriveKitLog.i(
                    DriveKitVehicleUI.TAG,
                    "Fetching current location"
                )
                locationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    callback(location)
                }
            }
        }.addOnFailureListener { exception ->
            DriveKitLog.e(
                DriveKitVehicleUI.TAG, "Failed to get user current location: $exception"
            )
            callback(null)
        }
    }

    private fun isLocationAcceptable(location: Location): Boolean {

        val gpsValidityDurationInSeconds = 600
        val elapsedTimeSinceLocation = Date().diffWith(
            Date(location.time),
            calendarField = CalendarField.SECOND
        )

        DriveKitLog.i(
            DriveKitVehicleUI.TAG,
        "FusedLocationProviderClient.lastLocation returned with $elapsedTimeSinceLocation seconds old location, accuracy = ${location.accuracy}"
        )

        val horizontalAccuracyThreshold = Meter(100.0);
        if (location.accuracy >= horizontalAccuracyThreshold.value) {
            return false
        }


        return elapsedTimeSinceLocation <= gpsValidityDurationInSeconds
    }

    fun getAddress(context: Context, latLng: LatLng, callback: (DKAddress?) -> Unit) {
        ReverseGeocoder.getAddresses(
            context,
            listOf(DKLocation(latitude = latLng.latitude, longitude = latLng.longitude)),
            object : DKReverseGeocoderListener {
                override fun onResponse(addresses: List<DKAddress?>) {
                    callback(addresses.firstOrNull())
                }
            })
    }

    private fun getIcon(context: Context, @DrawableRes drawableId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, drawableId)
            ?.let { targetLocationDrawable ->
                val bitmap = (targetLocationDrawable as BitmapDrawable).bitmap
                val size =
                    context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_ic_big)
                        .toInt()
                BitmapDescriptorFactory.fromBitmap(
                    bitmap.scale(size, size)
                )
            }
    }

    fun getTargetLocationIcon(context: Context): BitmapDescriptor? {
        return getIcon(context, R.drawable.dk_vehicle_target_location)
    }

    fun getCurrentLocationIcon(context: Context): BitmapDescriptor? {
        return getIcon(context, R.drawable.dk_vehicle_current_location)
    }

    override fun onCleared() {
        super.onCleared()
        cancellationTokenSource.cancel()
    }

    class FindMyVehicleViewModelFactory(
        private val vehicleId: String?
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FindMyVehicleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FindMyVehicleViewModel(vehicleId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}