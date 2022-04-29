package com.drivekit.demoapp.simulator.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivekit.demoapp.component.ChartEntry
import com.drivekit.demoapp.component.TripSimulatorGraphView
import com.drivekit.demoapp.simulator.viewmodel.PresetTripType
import com.drivekit.demoapp.simulator.viewmodel.TripSimulatorDetailViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.highlightSmall
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import kotlinx.android.synthetic.main.activity_trip_simulator_detail.*

class TripSimulatorDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: TripSimulatorDetailViewModel

    companion object {
        const val PRESET_TYPE_EXTRA = "preset-extra"

        fun launchActivity(activity: Activity, presetTripType: PresetTripType) {
            val intent = Intent(activity, TripSimulatorDetailActivity::class.java)
            intent.putExtra(PRESET_TYPE_EXTRA, presetTripType)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_simulator_detail)

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = getString(R.string.trip_simulator_header)

        val presetTripType = intent.getSerializableExtra(PRESET_TYPE_EXTRA) as PresetTripType

        viewModel = ViewModelProviders.of(
            this,
            TripSimulatorDetailViewModel.TripSimulatorDetailViewModelFactory(presetTripType)).get(TripSimulatorDetailViewModel::class.java)

        text_view_title.apply {
           text = getString(presetTripType.getTitleResId())
            headLine1()
        }

        text_view_description.text = getString(presetTripType.getDescriptionResId())
        button_stop_simulation.apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                if (viewModel.isSimulating) {
                    val alertDialog = DKAlertDialog.LayoutBuilder()
                        .init(this@TripSimulatorDetailActivity)
                        .layout(com.drivequant.drivekit.challenge.ui.R.layout.template_alert_dialog_layout)
                        .positiveButton(getString(R.string.button_stop)) { _, _ ->
                            viewModel.stopSimulation()
                            updateContent()
                            text = getString(R.string.trip_simulator_restart_button)
                        }
                        .negativeButton(getString(R.string.dk_common_cancel)) { dialog, _ -> dialog.dismiss() }
                        .show()

                    val titleTextView =
                        alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                    val descriptionTextView =
                        alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                    titleTextView?.text = getString(R.string.app_name)
                    descriptionTextView?.text =
                        getString(R.string.trip_simulator_stop_simulation_content)
                    titleTextView?.headLine1()
                    descriptionTextView?.normalText()
                } else {
                    viewModel.startSimulation()
                }
            }
        }

        val graphView = TripSimulatorGraphView(this)
        graph_container.addView(graphView)
        viewModel.startSimulation()
        viewModel.currentSpeed.observe(this) {
            updateContent()
            graphView.configure(ChartEntry(it, getString(R.string.trip_simulator_graph_velocity), R.color.colorPrimary))
        }

    }

    private fun updateContent() {
        simulation_run_duration.apply {
            setItemTitle(getString(R.string.trip_simulator_run_duration))
            setItemValue(viewModel.getTotalDuration())
        }

        simulation_run_time.apply {
            setItemTitle(getString(R.string.trip_simulator_run_time))
            setItemValue(viewModel.getSpentDuration())
        }
        simulation_run_velocity.apply {
            setItemTitle(getString(R.string.trip_simulator_run_velocity))
            setItemValue(viewModel.getVelocity(this@TripSimulatorDetailActivity))
        }

        simulation_run_sdk_state.apply {
            setItemTitle(getString(R.string.trip_simulator_run_sdk_state))
            setItemValue(viewModel.getState())
        }
        simulation_automatic_stop_in.apply {
            visibility = if (viewModel.shouldDisplayStoppingMessage()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            setItemValue(viewModel.getRemainingTimeToStop())
            setItemTitle(getString(R.string.trip_simulator_automatic_stop_in))
        }

        if (viewModel.isSimulating) {
            R.string.trip_simulator_stop_button
        } else {
            R.string.trip_simulator_restart_button
        }.let {
            button_stop_simulation.text = getString(it)
        }
    }

    override fun onBackPressed() {
        if (viewModel.isSimulating) {
            val alertDialog = DKAlertDialog.LayoutBuilder()
                .init(this@TripSimulatorDetailActivity)
                .layout(com.drivequant.drivekit.challenge.ui.R.layout.template_alert_dialog_layout)
                .positiveButton(getString(R.string.button_stop)) { _, _ ->
                    viewModel.stopSimulation()
                    finish()
                }
                .negativeButton(getString(R.string.dk_common_cancel)) { dialog, _ -> dialog.dismiss() }
                .show()

            val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
            val descriptionTextView =
                alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
            titleTextView?.text = getString(R.string.app_name)
            descriptionTextView?.text = getString(R.string.trip_simulator_stop_simulation_content)
            titleTextView?.headLine1()
            descriptionTextView?.normalText()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}