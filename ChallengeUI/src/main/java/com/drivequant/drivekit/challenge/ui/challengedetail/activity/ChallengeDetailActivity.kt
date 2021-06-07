package com.drivequant.drivekit.challenge.ui.challengedetail.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.fragment.ChallengeDetailFragment
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeParticipationActivity

class ChallengeDetailActivity : AppCompatActivity() {

    companion object {
       private const val CHALLENGE_ID_EXTRA = "challenge-id-extra"

        fun launchActivity(
            activity: Activity,
            challengeId: String) {
            val intent = Intent(activity, ChallengeDetailActivity::class.java)
            intent.putExtra(CHALLENGE_ID_EXTRA, challengeId)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_challenge_detail)

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val challengeId =
            intent.getStringExtra(ChallengeParticipationActivity.CHALLENGE_ID_EXTRA) as String
        val viewModel = ViewModelProviders.of(
            this,
            ChallengeDetailViewModel.ChallengeDetailViewModelFactory(challengeId)
        ).get(ChallengeDetailViewModel::class.java)

        title = viewModel.challenge.title

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, ChallengeDetailFragment.newInstance(challengeId))
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}