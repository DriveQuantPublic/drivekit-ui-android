package com.drivequant.drivekit.vehicle.ui.picker.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.drivequant.drivekit.vehicle.enum.VehicleBrand
import com.drivequant.drivekit.vehicle.enum.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.VehiclePickerViewConfig
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.*
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleItemListFragment
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CarCategory
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.CategoryType.*
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.VehicleType

class VehiclePickerActivity : AppCompatActivity(), VehicleItemListFragment.OnListFragmentInteractionListener {

    lateinit var vehicleType: VehicleType
    lateinit var vehicleCategory: CarCategory
    lateinit var vehicleBrand: VehicleBrand
    lateinit var vehicleModel: String
    lateinit var vehicleEngineIndex: VehicleEngineIndex

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
                vehicleCategory = CarCategory.valueOf(item.value)
                if (viewConfig.displayBrandsWithIcons){
                    dispatchToScreen(BRANDS_ICONS)
                } else {
                    dispatchToScreen(BRANDS_FULL)
                }
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

    private fun dispatchToScreen(vehiclePickerStep: VehiclePickerStep){
        val fragment = VehicleItemListFragment.newInstance(vehiclePickerStep, viewConfig)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_left, R.animator.slide_out_right)
            .addToBackStack(vehiclePickerStep.name)
            .replace(R.id.container, fragment)
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