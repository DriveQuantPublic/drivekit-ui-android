package com.drivequant.drivekit.challenge.ui.challengedetail.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.challengedetail.fragment.ChallengeDetailFragment
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailViewModel
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeParticipationActivity
import com.drivequant.drivekit.common.ui.component.PseudoChangeListener
import com.drivequant.drivekit.common.ui.component.PseudoCheckListener
import com.drivequant.drivekit.common.ui.component.PseudoUtils
import com.drivequant.drivekit.common.ui.utils.DKResource

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
    }

    override fun onResume() {
        super.onResume()
        PseudoUtils.checkPseudo(object : PseudoCheckListener {
            override fun onPseudoChecked(hasPseudo: Boolean) {
                if (hasPseudo) {
                    showFragment()
                } else {
                    PseudoUtils.show(this@ChallengeDetailActivity, object : PseudoChangeListener {
                        override fun onPseudoChanged(success: Boolean) {
                            if (!success) {
                                Toast.makeText(applicationContext, DKResource.convertToString(applicationContext, "dk_common_error_message"), Toast.LENGTH_LONG).show()
                            }
                            showFragment()
                        }
                        override fun onCancelled() {
                            showFragment()
                        }
                    })
                }
            }
        })
    }

    private fun showFragment(){
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