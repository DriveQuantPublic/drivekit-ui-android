package com.drivekit.demoapp.dashboard.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.drivekit.demoapp.component.FeatureCard
import com.drivekit.demoapp.dashboard.enum.InfoBannerType
import com.drivekit.demoapp.dashboard.view.InfoBannerView
import com.drivekit.demoapp.dashboard.viewmodel.DashboardViewModel
import com.drivekit.demoapp.drivekit.TripListenerController
import com.drivekit.demoapp.features.activity.FeatureListActivity
import com.drivekit.demoapp.notification.controller.DKNotificationManager
import com.drivekit.demoapp.settings.activity.SettingsActivity
import com.drivekit.demoapp.simulator.activity.TripSimulatorActivity
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.core.extension.getSerializableExtraCompat
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysisUI
import com.drivequant.drivekit.tripanalysis.triprecordingwidget.recordingbutton.DKTripRecordingButton
import com.drivequant.drivekit.tripanalysis.triprecordingwidget.recordingbutton.DKTripRecordingUserMode
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.SynthesisCardsViewListener
import com.drivequant.drivekit.ui.synthesiscards.fragment.DKSynthesisCardViewPagerFragment
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class DashboardActivity : AppCompatActivity() {
    private lateinit var viewModel: DashboardViewModel
    private var menu: Menu? = null
    private var startStopTripButton: DKTripRecordingButton? = null
    private lateinit var tripSimulatorButtonContainer: ViewGroup
    private lateinit var tripSimulatorButton: Button
    private lateinit var infoBanners: ViewGroup

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
        this.infoBanners = findViewById(R.id.info_banners)
        this.tripSimulatorButtonContainer = findViewById(R.id.button_trip_simulator)
        initFeatureCard()
        configureStartStopTripButton()

        if (DKNotificationManager.isTripDetailNotificationIntent(intent)) {
            manageTripDetailRedirection()
        }
        if (DKNotificationManager.isAppDiagnosisNotificationIntent(intent)) {
            DriveKitNavigationController.permissionsUtilsUIEntryPoint?.startAppDiagnosisActivity(this)
        }
        if (DKNotificationManager.isTripAnalysisNotificationIntent(intent)) {
            lifecycleScope.launch {
                delay(300)
                startStopTripButton?.showConfirmationDialog()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkViewModelInitialization()
        TripListenerController.addSdkStateChangeListener(viewModel.sdkStateChangeListener)
        showContent()
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        }
    }

    private fun showContent() {
        initInfoBanners()
        initSynthesisTripsCard()
        initLastTripsCard()
        initTripSimulatorButton()
    }

    private fun initInfoBanners() {
        infoBanners.removeAllViews()
        InfoBannerType.values().forEach {
            if (it.shouldDisplay(this)) {
                val view = InfoBannerView(this, it, object : InfoBannerView.InfoBannerListener {
                    override fun onInfoBannerClicked() {
                        when (it) {
                            InfoBannerType.DIAGNOSIS -> PermissionsUtilsUI.startAppDiagnosisActivity(this@DashboardActivity)
                        }
                    }
                })
                infoBanners.addView(view)
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
        DriverDataUI.getLastTripsView(HeaderDay.DISTANCE).let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_last_trips, viewModel.getLastTripsCardsView())
                .commit()
        }
    }

    private fun initFeatureCard() {
        val cardFeatures: FeatureCard = findViewById(R.id.card_features)
        checkViewModelInitialization()
        cardFeatures.apply {
            configureTitle(viewModel.getFeatureCardTitleResId())
            configureDescription(viewModel.getFeatureCardDescriptionResId())
            configureActionButton(viewModel.getFeatureCardTextButtonButtonResId(), object : FeatureCard.FeatureCardActionClickListener{
                override fun onButtonClicked() {
                    startActivity(Intent(context, FeatureListActivity::class.java))
                }
            })
        }
    }

    private fun initTripSimulatorButton() {
        tripSimulatorButtonContainer.findViewById<Button>(R.id.button_action).apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            tripSimulatorButton = this
            tripSimulatorButton.text = getString(R.string.simulate_trip)
            setOnClickListener {
                TripSimulatorActivity.launchActivity(this@DashboardActivity)
            }
        }
    }

    private fun configureStartStopTripButton() {
        this.startStopTripButton =
            supportFragmentManager.findFragmentById(R.id.button_start_stop_trip) as DKTripRecordingButton
        if (DriveKitTripAnalysisUI.tripRecordingUserMode == DKTripRecordingUserMode.NONE) {
            this.startStopTripButton?.let {
                supportFragmentManager.beginTransaction().remove(it).commit()
                this.startStopTripButton = null
            }
        }
    }

    private fun manageTripDetailRedirection() {
        intent?.let {
            val itinId = intent.getStringExtra(TripDetailActivity.ITINID_EXTRA)
            val openAdvice = intent.getBooleanExtra(TripDetailActivity.OPEN_ADVICE_EXTRA, false)
            val tripListConfigurationType = intent.getSerializableExtraCompat(
                TripDetailActivity.TRIP_LIST_CONFIGURATION_TYPE_EXTRA,
                TripListConfigurationType::class.java
            )
            if (!itinId.isNullOrBlank() && tripListConfigurationType != null) {
                TripDetailActivity.launchActivity(
                    this,
                    itinId,
                    openAdvice,
                    tripListConfigurationType
                )
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
