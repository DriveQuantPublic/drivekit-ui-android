package com.drivekit.demoapp.simulator.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.drivekit.demoapp.simulator.viewmodel.TripSimulatorViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivekit.drivekitdemoapp.databinding.ActivityTripSimulatorBinding
import com.drivequant.drivekit.common.ui.extension.highlightSmall
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager

internal class TripSimulatorActivity : AppCompatActivity() {

    val viewModel = TripSimulatorViewModel()
    private lateinit var binding: ActivityTripSimulatorBinding

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, TripSimulatorActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityTripSimulatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.root.findViewById(com.drivequant.drivekit.common.ui.R.id.dk_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setActivityTitle(getString(R.string.trip_simulator_header))

        binding.textViewSelectTrip.highlightSmall()
        binding.textViewDescription.normalText()
        binding.textViewTripDescription.normalText()
        initFilter()
        checkSimulationError()

        binding.root.findViewById<Button>(R.id.button_action).apply {
            text = getString(R.string.trip_simulator_start_button)
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

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(binding.root) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
        }
    }

    private fun checkSimulationError() {
        binding.textViewErrorMessage.apply {
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
                    l: Long
                ) {
                    (adapterView?.getChildAt(0) as? TextView)?.setTextColor(DKColors.mainFontColor)
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
            .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
            .positiveButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        val titleTextView =
            alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
        val descriptionTextView =
            alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)
        titleTextView?.text = getString(R.string.app_name)
        descriptionTextView?.text = getString(errorMessageResId)
    }
}
