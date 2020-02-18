package com.drivekit.demoapp.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.transition.FragmentTransitionSupport
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.transition.Slide
import android.view.Gravity
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.*
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleItemListFragment
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem

class VehiclePickerActivity : AppCompatActivity(), VehicleItemListFragment.OnListFragmentInteractionListener {

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

        dispatchToScreen(TYPE)
    }

    override fun onSelectedItem(vehiclePickerStep: VehiclePickerStep, item: VehiclePickerItem) {
        when (vehiclePickerStep){
            TYPE -> dispatchToScreen(CATEGORY)
            CATEGORY -> TODO()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun dispatchToScreen(vehiclePickerStep: VehiclePickerStep){
        val fragment = VehicleItemListFragment.newInstance(vehiclePickerStep, viewConfig)
        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}