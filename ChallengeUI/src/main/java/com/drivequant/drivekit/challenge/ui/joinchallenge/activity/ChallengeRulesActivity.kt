package com.drivequant.drivekit.challenge.ui.joinchallenge.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import  androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeParticipationActivity.Companion.CHALLENGE_ID_EXTRA
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_activity_challenge_rules.*
import kotlinx.android.synthetic.main.dk_activity_challenge_rules.progress_circular

class ChallengeRulesActivity : AppCompatActivity() {


    private lateinit var challengeId: String
    private lateinit var viewModel: ChallengeParticipationViewModel

    companion object {
        const val CONSULT_RULES_EXTRA = "consult-rules-extra"

        fun launchActivity(
            activity: Activity,
            challengeId: String,
            isRegistered: Boolean) {
            val intent = Intent(activity, ChallengeRulesActivity::class.java)
            intent.putExtra(CONSULT_RULES_EXTRA, isRegistered)
            intent.putExtra(CHALLENGE_ID_EXTRA, challengeId)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dk_activity_challenge_rules)
        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                this,
                "dk_tag_challenge_rules"
            ), javaClass.simpleName
        )

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val isRegistered = intent.getBooleanExtra(CONSULT_RULES_EXTRA, false)
        challengeId = intent.getStringExtra(CHALLENGE_ID_EXTRA) as String

        val value = if (isRegistered) {
            text_view_accept_rule.text = DKResource.convertToString(this, "dk_challenge_participate_button")
            "dk_challenge_rule_title"
        } else {
            text_view_accept_rule.visibility = View.VISIBLE
            text_view_accept_rule.text = DKResource.convertToString(this, "dk_challenge_optin_title")
           "dk_challenge_optin_title"
        }
        title = DKResource.convertToString(this, value)
        (savedInstanceState?.getString("challengeId"))?.let { it ->
            challengeId = it
        }

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(
                this,
                ChallengeParticipationViewModel.ChallengeParticipationViewModelFactory(challengeId)
            ).get(ChallengeParticipationViewModel::class.java)
        }

        viewModel.syncJoinChallengeError.observe(this, Observer {
            if (it) {
                finish()
            } else {
                //TODO show popup sync error
            }
            updateProgressVisibility(false)
        })

        text_view_accept_rule.setOnClickListener {
            if (!isRegistered) {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(this)
                    .layout(R.layout.template_alert_dialog_layout)
                    .positiveButton(DKResource.convertToString(this, "dk_common_ok"),
                        DialogInterface.OnClickListener { dialog, _ ->
                            updateProgressVisibility(true)
                            viewModel.joinChallenge(challengeId)
                            dialog.dismiss()
                        })
                    .negativeButton(DKResource.convertToString(this, "dk_common_cancel"),
                        DialogInterface.OnClickListener { dialog, _ ->
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

        viewModel.getRules()?.let {
            if (it.isNotEmpty()) {
                text_view_challenge_rule.text =
                    HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }
        setStyle()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::challengeId.isInitialized) {
            outState.putString("challengeId", challengeId)
        }
        super.onSaveInstanceState(outState)
    }

    private fun setStyle() {
        text_view_accept_rule.setBackgroundColor(DriveKitUI.colors.secondaryColor())
    }

    private fun updateProgressVisibility(displayProgress: Boolean) {
        if (displayProgress) {
            progress_circular.visibility = View.VISIBLE
        } else {
            progress_circular.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}