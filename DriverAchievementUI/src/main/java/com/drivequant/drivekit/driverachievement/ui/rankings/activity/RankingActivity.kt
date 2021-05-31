package com.drivequant.drivekit.driverachievement.ui.rankings.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.driverachievement.ui.R
import com.drivequant.drivekit.driverachievement.ui.rankings.fragment.RankingFragment

class RankingActivity : AppCompatActivity() {

    companion object {
        const val GROUPNAME_EXTRA = "groupName-extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_ranking)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = DKResource.convertToString(this, "dk_achievements_ranking_menu_ranking")

        val fragment = RankingFragment()
        val bundle = Bundle()
        savedInstanceState?.getString(GROUPNAME_EXTRA)?.let {
            bundle.putString(RankingFragment.GROUPNAME_EXTRA, it)
            fragment.arguments = bundle
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}