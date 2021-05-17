package com.drivequant.drivekit.challenge.ui.joinchallenge.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.joinchallenge.fragment.ChallengeJoinFragment
import com.drivequant.drivekit.dbchallengeaccess.DbChallengeAccess

class ChallengeJoinActivity : AppCompatActivity() {

    companion object {
        const val CHALLENGE_ID_EXTRA = "challenge-id-extra"

        fun launchActivity(
            activity: Activity,
            challengeId: String) {
            val intent = Intent(activity, ChallengeJoinActivity::class.java)
            intent.putExtra(CHALLENGE_ID_EXTRA, challengeId)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_challenge_join)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val challengeId = intent.getStringExtra(CHALLENGE_ID_EXTRA) as String
        val challenge = DbChallengeAccess.findChallengeById(challengeId)?.apply {
            this@ChallengeJoinActivity.title = this.title
        }
        challenge?.let {
            ChallengeJoinFragment.newInstance(it.challengeId)
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, ChallengeJoinFragment.newInstance(challengeId))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}