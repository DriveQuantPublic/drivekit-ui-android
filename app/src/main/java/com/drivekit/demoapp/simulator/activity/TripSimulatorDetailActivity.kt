package com.drivekit.demoapp.simulator.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.drivekit.demoapp.component.ChartEntry
import com.drivekit.demoapp.component.TripSimulatorGraphView
import com.drivekit.demoapp.simulator.viewmodel.PresetTripType
import com.drivekit.demoapp.simulator.viewmodel.TripSimulatorDetailViewModel
import com.drivekit.demoapp.simulator.viewmodel.TripSimulatorDetailViewModelListener
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.highlightSmall
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import kotlinx.android.synthetic.main.activity_trip_simulator_detail.*

internal class TripSimulatorDetailActivity : AppCompatActivity(), TripSimulatorDetailViewModelListener {

    private lateinit var viewModel: TripSimulatorDetailViewModel
    private lateinit var graphView: TripSimulatorGraphView
    private lateinit var presetTripType: PresetTripType

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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_simulator_detail)

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = getString(R.string.trip_simulator_header)

        presetTripType = intent.getSerializableExtra(PRESET_TYPE_EXTRA) as PresetTripType

        (savedInstanceState?.getSerializable("presetTripTypeTag"))?.let { it ->
            presetTripType = it as PresetTripType
        }

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(
                this,
                TripSimulatorDetailViewModel.TripSimulatorDetailViewModelFactory(presetTripType)
            )[TripSimulatorDetailViewModel::class.java]
        }

        if (!this::graphView.isInitialized) {
            graphView = TripSimulatorGraphView(this)
            graph_container.addView(graphView)
        }

        viewModel.registerListener(this@TripSimulatorDetailActivity)

        initContent()
        startStopSimulation()
        updateContent()
    }

    private fun initContent() {
        text_view_title.apply {
            text = getString(presetTripType.getTitleResId())
            highlightSmall()
        }
        text_view_description.apply {
            text = getString(presetTripType.getDescriptionResId())
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
        simulation_run_duration.setItemTitle(getString(R.string.trip_simulator_run_duration))
        simulation_run_time.setItemTitle(getString(R.string.trip_simulator_run_time))
        simulation_run_velocity.setItemTitle(getString(R.string.trip_simulator_run_velocity))
        simulation_run_sdk_state.setItemTitle(getString(R.string.trip_simulator_run_sdk_state))
    }

    private fun startStopSimulation() {
        button_stop_start_trip_simulator.findViewById<Button>(R.id.button_action).apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                if (viewModel.isSimulating) {
                    showStopSimulationPopup { updateContent() }
                } else {
                    viewModel.startSimulation()
                    graphView.clean()
                }
                updateContent()
            }
        }
    }

    private fun showStopSimulationPopup(onStopSimulation: () -> Unit) {
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(this@TripSimulatorDetailActivity)
            .layout(R.layout.template_alert_dialog_layout)
            .positiveButton(getString(R.string.button_stop)) { _, _ ->
                viewModel.stopSimulation()
                onStopSimulation.invoke()
            }
            .negativeButton(getString(R.string.dk_common_cancel)) { dialog, _ -> dialog.dismiss() }
            .show()

        val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView =
            alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
        titleTextView?.text = getString(R.string.trip_simulator_stop_simulation_alert_title)
        descriptionTextView?.text = getString(R.string.trip_simulator_stop_simulation_alert_content)
        titleTextView?.headLine1()
        descriptionTextView?.normalText()
    }

    private fun updateContent() {
        simulation_run_duration.setItemValue(viewModel.getTotalDuration())
        simulation_run_time.setItemValue(viewModel.getSpentDuration())
        simulation_run_velocity.setItemValue(viewModel.getVelocity(this@TripSimulatorDetailActivity))
        simulation_run_sdk_state.setItemValue(viewModel.getState())
        simulation_automatic_stop_in.apply {
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
            button_stop_start_trip_simulator.findViewById<Button>(R.id.button_action).text = getString(it)
        }
    }

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
