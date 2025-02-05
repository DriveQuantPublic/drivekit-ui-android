package com.drivequant.drivekit.timeline.ui.timeline

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        setupUi()

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            update(findViewById(R.id.root)) { view, insets ->
                addSystemStatusBarTopPadding(findViewById(R.id.toolbar), insets)
                addSystemNavigationBarBottomPadding(view, insets)
            }
        }
    }

    private fun setupUi() {
        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TIMELINE_DETAIL_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedPeriod = data.getStringExtra("selectedPeriod")
            val selectedDate = data.getLongExtra("selectedDate", 0)
            if (selectedPeriod != null && selectedDate > 0) {
                val date = Date()
                date.time = selectedDate
                fragment.updateDataFromDetailScreen(DKPeriod.valueOf(selectedPeriod), date)
            }
        }
    }
}
