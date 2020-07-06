package com.drivequant.drivekit.driverachievement.ui.leaderboard.viewmodel

import android.content.Context
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.databaseutils.entity.DriverRanked
import com.drivequant.drivekit.databaseutils.entity.Ranking
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.ui.R

class RankingListData(private val ranking: Ranking) {
    fun getStatus(): RankingStatus {
        return RankingStatus.GOING_DOWN
    }

    fun getDriverPosition():Int = ranking.userPosition

    fun getLeaderBoardStatus(context: Context): SpannableString =
        DKSpannable().append("${ranking.userPosition}", context.resSpans {
            color(DriveKitUI.colors.secondaryColor())
            size(R.dimen.dk_text_xbig)
        }).append("/",context.resSpans {
            color(DriveKitUI.colors.mainFontColor())
            size(R.dimen.dk_text_xbig)
        }).append(
            "${ranking.nbDriverRanked}",context.resSpans {
                color(DriveKitUI.colors.mainFontColor())
                size(R.dimen.dk_text_xbig)
            }
        )
        .toSpannable()

    fun getLeaderBoardTitle(): String {
         return when (ranking.rankingType) {
            RankingType.SAFETY -> "Safety"
            RankingType.DISTRACTION -> "Distraction"
            RankingType.ECO_DRIVING -> "Eco-Driving"
        }
    }

    fun getIcon(): String {
        return when (ranking.rankingType) {
            RankingType.SAFETY -> "dk_achievements_safety"
            RankingType.DISTRACTION -> "dk_achievements_phone_distraction"
            RankingType.ECO_DRIVING -> "dk_achievements_ecodriving"
        }
    }

    fun getDriverRanked(): List<DriverRanked> = ranking.driversRanked
}