package com.drivequant.drivekit.challenge.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.fragment.ChallengeFragment
import com.drivequant.drivekit.common.ui.utils.DKResource

class ChallengeListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_challenge_list)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = DKResource.convertToString(this, "dk_challenge_menu")
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, ChallengeFragment())
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}