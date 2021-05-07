package com.drivequant.drivekit.challenge.ui

import android.content.Context
import android.content.Intent
import com.drivequant.drivekit.challenge.ui.activity.ChallengeListActivity

object ChallengeUI {

    fun startChallengeActivity(context: Context) {
        val intent = Intent(context, ChallengeListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}