package com.drivequant.drivekit.challenge.ui

import android.content.Context
import android.content.Intent
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailItem
import com.drivequant.drivekit.challenge.ui.challengelist.activity.ChallengeListActivity
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeParticipationActivity
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeParticipationActivity.Companion.CHALLENGE_ID_EXTRA

object ChallengeUI {

    internal val challengeDetailItems = listOf(
        ChallengeDetailItem.RESULTS,
        ChallengeDetailItem.RANKING,
        ChallengeDetailItem.TRIPS,
        ChallengeDetailItem.RULES)

    fun initialize() {}

    fun startChallengeActivity(context: Context) {
        val intent = Intent(context, ChallengeListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun startChallengeParticipationActivity(context: Context, challengeId: String) {
        val intent = Intent(context, ChallengeParticipationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(CHALLENGE_ID_EXTRA, challengeId)
        context.startActivity(intent)
    }
}