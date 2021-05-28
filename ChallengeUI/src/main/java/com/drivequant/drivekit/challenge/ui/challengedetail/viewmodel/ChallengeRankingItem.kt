package com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import com.drivequant.drivekit.challenge.ui.R
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRankingItem
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.core.DriveKit

class ChallengeRankingItem(
    private val driverRank: Int,
    private val driverNickname: String,
    private val driverDistance: Double,
    private val driverScore: Double,
    private val driverId: String?,
    private val isJumpRank: Boolean) : DKDriverRankingItem {

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

    override fun getNickname(context: Context): String = driverNickname

    override fun getDistance(context: Context): String =
        DKDataFormatter.formatMeterDistanceInKm(context, driverDistance * 1000).convertToString()

    override fun getScore(context: Context, textColor: Int): Spannable {
        return if (driverScore == 10.0) {
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
    }

    override fun isCurrentUser(): Boolean = DriveKit.config.userId == driverId

    override fun isJumpRank(): Boolean = isJumpRank
}
