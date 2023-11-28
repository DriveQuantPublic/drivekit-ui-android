package com.drivequant.drivekit.challenge.ui.joinchallenge.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.databinding.DkActivityChallengeRulesBinding
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeParticipationActivity.Companion.CHALLENGE_ID_EXTRA
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource

class ChallengeRulesActivity : AppCompatActivity() {

    private lateinit var challengeId: String
    private lateinit var viewModel: ChallengeParticipationViewModel
    private lateinit var binding: DkActivityChallengeRulesBinding

    companion object {
        const val CONSULT_RULES_EXTRA = "consult-rules-extra"
        const val UPDATE_CHALLENGE_REQUEST_CODE = 108

        fun launchActivity(
            activity: Activity,
            challengeId: String,
            isRegistered: Boolean
        ) {
            val intent = Intent(activity, ChallengeRulesActivity::class.java)
            intent.putExtra(CONSULT_RULES_EXTRA, isRegistered)
            intent.putExtra(CHALLENGE_ID_EXTRA, challengeId)
            activity.startActivityForResult(intent, UPDATE_CHALLENGE_REQUEST_CODE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::challengeId.isInitialized) {
            outState.putString("challengeId", challengeId)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DkActivityChallengeRulesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                this,
                "dk_tag_challenge_rules"
            ), javaClass.simpleName
        )

        setSupportActionBar(binding.root.findViewById(R.id.dk_toolbar))
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val isRegistered = intent.getBooleanExtra(CONSULT_RULES_EXTRA, false)
        intent.getStringExtra(CHALLENGE_ID_EXTRA)?.apply {
            challengeId = this
        } ?: run {
            finish()
            return
        }

        val acceptRulesText = if (isRegistered) {
            "dk_challenge_participate_button"
        } else {
            binding.textViewAcceptRule.visibility = View.VISIBLE
            "dk_challenge_optin_title"
        }

        binding.textViewAcceptRule.apply {
            text = DKResource.convertToString(this@ChallengeRulesActivity, acceptRulesText)
            headLine1(DriveKitUI.colors.fontColorOnSecondaryColor())
        }
        (savedInstanceState?.getString("challengeId"))?.let {
            challengeId = it
        }

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(
                this,
                ChallengeParticipationViewModel.ChallengeParticipationViewModelFactory(challengeId)
            )[ChallengeParticipationViewModel::class.java]
        }

        viewModel.syncJoinChallengeError.observe(this) {
            if (it) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(
                    this,
                    DKResource.convertToString(
                        this,
                        "dk_challenge_failed_to_join"
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
            updateProgressVisibility(false)
        }

        viewModel.challenge?.rules?.let {
            if (it.isNotEmpty()) {
                binding.textViewChallengeRule.text =
                    HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }

        binding.textViewAcceptRule.setOnClickListener {
            if (!isRegistered) {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(this)
                    .layout(R.layout.template_alert_dialog_layout)
                    .positiveButton(positiveListener = { dialog, _ ->
                        updateProgressVisibility(true)
                        viewModel.joinChallenge(challengeId)
                        dialog.dismiss()
                    })
                    .negativeButton(negativeListener = { _,_ ->
                        finish()
                    })
                    .show()

                val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                titleTextView?.text =
                    DKResource.convertToString(this, "dk_challenge_participate_button")
                viewModel.challenge?.optinText?.let {
                    descriptionTextView?.text = it
                }
                titleTextView?.headLine1()
                descriptionTextView?.normalText()
            }
        }
        binding.textViewAcceptRule.setBackgroundColor(DriveKitUI.colors.secondaryColor())
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        binding.progressCircular.visibility = if (displayProgress) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        setActivityTitle(DKResource.convertToString(this, "dk_challenge_rule_title"))
    }
}
