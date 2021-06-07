package com.drivequant.drivekit.challenge.ui.common

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeRulesActivity
import com.drivequant.drivekit.challenge.ui.joinchallenge.viewmodel.ChallengeParticipationViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKResource
import kotlinx.android.synthetic.main.dk_challenge_header_view.view.*

class ChallengeHeaderView(context: Context) : LinearLayout(context) {

    init {
        val view = View.inflate(context, R.layout.dk_challenge_header_view, null)
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        setStyle()
    }

    fun configure(viewModel: ChallengeParticipationViewModel, activity: Activity) {
        viewModel.challenge?.let { challenge ->
            if (!challenge.rules.isNullOrBlank()) {
                text_view_challenge_rule_consult.apply {
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
                text_view_conditions.text = challenge.conditionsDescription
                text_view_conditions.visibility = View.VISIBLE
            }

            text_view_rules.text = challenge.description
            text_view_date.text = viewModel.getDateRange()
            text_view_title.text = challenge.title
        }
    }

    fun displayRulesText(visibility: Boolean = true) {
        text_view_rules.visibility = if (visibility) View.VISIBLE else GONE
    }

    fun displayConsultRulesText(visibility: Boolean = true) {
        text_view_challenge_rule_consult.visibility = if (visibility) View.VISIBLE else GONE
    }

    private fun setStyle() {
        text_view_title.setTextColor(DriveKitUI.colors.mainFontColor())
        text_view_date.smallText(Color.parseColor("#9E9E9E"))
        view_separator.setBackgroundColor(DriveKitUI.colors.neutralColor())
        text_view_conditions.normalText()
        text_view_rules.normalText()
        text_view_challenge_rule_consult.normalText(DriveKitUI.colors.complementaryFontColor())
    }
}