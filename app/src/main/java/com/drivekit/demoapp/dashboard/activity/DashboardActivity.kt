package com.drivekit.demoapp.dashboard.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import com.drivekit.demoapp.component.FeatureCard
import com.drivekit.demoapp.dashboard.enum.InfoBannerType
import com.drivekit.demoapp.dashboard.view.InfoBannerView
import com.drivekit.demoapp.dashboard.viewmodel.DashboardViewModel
import com.drivekit.demoapp.drivekit.TripListenerController
import com.drivekit.demoapp.features.activity.FeatureListActivity
import com.drivekit.demoapp.settings.activity.SettingsActivity
import com.drivekit.demoapp.simulator.activity.TripSimulatorActivity
import com.drivekit.demoapp.utils.getSerializableCompat
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.triplist.viewModel.HeaderDay
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKScoreTypeLevels
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
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
            viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
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
        DriverDataUI.getLastTripsView(HeaderDay.DISTANCE).let {
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
        launchLegend() // TODO
        button_start_stop_trip.findViewById<Button>(R.id.button_action).apply {
            setBackgroundColor(DriveKitUI.colors.secondaryColor())
            startStopTripButton = this
            startStopTripButton.text = getString(viewModel.getStartStopTripButtonTitleResId())
            setOnClickListener {
                //viewModel.startStopTrip()
                launchLegend() // TODO
            }
        }

        viewModel.sdkStateObserver.observe(this) {
            startStopTripButton.text = getString(viewModel.getStartStopTripButtonTitleResId())
        }
    }

    private fun launchLegend() {
        val dkScoreType: DKScoreType = DKScoreType.SAFETY // TODO mock
        val items = listOf(
            Pair(DKScoreTypeLevels.EXCELLENT, R.id.score_item_excellent),
            Pair(DKScoreTypeLevels.VERY_GOOD, R.id.score_item_very_good),
            Pair(DKScoreTypeLevels.GREAT, R.id.score_item_great),
            Pair(DKScoreTypeLevels.MEDIUM, R.id.score_item_medium),
            Pair(DKScoreTypeLevels.NOT_GOOD, R.id.score_item_not_good),
            Pair(DKScoreTypeLevels.BAD, R.id.score_item_bad),
            Pair(DKScoreTypeLevels.VERY_BAD, R.id.score_item_very_bad)
        )
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(this)
            .layout(R.layout.dk_my_synthesis_scores_legend_alert_dialog)
            .positiveButton(getString(R.string.dk_common_close)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        // Title
        alertDialog.findViewById<TextView>(R.id.my_synthesis_score_legend_title)?.apply {
            text = when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_safety_score
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_eco_score
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_distraction_score
                DKScoreType.SPEEDING -> R.string.dk_driverdata_speeding_score
            }.let {
                getString(it)
            }
            headLine2(DriveKitUI.colors.primaryColor())
        }

        // Description
        alertDialog.findViewById<TextView>(R.id.my_synthesis_score_legend_description)?.apply {
            text = when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_score_info
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_score_info
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_score_info
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_score_info
            }.let {
                getString(it)
            }
            smallText()
        }

        items.forEach {
            configureScoreItem(it.first, alertDialog.findViewById(it.second), dkScoreType)
        }
    }

    private fun configureScoreItem(
        scoreLevel: DKScoreTypeLevels,
        view: LinearLayout?,
        dkScoreType: DKScoreType
    ) {
        view?.findViewById<View>(R.id.score_color)?.let { scoreColor ->
            DrawableCompat.setTint(
                scoreColor.background,
                ContextCompat.getColor(scoreColor.context, scoreLevel.getColorResId())
            )
        }

        view?.findViewById<TextView>(R.id.score_description)?.apply {
            val text: String = when (scoreLevel) {
                DKScoreTypeLevels.EXCELLENT -> R.string.dk_driverdata_mysynthesis_score_title_excellent
                DKScoreTypeLevels.VERY_GOOD -> R.string.dk_driverdata_mysynthesis_score_title_very_good
                DKScoreTypeLevels.GREAT -> R.string.dk_driverdata_mysynthesis_score_title_good
                DKScoreTypeLevels.MEDIUM -> R.string.dk_driverdata_mysynthesis_score_title_average
                DKScoreTypeLevels.NOT_GOOD -> R.string.dk_driverdata_mysynthesis_score_title_low
                DKScoreTypeLevels.BAD -> R.string.dk_driverdata_mysynthesis_score_title_bad
                DKScoreTypeLevels.VERY_BAD -> R.string.dk_driverdata_mysynthesis_score_title_very_bad
            }.let {
                getString(
                    it,
                    scoreLevel.getScoreLevels(dkScoreType).first.format(1),
                    scoreLevel.getScoreLevels(dkScoreType).second.format(1)
                )
            }
            this.text = DKSpannable().append(text, context.resSpans {
                color(DriveKitUI.colors.primaryColor())
                typeface(Typeface.NORMAL)
                size(R.dimen.dk_text_normal)
            }).space()
                .append(
                    getString(getScoreLevelDescription(dkScoreType, scoreLevel)),
                    context.resSpans {
                        color(DriveKitUI.colors.complementaryFontColor())
                        typeface(Typeface.NORMAL)
                        size(R.dimen.dk_text_small)
                    }).toSpannable()
        }
    }

    @StringRes
    private fun getScoreLevelDescription(dkScoreType: DKScoreType, scoreLevel: DKScoreTypeLevels) =
        when (scoreLevel) {
            DKScoreTypeLevels.EXCELLENT -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_excellent
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_excellent
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_excellent
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_excellent
            }
            DKScoreTypeLevels.VERY_GOOD -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_very_good
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_very_good
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_very_good
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_very_good
            }
            DKScoreTypeLevels.GREAT -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_good
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_good
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_good
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_good
            }
            DKScoreTypeLevels.MEDIUM -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_average
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_average
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_average
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_average
            }
            DKScoreTypeLevels.NOT_GOOD -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_low
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_low
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_low
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_low
            }
            DKScoreTypeLevels.BAD -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_bad
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_bad
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_bad
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_level_bad
            }
            DKScoreTypeLevels.VERY_BAD -> when (dkScoreType) {
                DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_level_very_bad
                DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_level_very_bad
                DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_level_very_bad
                DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_spedding_level_very_bad
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
            val tripListConfigurationType = intent.getSerializableCompat(
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
