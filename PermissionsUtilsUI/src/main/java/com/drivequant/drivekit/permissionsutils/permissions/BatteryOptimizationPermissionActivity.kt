package com.drivequant.drivekit.permissionsutils.permissions

import android.os.Bundle
import com.drivequant.drivekit.permissionsutils.R

class BatteryOptimizationPermissionActivity : BasePermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_optimization)
    }
}
