package com.drivequant.drivekit.timeline.ui.timelinedetail

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.timeline.ui.R

class TimelineDetailActivity : AppCompatActivity() {

    companion object {
        fun launchActivity(activity: Activity) {
            val intent = Intent(activity, TimelineDetailActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_detail)
        setupUI()
    }

    private fun setupUI() {
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, TimelineDetailFragment.newInstance())
            .commit()
    }

    private fun tagScreen() {
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                this,
                "dk_tag_timeline_score_detail"
            ), javaClass.simpleName
        )
    }

    override fun onResume() {
        super.onResume()
        tagScreen()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}