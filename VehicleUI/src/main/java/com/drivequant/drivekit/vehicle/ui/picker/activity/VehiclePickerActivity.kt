package com.drivequant.drivekit.vehicle.ui.picker.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.drivequant.drivekit.vehicle.enum.VehicleBrand
import com.drivequant.drivekit.vehicle.enum.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.picker.VehicleCharacteristics
import com.drivequant.drivekit.vehicle.picker.VehicleVersion
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.*
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleCategoryDescriptionFragment
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleItemListFragment
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleNameChooserFragment
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.*

class VehiclePickerActivity : AppCompatActivity(), VehicleItemListFragment.OnListFragmentInteractionListener {

    private lateinit var viewModel : VehiclePickerViewModel

    lateinit var vehicleBrand: VehicleBrand
    lateinit var vehicleEngineIndex: VehicleEngineIndex
    lateinit var vehicleModel: String
    lateinit var vehicleYear: String
    lateinit var vehicleVersion: VehicleVersion
    lateinit var vehicleCharacteristics: VehicleCharacteristics
    lateinit var vehicleName: String

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


        viewModel = ViewModelProviders.of(this, VehiclePickerViewModelFactory()).get(VehiclePickerViewModel::class.java)
        viewModel.itemMapNewData.observe(this, Observer {
            viewModel.itemMapNewData.value?.let {
                dispatchToScreen(it.keys.first())
            }
        })
        viewModel.computeNextScreen(this, null, viewConfig)
    }


    override fun onSelectedItem(currentPickerStep: VehiclePickerStep, item: VehiclePickerItem) {
        when (currentPickerStep){
            TYPE -> {
                viewModel.selectedVehicleType = VehicleType.valueOf(item.value)
            }
            CATEGORY -> {
                viewModel.selectedCategory = viewModel.selectedVehicleType.getCategories(this).find { it.category == item.value }!!
            }
            CATEGORY_DESCRIPTION -> {
                val itemTest = 12

            }
            BRANDS_ICONS -> {
                viewModel.selectedBrand = VehicleBrand.valueOf(item.value)
            }
            BRANDS_FULL -> {
                viewModel.selectedBrand = VehicleBrand.valueOf(item.value)
            }
            ENGINE -> {
                vehicleEngineIndex = VehicleEngineIndex.getEnumByValue(item.value)
                dispatchToScreen(MODELS)
            }
            MODELS -> {
                vehicleModel = item.value
                dispatchToScreen(YEARS)
            }
            YEARS -> {
                vehicleYear = item.value
                dispatchToScreen(VERSIONS)
            }
            VERSIONS -> {
                item.text?.let {
                    vehicleVersion = VehicleVersion(it, item.value)
                }
                dispatchToScreen(NAME)
            }
            NAME -> {

            }
        }
        viewModel.computeNextScreen(this, currentPickerStep, viewConfig)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun updateTitle(title: String?) {
        title?.let {
            setTitle(it)
        }
    }

    fun dispatchToScreen(vehiclePickerStep: VehiclePickerStep){
        val fragment = when (vehiclePickerStep){
            CATEGORY_DESCRIPTION -> VehicleCategoryDescriptionFragment.newInstance(viewModel.selectedCategory, viewConfig)
            NAME -> VehicleNameChooserFragment.newInstance(viewModel, vehicleVersion, viewConfig)
            else -> VehicleItemListFragment.newInstance(viewModel, vehiclePickerStep, viewConfig)
        }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_left, R.animator.slide_out_right)
            .addToBackStack(vehiclePickerStep.name)
            .add(R.id.container, fragment)
            .commit()
    }
}