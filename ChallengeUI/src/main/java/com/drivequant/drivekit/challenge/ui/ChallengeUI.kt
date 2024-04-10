package com.drivequant.drivekit.challenge.ui

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel.ChallengeDetailItem
import com.drivequant.drivekit.challenge.ui.challengelist.activity.ChallengeListActivity
import com.drivequant.drivekit.challenge.ui.challengelist.fragment.ChallengeListFragment
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeParticipationActivity
import com.drivequant.drivekit.challenge.ui.joinchallenge.activity.ChallengeParticipationActivity.Companion.CHALLENGE_ID_EXTRA
import com.drivequant.drivekit.common.ui.navigation.ChallengeUIEntryPoint
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitLog

object ChallengeUI: ChallengeUIEntryPoint {
    internal const val TAG = "DriveKit Challenge UI"

    init {
        DriveKit.checkInitialization()
        DriveKitLog.i(TAG, "Initialization")
        DriveKitNavigationController.challengeUIEntryPoint = this
    }

    @JvmStatic
    fun initialize() {
        // Nothing to do currently.
    }

    internal val challengeDetailItems = listOf(
        ChallengeDetailItem.RESULTS,
        ChallengeDetailItem.RANKING,
        ChallengeDetailItem.TRIPS,
        ChallengeDetailItem.RULES
    )

    override fun startChallengeActivity(context: Context) {
        val intent = Intent(context, ChallengeListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    @JvmStatic
    fun createChallengeListFragment(): Fragment {
        return ChallengeListFragment()
    }

    override fun startChallengeParticipationActivity(context: Context, challengeId: String) {
        val intent = Intent(context, ChallengeParticipationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(CHALLENGE_ID_EXTRA, challengeId)
        context.startActivity(intent)
    }
}
