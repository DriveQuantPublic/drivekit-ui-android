package com.drivequant.drivekit.timeline.ui.timelinedetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.timeline.TimelineActivity
import java.util.*

internal class TimelineDetailActivity : AppCompatActivity() {

    private lateinit var fragment: TimelineDetailFragment

    companion object {
        const val SELECTED_SCORE_ID_EXTRA = "selectedScore"
        const val SELECTED_PERIOD_ID_EXTRA = "selectedPeriod"
        const val SELECTED_DATE_ID_EXTRA = "selectedDate"

        fun launchActivity(
            activity: Activity,
            selectedScore: DKScoreType,
            selectedPeriod: DKPeriod,
            selectedDate: Date?
        ) {
            val intent = Intent(activity, TimelineDetailActivity::class.java)
            intent.putExtra(SELECTED_SCORE_ID_EXTRA, selectedScore.name)
            intent.putExtra(SELECTED_PERIOD_ID_EXTRA, selectedPeriod.name)
            intent.putExtra(SELECTED_DATE_ID_EXTRA, selectedDate?.time)
            activity.startActivityForResult(intent, TimelineActivity.TIMELINE_DETAIL_REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_detail)
        setupUI()
        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(findViewById(R.id.root)) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
        }
    }

    private fun setupUI() {
        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val selectedScore = intent.getStringExtra(SELECTED_SCORE_ID_EXTRA)
        val selectedPeriod = intent.getStringExtra(SELECTED_PERIOD_ID_EXTRA)
        val selectedDate = intent.getLongExtra(SELECTED_DATE_ID_EXTRA, 0L)

        if (selectedScore != null
            && selectedPeriod != null
            && selectedDate > 0
        ) {
            val computedDate = Date()
            computedDate.time = selectedDate
            fragment = TimelineDetailFragment.newInstance(
                DKScoreType.valueOf(selectedScore),
                DKPeriod.valueOf(selectedPeriod),
                computedDate
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

    @Suppress("OverrideDeprecatedMigration")
    override fun onBackPressed() {
        finishActivity()
        super.onBackPressed()
    }

    private fun finishActivity() {
        val intent = Intent()
        intent.putExtra(SELECTED_PERIOD_ID_EXTRA, fragment.viewModel.selectedPeriod.name)
        intent.putExtra(SELECTED_DATE_ID_EXTRA, fragment.viewModel.selectedDate.time)
        setResult(RESULT_OK, intent)
        finish()
    }
}
