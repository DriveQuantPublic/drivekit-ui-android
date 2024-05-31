package com.drivequant.drivekit.challenge.ui.challengelist.viewmodel

import android.content.Context
import android.text.Spannable
import androidx.annotation.DrawableRes
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.IntArg
import com.drivequant.drivekit.common.ui.utils.TextArg
import com.drivequant.drivekit.databaseutils.entity.ChallengeStatus
import com.drivequant.drivekit.databaseutils.entity.ChallengeType
import java.util.Date

class ChallengeData(
    val challengeId: String,
    val title: String,
    val startDate: Date,
    val endDate: Date,
    val startAndEndYear: Set<Date>,
    val isFinished: Boolean,
    private val rank: Int,
    val isRanked: Boolean,
    private val type: ChallengeType,
    val isRegistered: Boolean,
    val status: ChallengeStatus,
    private val nbDriverRegistered: Int,
) {
    @DrawableRes
    fun getIconResId(): Int? = when (type) {
        ChallengeType.SAFETY,
        ChallengeType.HARD_BRAKING,
        ChallengeType.HARD_ACCELERATION -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_safety_flat
        ChallengeType.ECODRIVING -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_ecodriving_flat
        ChallengeType.DISTRACTION -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_distraction_flat
        ChallengeType.SPEEDING -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_speeding_flat
        ChallengeType.DEPRECATED,
        ChallengeType.UNKNOWN -> null
    }

    fun getParticipationText(context: Context): Spannable {
        val textColor = DKColors.secondaryColor

        return if (this.isFinished) {
            if (this.isRegistered) {
                if (this.isRanked) {
                    DKResource.buildString(context, R.string.dk_challenge_list_ranked, textColor, TextArg("${this.rank} / ${this.nbDriverRegistered}"))
                } else {
                    DKSpannable().append(context.getString(R.string.dk_challenge_list_not_ranked)).toSpannable()
                }
            } else {
                DKSpannable().append(context.getString(R.string.dk_challenge_list_not_registered)).toSpannable()
            }
        } else {
            if (this.isRegistered) {
                if (this.isRanked) {
                    DKResource.buildString(
                        context,
                        R.string.dk_challenge_list_ranked,
                        textColor,
                        TextArg("${this.rank } / ${this.nbDriverRegistered}")
                    )
                } else {
                    if (this.displayParticipantsCount()) {
                        DKResource.buildString(
                            context,
                            R.string.dk_challenge_list_registered_among,
                            textColor,
                            IntArg(this.nbDriverRegistered)
                        )
                    } else {
                        DKSpannable().append(context.getString(R.string.dk_challenge_list_registered)).toSpannable()
                    }
                }
            } else {
                if (this.displayParticipantsCount()) {
                    DKResource.buildString(context, R.string.dk_challenge_list_join_among, textColor, IntArg(this.nbDriverRegistered))
                } else {
                    DKSpannable().append(context.getString(R.string.dk_challenge_list_join)).toSpannable()
                }
            }
        }
    }

    fun shouldDisplayChallengeDetail() = isRanked

    fun shouldDisplayExplaining() = isFinished && (!isRegistered || !isRanked)

    private fun displayParticipantsCount() = nbDriverRegistered > 100
}

interface ChallengeListener {
    fun onClickChallenge(challengeData: ChallengeData)
}
