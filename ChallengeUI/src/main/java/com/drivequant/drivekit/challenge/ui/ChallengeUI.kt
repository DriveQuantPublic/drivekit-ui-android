package com.drivequant.drivekit.challenge.ui

import android.content.Context
import android.content.Intent
import com.drivequant.drivekit.challenge.ui.challengelist.activity.ChallengeListActivity
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeParticipationActivity

object ChallengeUI {

    fun startChallengeActivity(context: Context) {
        val intent = Intent(context, ChallengeListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun startChallengeParticipationActivity(context: Context) {
        val intent = Intent(context, ChallengeParticipationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}