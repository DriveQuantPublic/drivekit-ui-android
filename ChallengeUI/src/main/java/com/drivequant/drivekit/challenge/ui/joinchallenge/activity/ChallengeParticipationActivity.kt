package com.drivequant.drivekit.challenge.ui.joinchallenge.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.joinchallenge.fragment.ChallengeParticipationFragment
import com.drivequant.drivekit.dbchallengeaccess.DbChallengeAccess

class ChallengeParticipationActivity : AppCompatActivity() {

    companion object {
        const val CHALLENGE_ID_EXTRA = "challenge-id-extra"

        fun launchActivity(
            activity: Activity,
            challengeId: String) {
            val intent = Intent(activity, ChallengeParticipationActivity::class.java)
            intent.putExtra(CHALLENGE_ID_EXTRA, challengeId)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_challenge_join)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val challengeId = intent.getStringExtra(CHALLENGE_ID_EXTRA) as String
        val challenge = DbChallengeAccess.findChallengeById(challengeId)?.apply {
            this@ChallengeParticipationActivity.title = this.title
        }
        challenge?.let {
            ChallengeParticipationFragment.newInstance(it.challengeId)
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, ChallengeParticipationFragment.newInstance(challengeId))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}