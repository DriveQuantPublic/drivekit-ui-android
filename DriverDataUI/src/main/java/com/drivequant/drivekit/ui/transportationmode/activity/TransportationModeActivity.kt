package com.drivequant.drivekit.ui.transportationmode.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.transportationmode.fragment.TransportationModeFragment

internal class TransportationModeActivity : AppCompatActivity() {

    companion object {
        private const val FRAGMENT_TAG = "transportation-mode-fragment-tag"
        private const val ITINID_EXTRA = "itinId-extra"
        const val UPDATE_TRIP_TRANSPORTATION_MODE = 104

        fun launchActivity(
            activity: Activity,
            itinId: String
        ) {
            val intent = Intent(activity, TransportationModeActivity::class.java)
            intent.putExtra(ITINID_EXTRA, itinId)
            activity.startActivityForResult(intent, UPDATE_TRIP_TRANSPORTATION_MODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_transportation_mode_activity)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val itinId = intent.getStringExtra(ITINID_EXTRA) as String
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                TransportationModeFragment.newInstance(itinId),
                FRAGMENT_TAG
            )
            .commit()

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            addSystemStatusBarTopPadding(findViewById(R.id.toolbar))
            addSystemNavigationBarBottomMargin(findViewById(R.id.container))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun onTransportationModeItemClicked(view: View) {
        getFragment()?.onTransportationModeClicked(view)
    }

    fun onTransportationProfileItemClicked(view: View) {
        getFragment()?.onTransportationProfileClicked(view)
    }

    fun onValidateClicked(@Suppress("UNUSED_PARAMETER")  view: View) {
        getFragment()?.onValidate()
    }

    private fun getFragment(): TransportationModeFragment? {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        return if (fragment != null && fragment is TransportationModeFragment){
            fragment
        } else {
            null
        }
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle(getString(R.string.dk_driverdata_transportation_mode_title))
    }
}
