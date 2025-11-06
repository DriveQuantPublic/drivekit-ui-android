package com.drivequant.drivekit.vehicle.ui.findmyvehicle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager

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
    }
}