package com.drivequant.drivekit.vehicle.ui.vehicles.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.fragment.VehiclesListFragment

class VehiclesListActivity : AppCompatActivity() {

    companion object {
        fun launchActivity(context: Context) {
            val intent = Intent(context, VehiclesListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicles_list)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val fragment = VehiclesListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    fun updateTitle(title: String) {
        setActivityTitle(title)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
