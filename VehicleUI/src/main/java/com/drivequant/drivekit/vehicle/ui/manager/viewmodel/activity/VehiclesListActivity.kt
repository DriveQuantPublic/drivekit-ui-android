package com.drivequant.drivekit.vehicle.ui.manager.viewmodel.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.manager.viewmodel.fragment.VehiclesListFragment

class VehiclesListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_vehicle_picker)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        supportFragmentManager.beginTransaction()
            .replace(R.id.container, VehiclesListFragment.newInstance())
            .commit()
    }
}