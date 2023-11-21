package com.drivekit.demoapp.simulator.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.drivekit.demoapp.simulator.viewmodel.TripSimulatorViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivekit.drivekitdemoapp.databinding.ActivityTripSimulatorBinding
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.highlightSmall
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog

internal class TripSimulatorActivity : AppCompatActivity() {

    val viewModel = TripSimulatorViewModel()
    private lateinit var binding: ActivityTripSimulatorBinding

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, TripSimulatorActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripSimulatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.dkToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = getString(R.string.trip_simulator_header)

        binding.textViewSelectTrip.highlightSmall()
        binding.textViewDescription.normalText(DriveKitUI.colors.complementaryFontColor())
        binding.textViewTripDescription.normalText(DriveKitUI.colors.complementaryFontColor())
        initFilter()
        checkSimulationError()

        binding.buttonSimulateTrip.buttonAction.apply {
            text = getString(R.string.trip_simulator_start_button)
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                when {
                    viewModel.shouldShowDeveloperModeErrorMessage() -> showErrorPopup(R.string.trip_simulator_error_dev_mode)
                    viewModel.shouldShowMockLocationErrorMessage() -> showErrorPopup(R.string.trip_simulator_error_mock_location)
                    !viewModel.isAutoStartEnabled(this@TripSimulatorActivity) -> showErrorPopup(R.string.trip_simulator_error_auto_mode)
                    !viewModel.hasVehicleAutoStartMode() -> showErrorPopup(R.string.trip_simulator_error_vehicle_disabled)
                    else -> {
                        viewModel.selectedPresetTripType.value?.let { presetTripType ->
                            TripSimulatorDetailActivity.launchActivity(
                                this@TripSimulatorActivity,
                                presetTripType
                            )
                        }
                    }
                }
            }
        }

        viewModel.selectedPresetTripType.observe(this) {
            updateTripDescription()
        }
    }

    private fun checkSimulationError() {
        binding.textViewErrorMessage.apply {
            setTextColor(DriveKitUI.colors.warningColor())
            if (viewModel.shouldShowMockLocationErrorMessage()) {
                visibility = View.VISIBLE
                text = getString(R.string.trip_simulator_error_mock_location)
                if (viewModel.shouldShowDeveloperModeErrorMessage()) {
                    text = getString(R.string.trip_simulator_error_dev_mode)
                }
            } else {
                visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkSimulationError()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initFilter() {
        val adapter = ArrayAdapter(
            this,
            R.layout.trip_simulator_item,
            viewModel.presetTripItems.map { getString(it.getTitleResId()) }
        )

        binding.tripsDropdownSpinner.apply {
            setAdapter(adapter)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    l: Long) {
                    adapterView?.getChildAt(0)?.let {
                        (it as TextView).setTextColor(DriveKitUI.colors.mainFontColor())
                    }
                    viewModel.selectedPresetTripType.postValue(viewModel.presetTripItems[position])
                }
                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        }
    }

    private fun updateTripDescription() {
        viewModel.selectedPresetTripType.value?.let {
            binding.textViewTripDescription.text = getString(it.getDescriptionResId())
        }
    }

    private fun showErrorPopup(errorMessageResId: Int) {
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(this)
            .layout(R.layout.template_alert_dialog_layout)
            .positiveButton(getString(R.string.dk_common_ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        val titleTextView =
            alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView =
            alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
        titleTextView?.text = getString(R.string.app_name)
        descriptionTextView?.text = getString(errorMessageResId)
        titleTextView?.headLine1()
        descriptionTextView?.normalText()
    }
}
