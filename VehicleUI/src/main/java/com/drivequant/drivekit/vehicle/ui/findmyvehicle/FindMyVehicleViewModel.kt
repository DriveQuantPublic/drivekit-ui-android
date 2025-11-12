package com.drivequant.drivekit.vehicle.ui.findmyvehicle

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.common.model.DKCoordinateAccuracy
import com.drivequant.drivekit.core.geocoder.DKAddress
import com.drivequant.drivekit.core.geocoder.DKLocation
import com.drivequant.drivekit.core.geocoder.DKReverseGeocoderListener
import com.drivequant.drivekit.core.geocoder.ReverseGeocoder
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Date

internal class FindMyVehicleViewModel : ViewModel() {

    private var cancellationTokenSource = CancellationTokenSource()
    private val lastTripProvider = lazy {
        DriveKitTripAnalysis.getLastTripLocation()
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

    // TODO: User Meter() class
    fun getDistanceToVehicleLastKnownLocationInMeters(
        userLocation: Location,
        callback: (Double?) -> Unit
    ) {
        val lastTrip = lastTripProvider.value
        if (lastTrip != null) {
            val distance = userLocation.distanceTo(Location("").apply {
                latitude = lastTrip.latitude
                longitude = lastTrip.longitude
            })
            callback(distance.toDouble())
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getUserCurrentLocation(
        locationClient: FusedLocationProviderClient, callback: (Location?) -> Unit
    ) {
        locationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            callback(location)
        }.addOnFailureListener { exception ->
            DriveKitLog.e(
                DriveKitVehicleUI.TAG, "Failed to get user current location : $exception"
            )
            callback(null)
        }
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
                    Bitmap.createScaledBitmap(bitmap, size, size, true)
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
}