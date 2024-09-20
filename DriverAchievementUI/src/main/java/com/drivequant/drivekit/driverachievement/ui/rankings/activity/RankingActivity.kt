package com.drivequant.drivekit.driverachievement.ui.rankings.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.rankings.fragment.RankingFragment

class RankingActivity : AppCompatActivity() {

    companion object {
        var rankingGroupName: String? = null
        fun launchActivity(context: Context,
                           groupName: String? = null) {
            val intent = Intent(context, RankingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            rankingGroupName = groupName
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_ranking)
        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, RankingFragment.newInstance(rankingGroupName))
            .commit()

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundDarkColor(window)
            addSystemStatusBarTopPadding(findViewById(R.id.toolbar))
            addSystemNavigationBarBottomMargin(findViewById(R.id.container))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle(getString(R.string.dk_achievements_ranking_menu_ranking))
    }
}
