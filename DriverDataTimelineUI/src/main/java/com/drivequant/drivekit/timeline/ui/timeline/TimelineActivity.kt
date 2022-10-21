package com.drivequant.drivekit.timeline.ui.timeline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.timeline.ui.R

class TimelineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        tagScreen()
        setupUi()
    }

    private fun setupUi() {
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, TimelineFragment.newInstance())
            .commit()
    }

    private fun tagScreen() {
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                this,
                "dk_tag_timeline"
            ), javaClass.simpleName
        )
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle("Timeline") //TODO(replace with timeline key string)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}