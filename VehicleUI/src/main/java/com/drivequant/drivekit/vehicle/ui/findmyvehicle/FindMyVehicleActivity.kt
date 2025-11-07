package com.drivequant.drivekit.vehicle.ui.findmyvehicle

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drivequant.drivekit.common.ui.component.DKText
import com.drivequant.drivekit.common.ui.graphical.DKStyle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.core.geocoder.DKAddress
import com.drivequant.drivekit.vehicle.ui.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

private const val INITIAL_ZOOM_LEVEL = 20f

internal open class FindMyVehicleActivity : ComponentActivity() {

    lateinit var viewModel: FindMyVehicleViewModel

    companion object {
        fun launchActivity(context: Context) {
            val intent = Intent(context, FindMyVehicleActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DKEdgeToEdgeManager.setSystemStatusBarForegroundColor(window)
        setContent {
            viewModel = viewModel()
            FindMyVehicleScreen()
        }
    }

    @Composable
    fun FindMyVehicleScreen() {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                FindMyVehicleMap()
            }

            Box(
                Modifier.background(color = Color(red = 255, green = 255, blue = 0))
            ) {
                FindMyVehicleContent()
            }
        }
    }

    @Composable
    fun FindMyVehicleMap() {
        val vehicleLastKnownCoordinates = viewModel.getVehicleLastKnownLocation()

        if (vehicleLastKnownCoordinates == null) {
            return // TODO HANDLE NO LAST TRIP
        }

        val initialCameraPosition =
            CameraPosition.fromLatLngZoom(vehicleLastKnownCoordinates, INITIAL_ZOOM_LEVEL)

        val initialCameraPositionState = rememberCameraPositionState {
            position = initialCameraPosition
        }

        var userLocation by remember {
            mutableStateOf<Location?>(null)
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        LaunchedEffect(Unit) {
            viewModel.getUserCurrentLocation(fusedLocationClient) { location ->
                location?.let {
                    userLocation = it

                    initialCameraPositionState.move(
                        CameraUpdateFactory.newLatLngBounds(
                            LatLngBounds.Builder().include(LatLng(it.latitude, it.longitude))
                                .include(vehicleLastKnownCoordinates).build(), 100
                        )
                    )
                }
            }
        }

        GoogleMap(
            modifier = Modifier.fillMaxWidth(),
            cameraPositionState = initialCameraPositionState,
        ) {
            val vehicleMarkerState =
                rememberMarkerState(null, position = vehicleLastKnownCoordinates)

            LaunchedEffect(vehicleMarkerState) {
                vehicleMarkerState.showInfoWindow()
            }

            var addressState by remember { mutableStateOf<DKAddress?>(null) }

            LaunchedEffect(Unit) {
                viewModel.getAddress(this@FindMyVehicleActivity, vehicleLastKnownCoordinates) {address ->
                    address?.let {
                        addressState = it
                        vehicleMarkerState.showInfoWindow()
                    }
                }
            }

            Marker(
                state = vehicleMarkerState,
                icon = BitmapDescriptorFactory.fromResource(R.drawable.dk_vehicle_target_location),
                anchor = Offset(0.5f, 0.5f),
                title = addressState?.address
            )

            userLocation?.let {
                Marker(
                    state = MarkerState(
                        position = LatLng(it.latitude, it.longitude)
                    ),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.dk_vehicle_current_location),
                    anchor = Offset(0.5f, 0.5f),
                )
            }
        }
    }

    @Composable
    fun FindMyVehicleContent() {
        Box(
            modifier = Modifier.background(Color(0, 255, 0))
        ) {
            DKText(text = "Test", DKStyle.NORMAL_TEXT)
        }
    }
}