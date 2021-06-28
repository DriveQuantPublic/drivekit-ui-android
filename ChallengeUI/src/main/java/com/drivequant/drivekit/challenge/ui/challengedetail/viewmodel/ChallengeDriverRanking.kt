package com.drivequant.drivekit.challenge.ui.challengedetail.viewmodel

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Spannable
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRanking
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRankingItem
import com.drivequant.drivekit.common.ui.component.ranking.RankingHeaderDisplayType
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DriverProgression


internal class ChallengeDriverRanking(val viewModel: ChallengeDetailViewModel) : DKDriverRanking {
    override fun getHeaderDisplayType(): RankingHeaderDisplayType = RankingHeaderDisplayType.FULL
    override fun getTitle(): String = viewModel.getChallengeResultScoreTitle()
    override fun getIcon(context: Context): Drawable? = viewModel.getRankingHeaderIcon(context)
    override fun getProgression(): DriverProgression? = viewModel.getRankingProgression()
    override fun getDriverGlobalRank(context: Context): Spannable =
        viewModel.geRankingGlobalRank(context)

    override fun getScoreTitle(context: Context): String = viewModel.getScoreTitle(context)
    override fun getDriverRankingList(): List<DKDriverRankingItem> = viewModel.getRankingList()
    override fun getBackgroundColor(): Int = Color.parseColor("#FAFAFA")
}