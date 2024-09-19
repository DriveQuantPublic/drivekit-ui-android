package com.drivequant.drivekit.challenge.ui.challengedetail.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.fragment.ChallengeDetailFragment
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeParticipationActivity
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager

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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_challenge_detail)

        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        DKEdgeToEdgeManager.apply {
            addSystemStatusBarTopPadding(findViewById(R.id.toolbar))
            addSystemNavigationBarBottomMargin(findViewById(R.id.container))
        }
    }

    override fun onResume() {
        super.onResume()
        showFragment()
    }

    private fun showFragment() {
        val challengeId = intent.getStringExtra(ChallengeParticipationActivity.CHALLENGE_ID_EXTRA)
        if (challengeId == null) {
            finish()
            return
        }
        val viewModel = ViewModelProvider(this, ChallengeDetailViewModel.ChallengeDetailViewModelFactory(challengeId))[ChallengeDetailViewModel::class.java]

        setActivityTitle(viewModel.challenge.title)

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
