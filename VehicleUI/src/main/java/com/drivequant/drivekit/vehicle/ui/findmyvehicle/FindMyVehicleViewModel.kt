package com.drivequant.drivekit.vehicle.ui.findmyvehicle

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.core.geocoder.DKAddress
import com.drivequant.drivekit.core.geocoder.DKLocation
import com.drivequant.drivekit.core.geocoder.DKReverseGeocoderListener
import com.drivequant.drivekit.core.geocoder.ReverseGeocoder
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource

internal class FindMyVehicleViewModel : ViewModel() {

    private var cancellationTokenSource = CancellationTokenSource()

    fun getVehicleLastKnownLocation(): LatLng? {
        val lastTrip = DriveKitTripAnalysis.getLastTripLocation()
        if (lastTrip != null) {
            return LatLng(lastTrip.latitude, lastTrip.longitude)
        }
        return null
    }

    fun getUserCurrentLocation(
        locationClient: FusedLocationProviderClient,
        callback: (Location?) -> Unit
    ) {
        // TODO Handle permissions
        locationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )
            .addOnSuccessListener { location ->
                callback(location)
            }
            .addOnFailureListener { exception ->
                DriveKitLog.e(DriveKitVehicleUI.TAG, "Failed to get user current location : $exception")
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

    override fun onCleared() {
        super.onCleared()
        cancellationTokenSource.cancel()
    }
}