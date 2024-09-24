package com.drivequant.drivekit.ui.drivingconditions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.ui.R
import java.util.*

internal class DrivingConditionsActivity : AppCompatActivity() {

    companion object {
        const val SELECTED_PERIOD_ID_EXTRA = "selectedPeriod"
        const val SELECTED_DATE_ID_EXTRA = "selectedDate"

        fun launchActivity(context: Context) {
            val intent = Intent(context, DrivingConditionsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        fun launchActivity(
            activity: Activity,
            selectedPeriod: DKPeriod?,
            selectedDate: Date?,
            requestCode: Int?
        ) {
            val intent = Intent(activity, DrivingConditionsActivity::class.java)
            intent.putExtra(SELECTED_PERIOD_ID_EXTRA, selectedPeriod?.name)
            intent.putExtra(SELECTED_DATE_ID_EXTRA, selectedDate?.time)
            if (requestCode != null && requestCode > 0) {
                activity.startActivityForResult(intent, requestCode)
            } else {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                activity.startActivity(intent)
            }
        }
    }

    private lateinit var fragment: DrivingConditionsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mysynthesis)
        setupUi()
        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            addInsetsPaddings(findViewById(R.id.toolbar))
            addInsetsMargins(findViewById(R.id.container))
        }
    }

    private fun setupUi() {
        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val selectedPeriod = intent.getStringExtra(SELECTED_PERIOD_ID_EXTRA)?.let { DKPeriod.valueOf(it) }
        val selectedDate = intent.getLongExtra(SELECTED_DATE_ID_EXTRA, 0L).let { if (it > 0L) Date(it) else null }

        this.fragment = DrivingConditionsFragment.newInstance(selectedPeriod, selectedDate)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, this.fragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle(getString(R.string.dk_driverdata_drivingconditions_title))
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
        intent.putExtra(SELECTED_PERIOD_ID_EXTRA, fragment.selectedPeriod.name)
        intent.putExtra(SELECTED_DATE_ID_EXTRA, fragment.selectedDate?.time)
        setResult(RESULT_OK, intent)
        finish()
    }
}
