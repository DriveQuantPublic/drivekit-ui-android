package com.drivekit.demoapp.simulator.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.drivekit.demoapp.simulator.viewmodel.PresetTripType
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import kotlinx.android.synthetic.main.activity_trip_simulator_detail.*

class TripSimulatorDetailActivity : AppCompatActivity() {

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
        text_view_title.text = getString(presetTripType.getTitleResId())
        text_view_description.text = getString(presetTripType.getDescriptionResId())

        button_stop_simulation.apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            setOnClickListener {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(this@TripSimulatorDetailActivity)
                    .layout(com.drivequant.drivekit.challenge.ui.R.layout.template_alert_dialog_layout)
                    .positiveButton(getString(R.string.button_stop)) { dialog, _ ->
                        //TODO stop simulation
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
            }
        }

        simulation_run_duration.apply {
            setItemTitle(getString(R.string.trip_simulator_run_duration))
            setItemValue("08:52")
        }

        simulation_run_time.apply {
            setItemTitle(getString(R.string.trip_simulator_run_time))
            setItemValue("00:53")
        }
        simulation_run_velocity.apply {
            setItemTitle(getString(R.string.trip_simulator_run_velocity))
            setItemValue("35 km/h")
        }

        simulation_run_sdk_state.apply {
            setItemTitle(getString(R.string.trip_simulator_run_sdk_state))
            setItemValue("RUNNING")
        }
        simulation_automatic_stop_in.apply {
            setItemTitle(getString(R.string.trip_simulator_automatic_stop_in))
            setItemValue("04:20")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}