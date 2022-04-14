package com.drivekit.demoapp.dashboard.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        title = "MOCK" // TODO miss "Dashboard" string key

        initFeatureCard()
    }

    override fun onResume() {
        super.onResume()
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        }

        DriverDataUI.getLastTripsSynthesisCardsView(listener = object : SynthesisCardsViewListener {
            override fun onViewLoaded(fragment: DKSynthesisCardViewPagerFragment) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container_synthesis, fragment)
                    .commit()
            }
        })

        DriverDataUI.getLastTripsView(HeaderDay.DURATION).let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_last_trips, it)
                .commit()
        }
    }

    private fun initFeatureCard() {
        card_features.configureTitle(R.string.feature_list)
        card_features.configureDescription(R.string.feature_list_description)
        card_features.configureTextButton(R.string.button_see_features)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_action_bar_menu, menu)
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