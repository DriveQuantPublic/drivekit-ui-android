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
import com.drivequant.drivekit.common.ui.extension.headLine1
import kotlinx.android.synthetic.main.activity_trip_simulator.*

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

        text_view_developer_error.visibility = if (viewModel.shouldShowWarningMessage()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        updateTripDescription()
        text_view_select_trip.headLine1()
        initFilter()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initFilter() {
        trips_dropdown_spinner.apply {
            setItems(viewModel.getTripSimulatorItems())
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    position: Int,
                    l: Long) {
                    viewModel.selectItem(position)
                    updateTripDescription()
                }
                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        }
    }

    private fun updateTripDescription() {
        text_view_trip_description.text = getString(viewModel.getSelectedItem().getDescription())
    }
}
