package com.drivequant.drivekit.timeline.ui.timeline

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.timeline.ui.R
import java.util.*

internal class TimelineActivity : AppCompatActivity() {

    private lateinit var fragment: TimelineFragment

    companion object {
        const val TIMELINE_DETAIL_REQUEST_CODE = 100

        fun launchActivity(context: Context) {
            val intent = Intent(context, TimelineActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        setupUi()
    }

    private fun setupUi() {
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fragment = TimelineFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle(getString(R.string.dk_timeline_title))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TIMELINE_DETAIL_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedPeriod = data.getStringExtra("selectedPeriod")
            val selectedDate = data.getLongExtra("selectedDate", 0)
            if (selectedPeriod != null && selectedDate > 0) {
                val date = Date()
                date.time = selectedDate
                fragment.updateDataFromDetailScreen(DKTimelinePeriod.valueOf(selectedPeriod), date)
            }
        }
    }
}