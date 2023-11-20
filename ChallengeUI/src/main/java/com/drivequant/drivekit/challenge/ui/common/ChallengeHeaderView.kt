package com.drivequant.drivekit.challenge.ui.common

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeRulesActivity
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKResource

class ChallengeHeaderView(context: Context) : LinearLayout(context) {

    private val challengeRuleConsultTextView: TextView
    private val conditionsTextView: TextView
    private val rulesTextView: TextView
    private val dateTextView: TextView
    private val titleTextView: TextView
    private val separatorView: View

    init {
        val view = View.inflate(context, R.layout.dk_challenge_header_view, null)
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        this.challengeRuleConsultTextView = view.findViewById(R.id.text_view_challenge_rule_consult)
        this.conditionsTextView = view.findViewById(R.id.text_view_conditions)
        this.rulesTextView = view.findViewById(R.id.text_view_rules)
        this.dateTextView = view.findViewById(R.id.text_view_date)
        this.titleTextView = view.findViewById(R.id.text_view_title)
        this.separatorView = view.findViewById(R.id.view_separator)
        setStyle()
    }

    fun configure(viewModel: ChallengeParticipationViewModel, activity: Activity) {
        viewModel.challenge?.let { challenge ->
            if (!challenge.rules.isNullOrBlank()) {
                challengeRuleConsultTextView.apply {
                    text = DKResource.convertToString(
                        context,
                        "dk_challenge_consult_rule_button"
                    )
                    setTextColor(DriveKitUI.colors.secondaryColor())
                    visibility = View.VISIBLE
                    setOnClickListener {
                        ChallengeRulesActivity.launchActivity(
                            activity,
                            challenge.challengeId,
                            viewModel.isDriverRegistered()
                        )
                    }
                }
            }

            if (!challenge.conditionsDescription.isNullOrBlank()) {
                conditionsTextView.text = challenge.conditionsDescription
                conditionsTextView.visibility = View.VISIBLE
            }

            rulesTextView.text = challenge.description
            dateTextView.text = viewModel.getDateRange()
            titleTextView.text = challenge.title
        }
    }

    fun displayRulesText(visibility: Boolean = true) {
        rulesTextView.visibility = if (visibility) View.VISIBLE else GONE
    }

    fun displayConsultRulesText(visibility: Boolean = true) {
        challengeRuleConsultTextView.visibility = if (visibility) View.VISIBLE else GONE
    }

    fun displayConditionsDescriptionText(visibility: Boolean = true) {
        conditionsTextView.visibility = if (visibility) View.VISIBLE else GONE
    }

    private fun setStyle() {
        titleTextView.apply {
            setTextColor(DriveKitUI.colors.mainFontColor())
            typeface = DriveKitUI.primaryFont(context)
        }
        dateTextView.smallText(DriveKitUI.colors.complementaryFontColor())
        separatorView.setBackgroundColor(DriveKitUI.colors.neutralColor())
        conditionsTextView.normalText()
        rulesTextView.normalText()
        challengeRuleConsultTextView.normalText(DriveKitUI.colors.complementaryFontColor())
    }
}
