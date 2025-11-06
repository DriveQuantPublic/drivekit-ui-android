package com.drivequant.drivekit.vehicle.ui.findmyvehicle

import android.location.Location
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.DriveKitLog
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource

internal class FindMyVehicleViewModel : ViewModel() {

    private var cancellationTokenSource = CancellationTokenSource()

    fun getInitialCameraPosition(): CameraPosition? {
        val initialCoordinates = getVehicleLastKnownLocation()
        if (initialCoordinates == null) {
            return null
        }

        val initialZoomLevel = 10f
        return CameraPosition.fromLatLngZoom(
            initialCoordinates,
            initialZoomLevel
        )
    }

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

    override fun onCleared() {
        super.onCleared()
        cancellationTokenSource.cancel()
    }
}