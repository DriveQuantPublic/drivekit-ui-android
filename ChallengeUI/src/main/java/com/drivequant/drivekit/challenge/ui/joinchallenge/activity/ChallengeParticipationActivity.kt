package com.drivequant.drivekit.challenge.ui.joinchallenge.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.joinchallenge.fragment.ChallengeParticipationFragment
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationViewModel
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKEdgeToEdgeManager

class ChallengeParticipationActivity : AppCompatActivity() {

    private lateinit var viewModel: ChallengeParticipationViewModel
    private lateinit var fragment: ChallengeParticipationFragment

    companion object {
        const val CHALLENGE_ID_EXTRA = "challenge-id-extra"

        fun launchActivity(
            activity: Activity,
            challengeId: String
        ) {
            val intent = Intent(activity, ChallengeParticipationActivity::class.java)
            intent.putExtra(CHALLENGE_ID_EXTRA, challengeId)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_challenge_join)

        val toolbar = findViewById<Toolbar>(com.drivequant.drivekit.common.ui.R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val challengeId = intent.getStringExtra(CHALLENGE_ID_EXTRA)
        if (challengeId == null) {
            finish()
            return
        }

        viewModel = ViewModelProvider(this,
            ChallengeParticipationViewModel.ChallengeParticipationViewModelFactory(challengeId)
        )[ChallengeParticipationViewModel::class.java]

        fragment = ChallengeParticipationFragment.newInstance(challengeId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()

        DKEdgeToEdgeManager.apply {
            setSystemStatusBarForegroundColor(window)
            addInsetsPaddings(findViewById(R.id.toolbar))
            addInsetsMargins(findViewById(R.id.container))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == ChallengeRulesActivity.UPDATE_CHALLENGE_REQUEST_CODE) {
            fragment.dispatch()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.challenge?.let {
            setActivityTitle(it.title)
        }
    }
}
