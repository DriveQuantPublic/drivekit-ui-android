package com.drivequant.drivekit.ui.mysynthesis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.drivingconditions.DrivingConditionsActivity
import java.util.*

internal class MySynthesisActivity : AppCompatActivity() {

    companion object {
        const val DRIVING_CONDITIONS_REQUEST_CODE = 200

        fun launchActivity(context: Context) {
            val intent = Intent(context, MySynthesisActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    private var fragment: MySynthesisFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mysynthesis)
        setupUi()
    }

    private fun setupUi() {
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragment = MySynthesisFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
        this.fragment = fragment
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle(getString(R.string.dk_driverdata_mysynthesis_main_title))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DRIVING_CONDITIONS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedPeriod = data.getStringExtra(DrivingConditionsActivity.SELECTED_PERIOD_ID_EXTRA)
            val selectedDate = data.getLongExtra(DrivingConditionsActivity.SELECTED_DATE_ID_EXTRA, 0)
            if (selectedPeriod != null && selectedDate > 0) {
                val date = Date(selectedDate)
                fragment?.updateData(DKPeriod.valueOf(selectedPeriod), date)
            }
        }
    }
}
