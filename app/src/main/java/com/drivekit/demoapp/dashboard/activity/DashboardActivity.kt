package com.drivekit.demoapp.dashboard.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivekit.demoapp.component.FeatureCard
import com.drivekit.demoapp.dashboard.enum.InfoBannerType
import com.drivekit.demoapp.dashboard.view.InfoBannerView
import com.drivekit.demoapp.dashboard.viewmodel.DashboardViewModel
import com.drivekit.demoapp.drivekit.TripListenerController
import com.drivekit.demoapp.features.activity.FeatureListActivity
import com.drivekit.demoapp.settings.activity.SettingsActivity
import com.drivekit.demoapp.simulator.activity.TripSimulatorActivity
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.SynthesisCardsViewListener
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardViewPagerFragment
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import kotlinx.android.synthetic.main.activity_dashboard.*

internal class DashboardActivity : AppCompatActivity() {
    private lateinit var viewModel: DashboardViewModel
    private var menu: Menu? = null
    private lateinit var startStopTripButton: Button
    private lateinit var tripSimulatorButton: Button

    companion object {
        fun launchActivity(activity: Activity) {
            val intent = Intent(activity, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity_dashboard)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        title = getString(R.string.dashboard_header)
        initFeatureCard()

        manageTripDetailRedirection()
    }

    override fun onResume() {
        super.onResume()
        checkViewModelInitialization()
        TripListenerController.addSdkStateChangeListener(viewModel.sdkStateChangeListener)
        showContent()
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        }
    }

    private fun showContent() {
        initInfoBanners()
        initSynthesisTripsCard()
        initLastTripsCard()
        initStartStopTripButton()
        initTripSimulatorButton()
    }

    private fun initInfoBanners() {
        info_banners.removeAllViews()
        InfoBannerType.values().forEach {
            if (it.shouldDisplay(this)) {
                val view = InfoBannerView(this, it, object : InfoBannerView.InfoBannerListener {
                    override fun onInfoBannerClicked() {
                        when (it) {
                            InfoBannerType.DIAGNOSIS -> PermissionsUtilsUI.startAppDiagnosisActivity(this@DashboardActivity)
                        }
                    }
                })
                info_banners.addView(view)
            }
        }
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
        checkViewModelInitialization()
        card_features.apply {
            configureTitle(viewModel.getFeatureCardTitleResId())
            configureDescription(viewModel.getFeatureCardDescriptionResId())
            configureActionButton(viewModel.getFeatureCardTextButtonButtonResId(), object : FeatureCard.FeatureCardActionClickListener{
                override fun onButtonClicked() {
                    startActivity(Intent(context, FeatureListActivity::class.java))
                }
            })
        }
    }

    private fun initStartStopTripButton() {
        button_start_stop_trip.findViewById<Button>(R.id.button_action).apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            startStopTripButton = this
            startStopTripButton.text = getString(viewModel.getStartStopTripButtonTitleResId())
            setOnClickListener {
                viewModel.startStopTrip()
            }
        }

        viewModel.sdkStateObserver.observe(this) {
            startStopTripButton.text = getString(viewModel.getStartStopTripButtonTitleResId())
        }
    }

    private fun initTripSimulatorButton() {
        button_trip_simulator.findViewById<Button>(R.id.button_action).apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            tripSimulatorButton = this
            tripSimulatorButton.text = getString(R.string.simulate_trip)
            setOnClickListener {
                TripSimulatorActivity.launchActivity(this@DashboardActivity)
            }
        }
    }

    private fun manageTripDetailRedirection() {
        intent?.let {
            val itinId = intent.getStringExtra(TripDetailActivity.ITINID_EXTRA)
            val openAdvice = intent.getBooleanExtra(TripDetailActivity.OPEN_ADVICE_EXTRA, false)
            val tripListConfigurationType = intent.getSerializableExtra(TripDetailActivity.TRIP_LIST_CONFIGURATION_TYPE_EXTRA) as TripListConfigurationType? ?: TripListConfigurationType.MOTORIZED
            if (!itinId.isNullOrBlank()) {
                TripDetailActivity.launchActivity(this, itinId, openAdvice, tripListConfigurationType)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        TripListenerController.removeSdkStateChangeListener(viewModel.sdkStateChangeListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_action_bar_menu, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                SettingsActivity.launchActivity(this)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}