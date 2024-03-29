package com.drivequant.drivekit.common.ui.navigation

import android.content.Context

interface ChallengeUIEntryPoint {
    fun startChallengeActivity(context: Context)
    fun startChallengeParticipationActivity(context: Context, challengeId: String)
}
