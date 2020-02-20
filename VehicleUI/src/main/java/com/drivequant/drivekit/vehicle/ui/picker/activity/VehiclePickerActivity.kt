package com.drivequant.drivekit.vehicle.ui.picker.activity

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
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CategoryType.*

class VehiclePickerActivity : AppCompatActivity(), VehicleItemListFragment.OnListFragmentInteractionListener {

    private lateinit var viewModel : VehiclePickerViewModel

    lateinit var vehicleType: VehicleType
    lateinit var vehicleCategory: VehicleCategoryItem
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

        computeFirstScreen()
        currentStep?.let {
            dispatchToScreen(it)
        }
    }


    override fun onSelectedItem(vehiclePickerStep: VehiclePickerStep, item: VehiclePickerItem) {
        when (vehiclePickerStep){
            TYPE -> {
                vehicleType = VehicleType.valueOf(item.value)
                if (shouldDisplayCategoryScreen()){
                    dispatchToScreen(CATEGORY)
                } else {
                    if (viewConfig.displayBrandsWithIcons) {
                        dispatchToScreen(BRANDS_ICONS)
                    } else {
                        dispatchToScreen(BRANDS_FULL)
                    }
                }
            }
            CATEGORY -> {
                vehicleCategory = vehicleType.getCategories(this).find { it.category == item.value }!!
                //dispatchToScreen(CATEGORY_DESCRIPTION)
                if (viewConfig.displayBrandsWithIcons){
                    dispatchToScreen(BRANDS_ICONS)
                } else {
                    dispatchToScreen(BRANDS_FULL)
                }
            }
            CATEGORY_DESCRIPTION -> {
                val itemTest = 12

            }
            BRANDS_ICONS -> {
                vehicleBrand = VehicleBrand.valueOf(item.value)
                dispatchToScreen(ENGINE) // TODO
            }
            BRANDS_FULL -> {
                vehicleBrand = VehicleBrand.valueOf(item.value)
                dispatchToScreen(ENGINE)
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
            // TODO Odometer
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun computeFirstScreen(){
        when (viewConfig.vehicleTypes.size) {
            0 -> throw IllegalArgumentException("VehicleType in VehiclePickerViewConfig must have at least 1 item")
            1 -> {
                vehicleType = viewConfig.vehicleTypes.first()
                currentStep = CATEGORY
            }
            !in 0..1 -> {
                currentStep = TYPE
            }
        }
    }

    fun dispatchToScreen(vehiclePickerStep: VehiclePickerStep){
        val fragment = when (vehiclePickerStep){
            CATEGORY_DESCRIPTION -> VehicleCategoryDescriptionFragment.newInstance(vehicleCategory, viewConfig)
            NAME -> VehicleNameChooserFragment.newInstance(viewModel, vehicleVersion, viewConfig)
            else -> VehicleItemListFragment.newInstance(viewModel, vehiclePickerStep, viewConfig)
        }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_left, R.animator.slide_out_right)
            .addToBackStack(vehiclePickerStep.name)
            .add(R.id.container, fragment)
            .commit()
    }

    private fun shouldDisplayCategoryScreen(): Boolean {
        return when (viewConfig.categoryTypes) {
            LITE_CONFIG_ONLY,
            BOTH_CONFIG -> true
            BRANDS_CONFIG_ONLY -> false
        }
    }
}