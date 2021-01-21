package com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.driverachievement.ui.R

class RankingDriverData(
    val driverRank: Int,
    private val driverNickname: String?,
    private val driverDistance: Double,
    private val driverScore: Double,
    val driverId: String) {

    fun getFormattedDistance(context: Context): String =
        DKDataFormatter.formatMeterDistanceInKm(context, driverDistance * 1000)

    fun getFormattedScore(context: Context, textColor: Int): SpannableString {
        val preFormattedScore = if (driverScore == 10.0) {
            driverScore.removeZeroDecimal()
        } else {
            driverScore.format(2)
        }
        return DKSpannable().append(preFormattedScore, context.resSpans {
            size(R.dimen.dk_text_medium)
            color(textColor)
        }).append(" / 10", context.resSpans {
            size(R.dimen.dk_text_small)
            color(textColor)
        }).toSpannable()
    }

    fun getNickname(context: Context): String {
        return if (driverNickname.isNullOrEmpty()) {
            DKResource.convertToString(context, "dk_achievements_ranking_anonymous_driver")
        } else driverNickname
    }

    fun getRankDrawable(context: Context): Drawable? {
        val rankResId = when (driverRank) {
            1 -> "dk_achievements_rank_1"
            2 -> "dk_achievements_rank_2"
            3 -> "dk_achievements_rank_3"
            else -> null
        }
        return rankResId?.let {
            DKResource.convertToDrawable(
                context,
                it
            )
        }
    }
}