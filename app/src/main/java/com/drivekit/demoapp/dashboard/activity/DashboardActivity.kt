package com.drivekit.demoapp.dashboard.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivekit.demoapp.activity.SettingsActivity
import com.drivekit.demoapp.dashboard.viewmodel.DashboardViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.SynthesisCardsViewListener
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardViewPagerFragment
import kotlinx.android.synthetic.main.activity_dashboard.*

internal class DashboardActivity : AppCompatActivity() {
    private lateinit var viewModel: DashboardViewModel
    private var menu: Menu? = null
    private lateinit var startStopTripButton: Button
    private lateinit var tripSimulatorButton: Button

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)

        initFeatureCard()
    }

    override fun onResume() {
        super.onResume()
        showContent()
    }

    private fun showContent() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        }

        title = getString(R.string.dashboard_header)

        initSynthesisTripsCard()
        initLastTripsCard()
        initStartStopTripButton()
        initTripSimulatorButton()
    }

    private fun initSynthesisTripsCard() {
        viewModel.getSynthesisCardsView(object : SynthesisCardsViewListener {
            override fun onViewLoaded(fragment: DKSynthesisCardViewPagerFragment) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container_synthesis, fragment)
                    .commit()
            }
        })
    }

    private fun initLastTripsCard() {
        DriverDataUI.getLastTripsView(HeaderDay.DURATION).let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_last_trips, viewModel.getLastTripsCardsView())
                .commit()
        }
    }

    private fun initFeatureCard() {
        card_features.apply {
            configureTitle(R.string.feature_list)
            configureDescription(R.string.feature_list_description)
            configureTextButton(R.string.button_see_features)
        }
    }

    private fun initStartStopTripButton() {
        button_start_stop_trip.findViewById<Button>(R.id.button_action).apply {
            startStopTripButton = this
            startStopTripButton.text = getString(viewModel.getStartStopTripButtonTitleResId())
            setOnClickListener {
                viewModel.manageStartStopTripButton()
            }
        }

        viewModel.sdkStateObserver.observe(this) {
            startStopTripButton.text = getString(viewModel.getStartStopTripButtonTitleResId())
        }
    }

    private fun initTripSimulatorButton() {
        button_trip_simulator.findViewById<Button>(R.id.button_action).apply {
            tripSimulatorButton = this
            tripSimulatorButton.text = getString(R.string.start_trip) // TODO change key
            setOnClickListener {
                viewModel.manageStartStopTripSimulatorButton()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_action_bar_menu, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}