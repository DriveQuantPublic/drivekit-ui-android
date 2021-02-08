package com.drivequant.drivekit.ui.transportationmode.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.transportationmode.fragment.TransportationModeFragment

internal class TransportationModeActivity : AppCompatActivity() {

    private val FRAGMENT_TAG = "transportation-mode-fragment-tag"

    companion object {
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_transportation_mode_activity)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = DKResource.convertToString(this, "dk_driverdata_transportation_mode_title")

        val itinId = intent.getStringExtra(ITINID_EXTRA) as String
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                TransportationModeFragment.newInstance(itinId),
                FRAGMENT_TAG
            )
            .commit()
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

    fun onValidateClicked(view: View) {
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
}