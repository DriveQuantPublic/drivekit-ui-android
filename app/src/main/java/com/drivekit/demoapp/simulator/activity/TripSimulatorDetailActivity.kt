package com.drivekit.demoapp.simulator.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.drivekit.demoapp.component.ChartEntry
import com.drivekit.demoapp.component.TripSimulatorGraphView
import com.drivekit.demoapp.simulator.viewmodel.PresetTripType
import com.drivekit.demoapp.simulator.viewmodel.TripSimulatorDetailViewModel
import com.drivekit.demoapp.simulator.viewmodel.TripSimulatorDetailViewModelListener
import com.drivekit.drivekitdemoapp.R
import com.drivekit.drivekitdemoapp.databinding.ActivityTripSimulatorDetailBinding
import com.drivequant.drivekit.common.ui.extension.getSerializableCompat
import com.drivequant.drivekit.common.ui.extension.highlightSmall
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.core.extension.getSerializableExtraCompat

internal class TripSimulatorDetailActivity : AppCompatActivity(), TripSimulatorDetailViewModelListener {

    private lateinit var viewModel: TripSimulatorDetailViewModel
    private lateinit var graphView: TripSimulatorGraphView
    private lateinit var presetTripType: PresetTripType
    private lateinit var binding: ActivityTripSimulatorDetailBinding

    companion object {
        const val PRESET_TYPE_EXTRA = "preset-extra"

        fun launchActivity(activity: Activity, presetTripType: PresetTripType) {
            val intent = Intent(activity, TripSimulatorDetailActivity::class.java)
            intent.putExtra(PRESET_TYPE_EXTRA, presetTripType)
            activity.startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::presetTripType.isInitialized) {
            outState.putSerializable("presetTripTypeTag", presetTripType)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityTripSimulatorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.root.findViewById(com.drivequant.drivekit.common.ui.R.id.dk_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setActivityTitle(getString(R.string.trip_simulator_header))

        presetTripType = intent.getSerializableExtraCompat(PRESET_TYPE_EXTRA, PresetTripType::class.java)!!

        savedInstanceState?.getSerializableCompat("presetTripTypeTag", PresetTripType::class.java)?.let {
            presetTripType = it
        }

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(
                this,
                TripSimulatorDetailViewModel.TripSimulatorDetailViewModelFactory(presetTripType)
            )[TripSimulatorDetailViewModel::class.java]
        }

        if (!this::graphView.isInitialized) {
            graphView = TripSimulatorGraphView(this)
            binding.graphContainer.addView(graphView)
        }

        viewModel.registerListener(this@TripSimulatorDetailActivity)

        val startStopButton = binding.root.findViewById<Button>(R.id.button_action)

        initContent()
        startStopSimulation(startStopButton)
        updateContent()

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(binding.root) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
        }
    }

    private fun initContent() {
        binding.textViewTitle.apply {
            text = getString(presetTripType.getTitleResId())
            highlightSmall()
        }
        binding.textViewDescription.apply {
            text = getString(presetTripType.getDescriptionResId())
            normalText()
        }
        binding.simulationRunDuration.setItemTitle(getString(R.string.trip_simulator_run_duration))
        binding.simulationRunTime.setItemTitle(getString(R.string.trip_simulator_run_time))
        binding.simulationRunVelocity.setItemTitle(getString(R.string.trip_simulator_run_velocity))
        binding.simulationRunSdkState.setItemTitle(getString(R.string.trip_simulator_run_sdk_state))
    }

    private fun startStopSimulation(button: Button) {
        button.setOnClickListener {
            if (viewModel.isSimulating) {
                showStopSimulationPopup { updateContent() }
            } else {
                viewModel.startSimulation()
                graphView.clean()
            }
            updateContent()
        }
    }

    private fun showStopSimulationPopup(onStopSimulation: () -> Unit) {
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(this@TripSimulatorDetailActivity)
            .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
            .positiveButton(getString(R.string.button_stop)) { _, _ ->
                viewModel.stopSimulation()
                onStopSimulation.invoke()
            }
            .negativeButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_cancel)) { dialog, _ -> dialog.dismiss() }
            .show()

        val titleTextView = alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
        val descriptionTextView =
            alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)
        titleTextView?.text = getString(R.string.trip_simulator_stop_simulation_alert_title)
        descriptionTextView?.text = getString(R.string.trip_simulator_stop_simulation_alert_content)
    }

    private fun updateContent() {
        binding.simulationRunDuration.setItemValue(viewModel.getTotalDuration())
        binding.simulationRunTime.setItemValue(viewModel.getSpentDuration())
        binding.simulationRunVelocity.setItemValue(viewModel.getVelocity(this@TripSimulatorDetailActivity))
        binding.simulationRunSdkState.setItemValue(viewModel.getState())
        binding.simulationAutomaticStopIn.apply {
            visibility = if (viewModel.shouldDisplayStoppingMessage()) {
                setItemTitle(getString(R.string.trip_simulator_automatic_stop_in))
                setItemValue(viewModel.getRemainingTimeToStop())
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        if (viewModel.isSimulating) {
            R.string.trip_simulator_stop_button
        } else {
            R.string.trip_simulator_restart_button
        }.let {
            binding.root.findViewById<Button>(R.id.button_action).text = getString(it)
        }
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onBackPressed() {
        if (viewModel.isSimulating) {
           showStopSimulationPopup { finish() }
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun updateNeeded(updatedValue: Double?, timestamp: Double?) {
        if (updatedValue != null && timestamp != null) {
            graphView.updateGraph(
                ChartEntry(
                    updatedValue.toFloat(),
                    getString(R.string.trip_simulator_graph_velocity),
                    R.color.colorPrimary
                )
            )
        }
        runOnUiThread {
            updateContent()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.unregisterListener()
    }
}
