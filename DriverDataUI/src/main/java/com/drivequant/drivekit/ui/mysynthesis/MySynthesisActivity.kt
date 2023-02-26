package com.drivequant.drivekit.ui.mysynthesis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.ui.R

internal class MySynthesisActivity : AppCompatActivity() {

    private var fragment: MySynthesisFragment? = null

    companion object {
        fun launchActivity(context: Context) {
            val intent = Intent(context, MySynthesisActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

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
}
