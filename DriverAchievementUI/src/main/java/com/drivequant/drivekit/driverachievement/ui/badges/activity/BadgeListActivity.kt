package com.drivequant.drivekit.driverachievement.ui.badges.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.badges.fragment.BadgesListFragment

class BadgeListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_badges_list)

        setToolbar()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, BadgesListFragment())
            .commit()

        DKEdgeToEdgeManager.apply {
            addSystemStatusBarTopPadding(findViewById(R.id.toolbar))
            addSystemNavigationBarBottomMargin(findViewById(R.id.container))
        }
    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle(getString(R.string.menu_mybadges))
    }
}
