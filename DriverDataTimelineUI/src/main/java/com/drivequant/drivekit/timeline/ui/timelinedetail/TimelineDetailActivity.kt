package com.drivequant.drivekit.timeline.ui.timelinedetail

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.component.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.Timeline
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.timeline.ui.R
import com.google.gson.Gson
import java.util.*

class TimelineDetailActivity : AppCompatActivity() {

    companion object {

        const val SELECTED_SCORE_ID_EXTRA = "selectedScore"
        const val SELECTED_PERIOD_ID_EXTRA = "selectedPeriod"
        const val SELECTED_DATE_ID_EXTRA = "selectedDate"
        const val WEEK_TIMELINE_ID_EXTRA = "weekTimeline"
        const val MONTH_TIMELINE_ID_EXTRA = "monthTimeline"

        fun launchActivity(
            activity: Activity,
            selectedScore: DKScoreType,
            selectedPeriod: DKTimelinePeriod,
            selectedDate: Date?,
            weekTimeline: Timeline?,
            monthTimeline: Timeline?
        ) {
            val intent = Intent(activity, TimelineDetailActivity::class.java)
            intent.putExtra(SELECTED_SCORE_ID_EXTRA, selectedScore.name)
            intent.putExtra(SELECTED_PERIOD_ID_EXTRA, selectedPeriod.name)
            intent.putExtra(SELECTED_DATE_ID_EXTRA, selectedDate?.time)
            intent.putExtra(WEEK_TIMELINE_ID_EXTRA, Gson().toJson(weekTimeline))
            intent.putExtra(MONTH_TIMELINE_ID_EXTRA, Gson().toJson(monthTimeline))
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

        val selectedScore = intent.getStringExtra(SELECTED_SCORE_ID_EXTRA)
        val selectedPeriod = intent.getStringExtra(SELECTED_PERIOD_ID_EXTRA)
        val selectedDate = intent.getLongExtra(SELECTED_DATE_ID_EXTRA, 0L)
        val weekTimeline = intent.getStringExtra(WEEK_TIMELINE_ID_EXTRA)
        val monthTimeline = intent.getStringExtra(MONTH_TIMELINE_ID_EXTRA)

        if (selectedScore != null
            && selectedPeriod != null
            && selectedDate > 0
            && weekTimeline != null
            && monthTimeline != null
        ) {
            val computedDate = Date()
            computedDate.time = selectedDate
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.container, TimelineDetailFragment.newInstance(
                        DKScoreType.valueOf(selectedScore),
                        DKTimelinePeriod.valueOf(selectedPeriod),
                        computedDate,
                        Gson().fromJson(weekTimeline, Timeline::class.java),
                        Gson().fromJson(monthTimeline, Timeline::class.java),
                    )
                )
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}