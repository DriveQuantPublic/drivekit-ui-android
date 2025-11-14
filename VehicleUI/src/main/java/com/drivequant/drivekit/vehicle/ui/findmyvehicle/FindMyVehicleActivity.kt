package com.drivequant.drivekit.vehicle.ui.findmyvehicle

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.DKPrimaryButton
import com.drivequant.drivekit.common.ui.component.DKText
import com.drivequant.drivekit.common.ui.extension.formatDate
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.graphical.DKStyle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.common.ui.utils.DKUnitSystem
import com.drivequant.drivekit.common.ui.utils.Meter
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.core.common.model.DKCoordinateAccuracy
import com.drivequant.drivekit.core.geocoder.DKAddress
import com.drivequant.drivekit.core.utils.ConnectivityType
import com.drivequant.drivekit.core.utils.DiagnosisHelper
import com.drivequant.drivekit.permissionsutils.diagnosis.listener.OnPermissionCallback
import com.drivequant.drivekit.permissionsutils.permissions.activity.RequestPermissionActivity
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
import kotlin.math.roundToInt

private const val INITIAL_ZOOM_LEVEL = 20f
private const val ITINERARY_LINE_WIDTH = 3f
private const val MAP_REGION_PADDING = 100
private val VEHICLE_NEARBY_THRESHOLD = Meter(100.0)
private val VEHICLE_FAR_THRESHOLD = Meter(1000.0)

internal open class FindMyVehicleActivity : RequestPermissionActivity() {

    lateinit var viewModel: FindMyVehicleViewModel
    private val fusedLocationClient = lazy { LocationServices.getFusedLocationProviderClient(this) }

    private val textColorResId = com.drivequant.drivekit.common.ui.R.color.primaryColor

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
        setContentView(R.layout.activity_find_my_vehicle)
        setupToolbar()
        setupEdgeToEdge()

        findViewById<ComposeView>(R.id.compose_view).setContent {
            viewModel = viewModel()
            FindMyVehicleScreen()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.dk_find_vehicle_title)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupEdgeToEdge() {
        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(findViewById(R.id.root)) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
        }
    }

    @Composable
    fun FindMyVehicleScreen() {
        val vehicleLastKnownCoordinates = viewModel.getVehicleLastKnownCoordinates()

        var userLocation by remember {
            mutableStateOf<Location?>(null)
        }

        val lifecycleOwner = LocalLifecycleOwner.current

        val gpsSensorNeededMessage =
            stringResource(com.drivequant.drivekit.permissionsutils.R.string.dk_perm_utils_app_diag_loc_sensor_ko)
        val setupUserLocation: () -> Unit = {
            if (!DiagnosisHelper.isActivated(
                    this@FindMyVehicleActivity,
                    ConnectivityType.GPS
                )
            ) {
                DKAlertDialog.LayoutBuilder()
                    .init(this)
                    .message(gpsSensorNeededMessage)
                    .negativeButton()
                    .positiveButton(
                        positiveListener = { dialogInterface: DialogInterface?, _: Int ->
                            run {
                                dialogInterface?.dismiss()
                                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            }
                        })
                    .show()
            } else {
                viewModel.getUserCurrentLocation(fusedLocationClient.value) { location ->
                    location?.let {
                        userLocation = it
                    }
                }
            }
        }

        fun attemptToSetupUserLocation() {
            if (DiagnosisHelper.hasFineLocationPermission(this@FindMyVehicleActivity)) {
                setupUserLocation()
            } else {
                requestFineLocationPermission { granted ->
                    if (granted) {
                        setupUserLocation()
                    }
                }
            }
        }

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    attemptToSetupUserLocation()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        Column(Modifier.fillMaxSize()) {
            vehicleLastKnownCoordinates?.let { coordinates ->

                Box(Modifier.weight(1f)) {
                    FindMyVehicleMap(
                        vehicleLastKnownCoordinates = coordinates,
                        userLocation = userLocation
                    )
                }
                FindMyVehicleContent(vehicleCoordinates = coordinates, userLocation = userLocation)

            } ?: Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                DKText(
                    text = stringResource(R.string.dk_find_vehicle_empty),
                    style = DKStyle.SMALL_TEXT,
                    textAlign = TextAlign.Center,
                    color = colorResource(textColorResId),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(DKColors.neutralColor))
                        .padding(16.dp)
                        .wrapContentSize()
                )
            }
        }
    }

    private fun requestFineLocationPermission(callback: (granted: Boolean) -> Unit) {
        permissionCallback = object :
            OnPermissionCallback {
            override fun onPermissionGranted(permissionName: Array<String>) {
                callback(true)
            }

            override fun onPermissionDeclined(permissionName: Array<String>) {
                handlePermissionDeclined(
                    this@FindMyVehicleActivity,
                    com.drivequant.drivekit.permissionsutils.R.string.dk_perm_utils_app_diag_activity_ko
                ) { requestFineLocationPermission(callback) }
            }

            override fun onPermissionTotallyDeclined(permissionName: String) {
                handlePermissionTotallyDeclined(
                    this@FindMyVehicleActivity,
                    com.drivequant.drivekit.permissionsutils.R.string.dk_perm_utils_app_diag_activity_ko
                )
            }
        }
        request(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @Composable
    fun FindMyVehicleMap(vehicleLastKnownCoordinates: LatLng, userLocation: Location?) {
        val initialCameraPosition =
            CameraPosition.fromLatLngZoom(vehicleLastKnownCoordinates, INITIAL_ZOOM_LEVEL)

        val cameraPositionState = rememberCameraPositionState {
            position = initialCameraPosition
        }

        fun animateMapToIncludeUserAndVehicle() {
            userLocation?.let { location ->
                DriveKitVehicleUI.coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngBounds(
                            LatLngBounds.Builder()
                                .include(LatLng(location.latitude, location.longitude))
                                .include(vehicleLastKnownCoordinates).build(), MAP_REGION_PADDING
                        ), 500
                    )
                }
            }
        }
        LaunchedEffect(userLocation) {
            animateMapToIncludeUserAndVehicle()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxWidth(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    mapToolbarEnabled = false
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
            FloatingActionButton(
                onClick = {
                    animateMapToIncludeUserAndVehicle()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                backgroundColor = Color.White
            ) {
                Image(
                    painter = painterResource(id = com.drivequant.drivekit.common.ui.R.drawable.dk_common_center_map),
                    modifier = Modifier.size(32.dp),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Black)
                )
            }
        }
    }

    @Composable
    fun UserMapMarker(userLocation: Location) {
        val userLatLng = LatLng(userLocation.latitude, userLocation.longitude)

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
        val vehicleAccuracyLevel = viewModel.getVehicleLastKnownLocationAccuracyLevel()

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
        
        viewModel.getTargetLocationIcon(this@FindMyVehicleActivity)?.let { icon ->
            Marker(
                state = vehicleMarkerState,
                icon = icon,
                anchor = Offset(0.5f, 0.5f),
                title = addressState?.address
            )
            if (vehicleAccuracyLevel == DKCoordinateAccuracy.POOR) {
                viewModel.getVehicleLastKnownLocationAccuracyMeters()?.let { accuracy ->
                    Circle(
                        vehicleLastKnownCoordinates,
                        radius = accuracy,
                        fillColor = Color(DKColors.primaryColor).copy(alpha = 0.3f),
                        strokeWidth = 0f
                    )
                }
            }
        }
    }

    @Composable
    fun FindMyVehicleContent(vehicleCoordinates: LatLng, userLocation: Location?) {
        var userDistanceToVehicle by remember {
            mutableStateOf<Meter?>(null)
        }
        var vehicleLastKnownLocationDate by remember {
            mutableStateOf<Date?>(null)
        }
        userLocation?.let {
            viewModel.getDistanceToVehicleLastKnownLocationInMeters(userLocation) { maybeDistance ->
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
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DKText(
                    text = stringResource(R.string.dk_find_vehicle_date, date, time),
                    DKStyle.NORMAL_TEXT, color = colorResource(textColorResId)
                )
                userDistanceToVehicle?.let { distance ->
                    VehicleDistance(distance)
                }
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    ItineraryButton(vehicleCoordinates)
                }
            }
        }
    }

    @Composable
    private fun ItineraryButton(coordinates: LatLng) {
        DKPrimaryButton(
            stringResource(R.string.dk_find_vehicle_itinerary),
            onClick = {
            val coordinatesForUri = "${coordinates.latitude},${coordinates.longitude}"
            val navigationIntentUri =
                "geo:$coordinatesForUri?q=$coordinatesForUri".toUri()
            val mapIntent = Intent(Intent.ACTION_VIEW, navigationIntentUri)
            startActivity(mapIntent)
        })
    }

    @Composable
    private fun VehicleDistance(distance: Meter) {
        val text = if (distance.value < VEHICLE_NEARBY_THRESHOLD.value) {
            when (DriveKitUI.unitSystem) {
                DKUnitSystem.METRIC -> R.string.dk_find_vehicle_location_very_close
                DKUnitSystem.IMPERIAL -> R.string.dk_find_vehicle_location_very_close_imperial
            }.let {
                stringResource(it, VEHICLE_NEARBY_THRESHOLD.value.roundToInt())
            }
        } else if (distance.value < VEHICLE_FAR_THRESHOLD.value) {
            val nearbyRoundingValue = 100
            val roundedNearbyDistance =
                (distance.value / nearbyRoundingValue).roundToInt() * nearbyRoundingValue
            // We don't handle specific conversion as yards and meters are close enough with a 100m rounding
            when (DriveKitUI.unitSystem) {
                DKUnitSystem.METRIC -> R.string.dk_find_vehicle_location_nearby
                DKUnitSystem.IMPERIAL -> R.string.dk_find_vehicle_location_nearby_imperial
            }.let {
                stringResource(it, roundedNearbyDistance)
            }
        } else {
            val roundedFarDistance = distance.toKilometers()
            when (DriveKitUI.unitSystem) {
                DKUnitSystem.METRIC -> {
                    stringResource(
                        R.string.dk_find_vehicle_location_far, roundedFarDistance.value.roundToInt()
                    )
                }

                DKUnitSystem.IMPERIAL -> {
                    stringResource(
                        R.string.dk_find_vehicle_location_far_imperial,
                        roundedFarDistance.toMiles().value.roundToInt()
                    )
                }
            }
        }
        DKText(text = text, DKStyle.NORMAL_TEXT, color = colorResource(textColorResId))
    }
}