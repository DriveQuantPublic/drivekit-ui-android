package com.drivequant.drivekit.vehicle.ui.findmyvehicle

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

internal class FindMyVehicleViewModel : ViewModel() {
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

}
