package com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel

import android.content.Context
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.databaseutils.entity.Ranking
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.ui.R

class RankingHeaderData(private val ranking: Ranking) {

    fun getProgression(previousRank: Int): DriverProgression {
       return if (previousRank > ranking.userPosition) {
            DriverProgression.GOING_UP
        } else {
            DriverProgression.GOING_DOWN
        }
    }

    fun getDriverGlobalRank(context: Context): SpannableString {
        val userPosition = if (ranking.userPosition == 0) {
            "-"
        } else {
            "${ranking.userPosition}"
        }
        return DKSpannable().append(userPosition, context.resSpans {
            color(DriveKitUI.colors.secondaryColor())
            size(R.dimen.dk_text_xbig)
        }).append(" / ",context.resSpans {
            color(DriveKitUI.colors.mainFontColor())
            size(R.dimen.dk_text_xbig)
        }).append(
            "${ranking.nbDriverRanked}", context.resSpans {
                color(DriveKitUI.colors.mainFontColor())
                size(R.dimen.dk_text_xbig)
            }
        )
            .toSpannable()
    }

    fun getTitle(): String {
        return when (ranking.rankingType) {
            RankingType.SAFETY -> "dk_common_safety"
            RankingType.DISTRACTION -> "dk_common_distraction"
            RankingType.ECO_DRIVING -> "dk_common_ecodriving"
        }
    }

    fun getIcon(): String {
        return when (ranking.rankingType) {
            RankingType.SAFETY -> "dk_achievements_safety"
            RankingType.DISTRACTION -> "dk_achievements_phone_distraction"
            RankingType.ECO_DRIVING -> "dk_achievements_ecodriving"
        }
    }
}