package com.drivequant.drivekit.vehicle.ui.vehicles.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.vehicles.fragment.VehiclesListFragment
import kotlinx.android.synthetic.main.activity_vehicle_detail.*

class VehiclesListActivity : AppCompatActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicles_list)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, VehiclesListFragment())
            .commit()
    }

    fun updateTitle(title: String){
        Log.e("TITLE", "title value is : $title")
        this.title = title
        supportActionBar?.title = "$title + from SupportActionBar"
        toolbar?.title = "$title + from Toolbar"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}