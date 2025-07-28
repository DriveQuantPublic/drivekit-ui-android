package com.drivequant.drivekit.ui.driverpassengermode.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.driverpassengermode.fragment.DriverPassengerModeFragment

internal class DriverPassengerModeActivity : AppCompatActivity() {

    companion object {
        private const val FRAGMENT_TAG = "driver-passenger-mode-fragment-tag"
        private const val ITINID_EXTRA = "itinId-extra"
        const val UPDATE_DRIVER_PASSENGER_MODE = 105

        fun launchActivity(
            activity: Activity,
            itinId: String
        ) {
            val intent = Intent(activity, DriverPassengerModeActivity::class.java)
            intent.putExtra(ITINID_EXTRA, itinId)
            activity.startActivityForResult(intent, UPDATE_DRIVER_PASSENGER_MODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_driver_passenger_mode_activity)

        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val itinId = intent.getStringExtra(ITINID_EXTRA) as String
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                DriverPassengerModeFragment.newInstance(itinId),
                FRAGMENT_TAG
            )
            .commit()

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(findViewById(R.id.root)) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle(getString(R.string.dk_driverdata_ocupant_declaration_title))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun onTransportationModeItemClicked(view: View) {
        getFragment()?.onTransportationModeClicked(view)
    }

    fun onCarOccupantRoleItemClicked(view: View) {
        getFragment()?.onCarOccupantRoleClicked(view)
    }

    fun onChangeButtonClicked(@Suppress("UNUSED_PARAMETER")  view: View) {
        getFragment()?.onChangeButtonClicked()
    }

    private fun getFragment(): DriverPassengerModeFragment? {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        return if (fragment != null && fragment is DriverPassengerModeFragment){
            fragment
        } else {
            null
        }
    }
}