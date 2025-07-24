package com.drivequant.drivekit.ui.passenger.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.ui.R

internal class PassengerActivity : AppCompatActivity() {

    companion object {
        private const val FRAGMENT_TAG = "passenger-fragment-tag"
        private const val ITINID_EXTRA = "itinId-extra"
        const val UPDATE_PASSENGER_MODE = 105

        fun launchActivity(
            activity: Activity,
            itinId: String
        ) {
            val intent = Intent(activity, PassengerActivity::class.java)
            intent.putExtra(ITINID_EXTRA, itinId)
            activity.startActivityForResult(intent, UPDATE_PASSENGER_MODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_passenger_activity)

        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val itinId = intent.getStringExtra(ITINID_EXTRA) as String
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                PassengerFragment.newInstance(itinId),
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // TODO the rest
}