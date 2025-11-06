package com.drivequant.drivekit.vehicle.ui.findmyvehicle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drivequant.drivekit.common.ui.component.DKText
import com.drivequant.drivekit.common.ui.graphical.DKStyle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

internal open class FindMyVehicleActivity: ComponentActivity() {

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
            val viewModel: FindMyVehicleViewModel = viewModel()
            FindMyVehicleScreen()
        }
    }

    @Composable
    fun FindMyVehicleScreen() {
        FindMyVehicleMap()
        DKText("Test", DKStyle.NORMAL_TEXT)
    }

    @Composable
    fun FindMyVehicleMap() {
        val initialCoordinates = LatLng(37.7749, -122.4194)
        val initialZoomLevel = 10f
        val initialCameraPosition = CameraPosition.fromLatLngZoom(
            initialCoordinates,
            initialZoomLevel
        )
        val initialCameraPositionState = rememberCameraPositionState {
            position = initialCameraPosition
        }


        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = initialCameraPositionState
        )
    }
}