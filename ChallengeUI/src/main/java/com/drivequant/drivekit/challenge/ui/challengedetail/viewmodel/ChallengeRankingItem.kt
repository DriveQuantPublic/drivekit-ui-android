package com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRankingItem
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.ChallengeType

class ChallengeRankingItem(private val viewModel: ChallengeDetailViewModel,
                           private val driverRank: Int,
                           private val driverPseudo: String?,
                           private val driverDistance: Double,
                           private val driverScore: Double) : DKDriverRankingItem {

    override fun getRank(): Int = driverRank

    override fun getRankResource(context: Context): Drawable?  =
        when (driverRank) {
            1 -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_rank_1
            2 -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_rank_2
            3 -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_rank_3
            else -> null
        }?.let {
            ContextCompat.getDrawable(context, it)
        }

    override fun getPseudo(context: Context): String = if (driverPseudo.isNullOrBlank()) {
        context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_anonymous_driver)
    } else driverPseudo

    override fun getDistance(context: Context): String = viewModel.formatChallengeDistance(driverDistance, context).convertToString()

    override fun getScore(context: Context, textColor: Int): Spannable {
        return if (viewModel.challenge.challengeType == ChallengeType.UNKNOWN || viewModel.challenge.challengeType == ChallengeType.DEPRECATED) {
            SpannableString("")
        } else {
            if (driverScore == 10.0) {
                driverScore.removeZeroDecimal()
            } else {
                driverScore.format(2)
            }.let {
                DKSpannable().append(it, context.resSpans {
                    size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_medium)
                    color(textColor)
                }).append(" / 10", context.resSpans {
                    size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_small)
                    color(textColor)
                }).toSpannable()
            }
        }
    }

    override fun isCurrentUser(): Boolean {
        viewModel.challengeDetailData?.let { challengeDetail ->
            challengeDetail.driversRanked?.let {
                if (it[challengeDetail.userIndex].rank == driverRank) {
                    return true
                }
            }
        }
        return false
    }

    override fun isJumpRank(): Boolean = false
}
