package com.drivequant.drivekit.vehicle.ui.findmyvehicle

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drivequant.drivekit.common.ui.component.DKText
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.graphical.DKStyle
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.core.geocoder.DKAddress
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import java.util.Date

private const val INITIAL_ZOOM_LEVEL = 20f
private const val ITINERARY_LINE_WIDTH = 3f

internal open class FindMyVehicleActivity : ComponentActivity() {

    lateinit var viewModel: FindMyVehicleViewModel
    val fusedLocationClient = lazy { LocationServices.getFusedLocationProviderClient(this) }

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
            FindMyVehicleContent()
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


        LaunchedEffect(Unit) {
            viewModel.getUserCurrentLocation(fusedLocationClient.value) { location ->
                location?.let {
                    userLocation = it

                    DriveKitVehicleUI.coroutineScope.launch {
                        initialCameraPositionState.animate(
                            CameraUpdateFactory.newLatLngBounds(
                                LatLngBounds.Builder().include(LatLng(it.latitude, it.longitude))
                                    .include(vehicleLastKnownCoordinates).build(), 100
                            ), 500
                        )
                    }
                }
            }
        }

        GoogleMap(
            modifier = Modifier.fillMaxWidth(),
            cameraPositionState = initialCameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false
            )
        ) {
            VehicleMapMarker(vehicleLastKnownCoordinates)

            userLocation?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)

                UserMapMarker(userLocation = it)
                Polyline(
                    points = listOf(vehicleLastKnownCoordinates, userLatLng),
                    geodesic = true,
                    color = Color(DKColors.mapTraceColor),
                    width = ITINERARY_LINE_WIDTH.convertDpToPx().toFloat(),
                    pattern = listOf(Dash(30f), Gap(20f))
                )
            }
        }
    }

    @Composable
    fun UserMapMarker(userLocation: Location) {
        val userLatLng = LatLng(userLocation.latitude, userLocation.longitude)
        Circle(
            userLatLng,
            radius = userLocation.accuracy.toDouble(),
            fillColor = Color(DKColors.primaryColor).copy(alpha = 0.3f),
            strokeWidth = 0f
        )

        viewModel.getCurrentLocationIcon(this@FindMyVehicleActivity)?.let { icon ->
            Marker(
                state = MarkerState(
                    position = userLatLng
                ),
                icon = icon,
                anchor = Offset(0.5f, 0.5f),
            )
        }
    }

    @Composable
    fun VehicleMapMarker(vehicleLastKnownCoordinates: LatLng) {
        val vehicleMarkerState = rememberMarkerState(null, position = vehicleLastKnownCoordinates)
        var addressState by remember { mutableStateOf<DKAddress?>(null) }
        LaunchedEffect(Unit) {
            viewModel.getAddress(
                this@FindMyVehicleActivity, vehicleLastKnownCoordinates
            ) { address ->
                address?.let {
                    addressState = it
                }
            }
        }

        LaunchedEffect(vehicleMarkerState) {
            vehicleMarkerState.showInfoWindow()
        }
        viewModel.getTargetLocationIcon(this@FindMyVehicleActivity)?.let { icon ->
            Marker(
                state = vehicleMarkerState,
                icon = icon,
                anchor = Offset(0.5f, 0.5f),
                title = addressState?.address
            )
        }
    }

    @Composable
    fun FindMyVehicleContent() {
        var userDistanceToVehicle by remember {
            mutableStateOf<Double?>(null)
        }
        var vehicleLastKnownLocationDate by remember {
            mutableStateOf<Date?>(null)
        }
        LaunchedEffect(Unit) {
            viewModel.getDistanceToVehicleLastKnownLocationInMeters(locationClient = fusedLocationClient.value) { maybeDistance ->
                maybeDistance?.let {
                    userDistanceToVehicle = it
                }
            }
            vehicleLastKnownLocationDate = viewModel.getVehicleLastKnownLocationDate()
        }
        vehicleLastKnownLocationDate?.let { lastTripDate ->
            val date = lastTripDate.formatDate(DKDatePattern.STANDARD_DATE)
            val time = lastTripDate.formatDate(DKDatePattern.HOUR_MINUTE)

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                DKText(
                    text = stringResource(R.string.dk_find_vehicle_date, date, time),
                    DKStyle.NORMAL_TEXT
                )
                // TODO : Handle imperial units
                userDistanceToVehicle?.let { distance ->
                    if (distance < 100) {
                        DKText(
                            text = stringResource(R.string.dk_find_vehicle_location_very_close),
                            DKStyle.NORMAL_TEXT
                        )
                    } else if (distance < 1000) {
                        val roundedDistanceTo100 = (distance / 100).toInt() * 100
                        DKText(
                            text = stringResource(
                                R.string.dk_find_vehicle_location_nearby,
                                roundedDistanceTo100
                            ),
                            DKStyle.NORMAL_TEXT
                        )
                    } else {
                        val roundedDistanceTo1000 = (distance / 1000).toInt()
                        DKText(
                            text = stringResource(
                                R.string.dk_find_vehicle_location_far,
                                roundedDistanceTo1000
                            ),
                            DKStyle.NORMAL_TEXT
                        )
                    }
                }
            }
        }
    }
}