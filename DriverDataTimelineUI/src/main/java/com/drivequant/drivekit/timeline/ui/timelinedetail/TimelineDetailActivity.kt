package com.drivequant.drivekit.timeline.ui.timelinedetail

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.databaseutils.entity.DKRawTimeline
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.timeline.TimelineActivity
import com.google.gson.Gson
import java.util.*

internal class TimelineDetailActivity : AppCompatActivity() {

    private lateinit var fragment: TimelineDetailFragment

    companion object {

        const val SELECTED_SCORE_ID_EXTRA = "selectedScore"
        const val SELECTED_PERIOD_ID_EXTRA = "selectedPeriod"
        const val SELECTED_DATE_ID_EXTRA = "selectedDate"
        const val WEEK_TIMELINE_ID_EXTRA = "weekTimeline"
        const val MONTH_TIMELINE_ID_EXTRA = "monthTimeline"

        fun launchActivity(
            activity: Activity,
            selectedScore: DKScoreType,
            selectedPeriod: DKPeriod,
            selectedDate: Date?,
            weekTimeline: DKRawTimeline?,
            monthTimeline: DKRawTimeline?
        ) {
            val intent = Intent(activity, TimelineDetailActivity::class.java)
            intent.putExtra(SELECTED_SCORE_ID_EXTRA, selectedScore.name)
            intent.putExtra(SELECTED_PERIOD_ID_EXTRA, selectedPeriod.name)
            intent.putExtra(SELECTED_DATE_ID_EXTRA, selectedDate?.time)
            intent.putExtra(WEEK_TIMELINE_ID_EXTRA, Gson().toJson(weekTimeline))
            intent.putExtra(MONTH_TIMELINE_ID_EXTRA, Gson().toJson(monthTimeline))
            activity.startActivityForResult(intent, TimelineActivity.TIMELINE_DETAIL_REQUEST_CODE)
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
            fragment = TimelineDetailFragment.newInstance(
                DKScoreType.valueOf(selectedScore),
                DKPeriod.valueOf(selectedPeriod),
                computedDate,
                Gson().fromJson(weekTimeline, DKRawTimeline::class.java),
                Gson().fromJson(monthTimeline, DKRawTimeline::class.java)
            )
            supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finishActivity()
        return true
    }

    override fun onBackPressed() {
        finishActivity()
        super.onBackPressed()
    }

    private fun finishActivity() {
        val intent = Intent()
        intent.putExtra("selectedPeriod", fragment.viewModel.selectedPeriod.name)
        intent.putExtra("selectedDate", fragment.viewModel.selectedDate.time)
        setResult(RESULT_OK, intent)
        finish()
    }
}
