package com.drivequant.drivekit.vehicle.ui.picker.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.databaseutils.entity.Vehicle
import com.drivequant.drivekit.vehicle.enums.TruckType
import com.drivequant.drivekit.vehicle.enums.VehicleBrand
import com.drivequant.drivekit.vehicle.enums.VehicleEngineIndex
import com.drivequant.drivekit.vehicle.picker.VehiclePickerStatus
import com.drivequant.drivekit.vehicle.picker.VehicleVersion
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.databinding.ActivityVehiclePickerBinding
import com.drivequant.drivekit.vehicle.ui.listener.VehiclePickerCompleteListener
import com.drivequant.drivekit.vehicle.ui.odometer.activity.OdometerInitActivity
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep
import com.drivequant.drivekit.vehicle.ui.picker.commons.VehiclePickerStep.*
import com.drivequant.drivekit.vehicle.ui.picker.fragments.DefaultCarEngineSelectionFragment
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleCategoryDescriptionFragment
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleItemListFragment
import com.drivequant.drivekit.vehicle.ui.picker.fragments.VehicleNameChooserFragment
import com.drivequant.drivekit.vehicle.ui.picker.model.VehiclePickerItem
import com.drivequant.drivekit.vehicle.ui.picker.viewmodel.*

class VehiclePickerActivity : AppCompatActivity(), VehicleItemListFragment.OnListFragmentInteractionListener {

    private lateinit var viewModel: VehiclePickerViewModel
    private lateinit var binding: ActivityVehiclePickerBinding
    private var steps: MutableList<VehiclePickerStep> = mutableListOf()
    private val mutex = Any()

    companion object {
        private var vehicleToDelete: Vehicle? = null
        @JvmOverloads
        fun launchActivity(
            activityContext: Context,
            vehicleToDelete: Vehicle? = null,
            listener: VehiclePickerCompleteListener? = null
        ) {
            this.vehicleToDelete = vehicleToDelete
            DriveKitVehicleUI.vehiclePickerComplete = listener
            val intent = Intent(activityContext, VehiclePickerActivity::class.java)
            val activity = activityContext as Activity
            activity.startActivity(intent)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_vehicles_add), javaClass.simpleName)

        binding = ActivityVehiclePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(binding.root.findViewById(R.id.dk_toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel = ViewModelProvider(this, VehiclePickerViewModel.VehiclePickerViewModelFactory())[VehiclePickerViewModel::class.java]

        vehicleToDelete?.let {
            viewModel.vehicleToDelete = it
        }

        viewModel.stepDispatcher.observe(this) {
            viewModel.stepDispatcher.value?.let {
                hideProgressCircular()
                dispatchToScreen(it)
            }
        }

        viewModel.progressBarObserver.observe(this) {
            it?.let {
                if (it) {
                    showProgressCircular()
                } else {
                    hideProgressCircular()
                }
            }
        }

        viewModel.fetchServiceErrorObserver.observe(this) {
            it?.let {
                if (it == VehiclePickerStatus.NO_RESULT) {
                    R.string.dk_vehicle_no_data
                } else {
                    R.string.dk_vehicle_error_message
                }.let { textResId ->
                    Toast.makeText(this, textResId, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.endObserver.observe(this) {
            it?.let { vehiclePickerStatus ->
                if (vehiclePickerStatus == VehiclePickerStatus.SUCCESS) {
                    DriveKitVehicleUI.vehiclePickerComplete?.let { listener ->
                        viewModel.createdVehicleId?.let { vehicleId ->
                            if (DriveKitVehicleUI.hasOdometer) {
                                OdometerInitActivity.launchActivity(
                                    this@VehiclePickerActivity,
                                    vehicleId
                                )
                            } else {
                                listener.onVehiclePickerFinished(vehicleId)
                            }
                            finish()
                        }
                    } ?: run {
                        if (DriveKitVehicleUI.hasOdometer) {
                            viewModel.createdVehicleId?.let { vehicleId ->
                                OdometerInitActivity.launchActivity(
                                    this@VehiclePickerActivity,
                                    vehicleId
                                )
                            }
                        }
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this,
                        R.string.dk_vehicle_failed_to_retrieve_vehicle_data,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        viewModel.computeNextScreen(this, null)

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            addSystemStatusBarTopPadding(findViewById(R.id.toolbar))
            addSystemNavigationBarBottomMargin(findViewById(R.id.container))
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        // Do not want to restore state. So do not call super.
    }

    fun updateTitle(title: String) {
        setActivityTitle(title)
    }

    private fun updateTitle() {
        synchronized(this.mutex) {
            steps.lastOrNull()?.let { updateTitle(it) } ?: updateTitle(getString(R.string.dk_vehicle_my_vehicle))
        }
    }

    override fun onSelectedItem(currentPickerStep: VehiclePickerStep, item: VehiclePickerItem) {
        var otherAction = false
        when (currentPickerStep) {
            TYPE -> viewModel.selectedVehicleTypeItem = VehicleTypeItem.valueOf(item.value)
            TRUCK_TYPE -> viewModel.selectedTruckType = TruckType.getEnumByName(item.value)
            CATEGORY -> viewModel.selectedCategory = viewModel.selectedVehicleTypeItem.getCategories(this).find { it.category == item.value }!!
            BRANDS_ICONS -> {
                if (item.value != "OTHER_BRANDS") {
                    viewModel.selectedBrand = VehicleBrand.valueOf(item.value)
                } else {
                    otherAction = true
                }
            }
            BRANDS_FULL -> viewModel.selectedBrand = VehicleBrand.valueOf(item.value)
            ENGINE -> viewModel.selectedEngineIndex = VehicleEngineIndex.valueOf(item.value)
            MODELS -> viewModel.selectedModel = item.value
            YEARS -> viewModel.selectedYear = item.value
            VERSIONS -> item.text?.let {
                viewModel.selectedVersion = VehicleVersion(it, item.value)
            }
            else -> {}
        }
        viewModel.computeNextScreen(this, currentPickerStep, otherAction)
    }

    private fun showProgressCircular() {
        binding.root.findViewById<ProgressBar>(com.drivequant.drivekit.common.ui.R.id.dk_progress_circular).apply {
            animate()
                .alpha(1f)
                .setDuration(200L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.VISIBLE
                    }
                })
        }
    }

    private fun hideProgressCircular() {
        binding.root.findViewById<ProgressBar>(com.drivequant.drivekit.common.ui.R.id.dk_progress_circular).apply {
            animate()
                .alpha(0f)
                .setDuration(200L)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE
                    }
                })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun dispatchToScreen(vehiclePickerStep: VehiclePickerStep) {
        val fragment = when (vehiclePickerStep) {
            CATEGORY_DESCRIPTION -> VehicleCategoryDescriptionFragment.newInstance(viewModel)
            NAME -> VehicleNameChooserFragment.newInstance(viewModel)
            DEFAULT_CAR_ENGINE -> DefaultCarEngineSelectionFragment.newInstance(viewModel)
            else -> VehicleItemListFragment.newInstance(viewModel, vehiclePickerStep, viewModel.getItemsByStep(vehiclePickerStep))
        }

        synchronized(this.mutex) {
            steps.add(vehiclePickerStep)
        }
        updateTitle()

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_left, R.animator.slide_out_right)
            .addToBackStack(vehiclePickerStep.name)
            .add(R.id.container, fragment)
            .commit()
    }

    private fun updateTitle(vehiclePickerStep: VehiclePickerStep) {
        val screenTitle = when (vehiclePickerStep) {
            CATEGORY_DESCRIPTION -> viewModel.selectedCategory.title
            NAME -> resources.getString(R.string.dk_vehicle_name)
            DEFAULT_CAR_ENGINE -> resources.getString(R.string.dk_motor)
            else -> resources.getString(R.string.dk_vehicle_my_vehicle)
        }
        screenTitle?.let {
            setActivityTitle(it)
        }
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
            synchronized(this.mutex) {
                steps.removeAt(steps.lastIndex)
            }
            updateTitle()
        }
    }

    override fun onResume() {
        super.onResume()
        updateTitle()
    }
}
