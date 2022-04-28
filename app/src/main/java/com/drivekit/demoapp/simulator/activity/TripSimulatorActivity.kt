package com.drivekit.demoapp.simulator.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.Toolbar
import com.drivekit.demoapp.simulator.viewmodel.TripSimulatorViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.highlightSmall
import kotlinx.android.synthetic.main.activity_trip_simulator.*
import android.widget.ArrayAdapter

class TripSimulatorActivity : AppCompatActivity() {

    val viewModel = TripSimulatorViewModel()

    companion object {
        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, TripSimulatorActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_simulator)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = getString(R.string.trip_simulator_header)

        text_view_developer_error.apply {
            setTextColor(DriveKitUI.colors.warningColor())
            visibility = if (viewModel.shouldShowWarningMessage()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        text_view_select_trip.highlightSmall()
        initFilter()
        viewModel.selectedPresetTripType.observe(this) {
            updateTripDescription()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initFilter() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            viewModel.presetTripItems.map { getString(it.getTitleResId()) }
        )

        trips_dropdown_spinner.apply {
            setAdapter(adapter)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    position: Int,
                    l: Long) {
                    viewModel.selectedPresetTripType.postValue(viewModel.presetTripItems[position])
                }
                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        }
    }

    private fun updateTripDescription() {
        viewModel.selectedPresetTripType.value?.let {
            text_view_trip_description.text = getString(it.getDescriptionResId())
        }
    }
}
