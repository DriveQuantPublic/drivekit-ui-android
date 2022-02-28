package com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRankingItem
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.convertToString

class ChallengeRankingItem(private val viewModel: ChallengeDetailViewModel,
                           private val driverRank: Int,
                           private val driverPseudo: String?,
                           private val driverDistance: Double,
                           private val driverScore: Double) : DKDriverRankingItem {

    override fun getRank(): Int = driverRank

    override fun getRankResource(context: Context): Drawable?  =
        when (driverRank) {
            1 -> "dk_common_rank_1"
            2 -> "dk_common_rank_2"
            3 -> "dk_common_rank_3"
            else -> null
        }?.let {
            DKResource.convertToDrawable(
                context,
                it
            )
        }

    override fun getPseudo(context: Context): String = if (driverPseudo.isNullOrBlank()) {
        DKResource.convertToString(context, "dk_common_anonymous_driver")
    } else driverPseudo

    override fun getDistance(context: Context): String = viewModel.formatChallengeDistance(driverDistance, context).convertToString()

    override fun getScore(context: Context, textColor: Int): Spannable {
        return when (viewModel.challenge.themeCode) {
            in 101..221,
            401 -> return if (driverScore == 10.0) {
                driverScore.removeZeroDecimal()
            } else {
                driverScore.format(2)
            }.let {
                DKSpannable().append(it, context.resSpans {
                    size(R.dimen.dk_text_medium)
                    color(textColor)
                }).append(" / 10", context.resSpans {
                    size(R.dimen.dk_text_small)
                    color(textColor)
                }).toSpannable()
            }
            in 306..309 -> DKSpannable().append(viewModel.formatChallengeDuration(driverScore, context)
                    .convertToString(),
                    context.resSpans {
                        color(textColor)
                        size(R.dimen.dk_text_normal)
                    }).toSpannable()

            in 302..305 -> DKSpannable().append(
                    viewModel.formatChallengeDistance(driverScore, context).convertToString(),
                    context.resSpans {
                        color(textColor)
                        size(R.dimen.dk_text_normal)
                    }).toSpannable()

            301 -> DKSpannable().append(driverScore.removeZeroDecimal(), context.resSpans {
                size(R.dimen.dk_text_medium)
                color(textColor)
            }).toSpannable()
            else -> SpannableString("")
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
