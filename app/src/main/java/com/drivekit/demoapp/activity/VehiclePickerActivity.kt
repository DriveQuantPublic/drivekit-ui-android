package com.drivekit.demoapp.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleItemListFragment

class VehiclePickerActivity : AppCompatActivity() {

    companion object {
        private lateinit var viewConfig: VehiclePickerViewConfig
        private var currentStep: VehiclePickerStep? = null
        fun launchActivity(context: Context,
                           vehiclePickerViewConfig: VehiclePickerViewConfig = VehiclePickerViewConfig(context)) {
            viewConfig = vehiclePickerViewConfig
            val intent = Intent(context, VehiclePickerActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_picker)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // TODO supportActionBar?.setBackgroundDrawable(ColorDrawable(.tripsViewConfig.primaryColor))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        supportFragmentManager.beginTransaction()
            .replace(R.id.container, VehicleItemListFragment.newInstance(VehiclePickerStep.TYPE, viewConfig))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}