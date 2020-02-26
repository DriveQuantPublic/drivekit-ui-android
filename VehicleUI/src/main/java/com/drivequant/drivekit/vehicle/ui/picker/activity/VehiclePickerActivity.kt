package com.drivequant.drivekit.vehicle.ui.picker.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.drivequant.drivekit.vehicle.enum.VehicleBrand
import com.drivequant.drivekit.vehicle.enum.VehicleEngineIndex
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
import kotlinx.android.synthetic.main.activity_vehicle_picker.*

class VehiclePickerActivity : AppCompatActivity(), VehicleItemListFragment.OnListFragmentInteractionListener {

    private lateinit var viewModel : VehiclePickerViewModel

    companion object {
        private lateinit var viewConfig: VehiclePickerViewConfig
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

        viewModel = ViewModelProviders.of(this, VehiclePickerViewModel.VehiclePickerViewModelFactory()).get(VehiclePickerViewModel::class.java)

        viewModel.stepDispatcher.observe(this, Observer {
            viewModel.stepDispatcher.value?.let {
                hideProgressCircular()
                dispatchToScreen(it)
            }
        })

        viewModel.progressBarObserver.observe(this, Observer {
            it?.let {
                if (it){
                    showProgressCircular()
                } else {
                    hideProgressCircular()
                }
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
            BRANDS_ICONS -> {
                viewModel.selectedBrand = VehicleBrand.valueOf(item.value)
            }
            BRANDS_FULL -> {
                viewModel.selectedBrand = VehicleBrand.valueOf(item.value)
            }
            ENGINE -> {

                viewModel.selectedEngineIndex = VehicleEngineIndex.getEnumByValue(item.value)
            }
            MODELS -> {
                viewModel.selectedModel = item.value
            }
            YEARS -> {
                viewModel.selectedYear = item.value
            }
            VERSIONS -> {
                item.text?.let {
                    viewModel.selectedVersion = VehicleVersion(it, item.value)
                }
            }
            else -> {}
        }
        viewModel.computeNextScreen(this, currentPickerStep, viewConfig)
    }

    private fun showProgressCircular() {
        progress_circular.animate()
            .alpha(255f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progress_circular?.visibility = View.VISIBLE
                }
            })
    }

    private fun hideProgressCircular() {
        progress_circular.animate()
            .alpha(0f)
            .setDuration(200L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progress_circular?.visibility = View.GONE
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun updateTitle(title: String?) {
        title?.let {
            // setTitle(it) // TODO implement getTitle in VM
        }
    }

    private fun dispatchToScreen(vehiclePickerStep: VehiclePickerStep){
        val fragment = when (vehiclePickerStep){
            CATEGORY_DESCRIPTION -> VehicleCategoryDescriptionFragment.newInstance(viewModel, viewConfig)
            NAME -> VehicleNameChooserFragment.newInstance(viewModel, viewConfig)
            else -> VehicleItemListFragment.newInstance(viewModel, vehiclePickerStep, viewModel.getItemsByStep(vehiclePickerStep), viewConfig)
        }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_left, R.animator.slide_out_right)
            .addToBackStack(vehiclePickerStep.name)
            .add(R.id.container, fragment)
            .commit()
    }
}