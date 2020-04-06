package com.drivequant.drivekit.vehicle.ui.picker.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.picker.VehiclePickerStatus
import com.drivequant.drivekit.vehicle.picker.VehicleVersion
import com.drivequant.drivekit.vehicle.ui.R
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
        const val REQUEST_VEHICLE_PICKER = 100
        private var vehicleToDelete: Vehicle? = null
        @JvmOverloads
        fun launchActivity(activityContext: Context, vehicleToDelete: Vehicle? = null) {
            this.vehicleToDelete = vehicleToDelete
            val intent = Intent(activityContext, VehiclePickerActivity::class.java)
            val activity = activityContext as Activity
            activity.startActivityForResult(intent, REQUEST_VEHICLE_PICKER)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_picker)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        setTitle(R.string.dk_vehicle_my_vehicle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel = ViewModelProviders.of(this, VehiclePickerViewModel.VehiclePickerViewModelFactory()).get(VehiclePickerViewModel::class.java)

        vehicleToDelete?.let {
            viewModel.vehicleToDelete = it
        }

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

        viewModel.fetchServiceErrorObserver.observe(this, Observer {
            it?.let {
                if (it == VehiclePickerStatus.NO_RESULT){
                    Toast.makeText(this, DKResource.convertToString(this, "dk_vehicle_no_data"), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, DKResource.convertToString(this, "dk_vehicle_error_message"), Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.endObserver.observe(this, Observer {
            it?.let { vehiclePickerStatus ->
                if (vehiclePickerStatus == VehiclePickerStatus.SUCCESS) {
                    finish()
                } else {
                    Toast.makeText(this, DKResource.convertToString(this, "dk_vehicle_failed_to_retrieve_vehicle_data"), Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.computeNextScreen(this, null)
    }


    override fun onSelectedItem(currentPickerStep: VehiclePickerStep, item: VehiclePickerItem) {
        var otherAction = false
        when (currentPickerStep){
            TYPE -> {
                viewModel.selectedVehicleTypeItem = VehicleTypeItem.valueOf(item.value)
            }
            CATEGORY -> {
                viewModel.selectedCategory = viewModel.selectedVehicleTypeItem.getCategories(this).find { it.category == item.value }!!
            }
            BRANDS_ICONS -> {
                if (item.value != "OTHER_BRANDS"){
                    viewModel.selectedBrand = VehicleBrand.valueOf(item.value)
                } else {
                    otherAction = true
                }
            }
            BRANDS_FULL -> {
                viewModel.selectedBrand = VehicleBrand.valueOf(item.value)
            }
            ENGINE -> {
                viewModel.selectedEngineIndex = VehicleEngineIndex.getEnumByName(item.value)
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
        viewModel.computeNextScreen(this, currentPickerStep, otherAction)
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

    private fun dispatchToScreen(vehiclePickerStep: VehiclePickerStep){
        val fragment = when (vehiclePickerStep){
            CATEGORY_DESCRIPTION -> VehicleCategoryDescriptionFragment.newInstance(viewModel)
            NAME -> VehicleNameChooserFragment.newInstance(viewModel)
            else -> VehicleItemListFragment.newInstance(viewModel, vehiclePickerStep, viewModel.getItemsByStep(vehiclePickerStep))
        }

        updateTitle(vehiclePickerStep)

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_left, R.animator.slide_out_right)
            .addToBackStack(vehiclePickerStep.name)
            .add(R.id.container, fragment)
            .commit()
    }

    private fun updateTitle(vehiclePickerStep: VehiclePickerStep){
        val screenTitle = when (vehiclePickerStep){
            CATEGORY_DESCRIPTION -> viewModel.selectedCategory.title
            NAME -> DKResource.convertToString(this, "dk_vehicle_name")
            else -> DKResource.convertToString(this, "dk_vehicle_my_vehicle")
        }
        screenTitle?.let {
            this.title = it
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1){
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}