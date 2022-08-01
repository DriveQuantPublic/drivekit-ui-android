package com.drivequant.drivekit.driverachievement.ui.rankings.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKResource
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_ranking)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, RankingFragment.newInstance(rankingGroupName))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle(DKResource.convertToString(this, "dk_achievements_ranking_menu_ranking"))
    }
}