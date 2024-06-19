package com.drivequant.drivekit.driverachievement.ui.rankings.viewmodel

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Spannable
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRanking
import com.drivequant.drivekit.common.ui.component.ranking.DKDriverRankingItem
import com.drivequant.drivekit.common.ui.component.ranking.RankingHeaderDisplayType
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DriverProgression
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.databaseutils.entity.RankingType
import com.drivequant.drivekit.driverachievement.ranking.RankingPeriod
import com.drivequant.drivekit.driverachievement.ui.DriverAchievementUI
import com.drivequant.drivekit.driverachievement.ui.R

class RankingData(
    private val viewModel: RankingViewModel
) : DKDriverRanking {

    override fun getHeaderDisplayType(): RankingHeaderDisplayType =
        if (DriverAchievementUI.rankingTypes.size > 1 && viewModel.rankingSelectorsData.size > 1)
            RankingHeaderDisplayType.COMPACT else RankingHeaderDisplayType.FULL

    @StringRes
    override fun getTitleResId(): Int =
        when (viewModel.fetchedRanking.rankingType) {
            RankingType.SAFETY -> com.drivequant.drivekit.common.ui.R.string.dk_common_safety
            RankingType.DISTRACTION -> com.drivequant.drivekit.common.ui.R.string.dk_common_distraction
            RankingType.ECO_DRIVING -> com.drivequant.drivekit.common.ui.R.string.dk_common_ecodriving
            RankingType.SPEEDING -> com.drivequant.drivekit.common.ui.R.string.dk_common_speed_limit
        }

    override fun getIcon(context: Context): Drawable? =
        when (viewModel.fetchedRanking.rankingType) {
            RankingType.SAFETY -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_safety_flat
            RankingType.DISTRACTION -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_distraction_flat
            RankingType.ECO_DRIVING -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_ecodriving_flat
            RankingType.SPEEDING -> com.drivequant.drivekit.common.ui.R.drawable.dk_common_speeding_flat
        }.let {
            ContextCompat.getDrawable(context, it)
        }

    override fun getProgression(): DriverProgression? {
        val ranking = viewModel.fetchedRanking
        if (ranking.userPosition > 0) {
            ranking.userPreviousPosition?.let {
                val delta = it - ranking.userPosition
                return if (delta > 0) {
                    DriverProgression.GOING_UP
                } else {
                    DriverProgression.GOING_DOWN
                }
            }
        }
        return null
    }

    override fun getDriverGlobalRank(context: Context): Spannable =
        if (viewModel.fetchedRanking.userPosition == 0) {
            "-"
        } else {
            "${viewModel.fetchedRanking.userPosition}"
        }.let {
            DKSpannable().append(it, context.resSpans {
                color(DKColors.secondaryColor)
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_xbig)
            }).append(" / ", context.resSpans {
                color(DKColors.mainFontColor)
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_xbig)
            }).append(
                "${viewModel.fetchedRanking.nbDriverRanked}", context.resSpans {
                    color(DKColors.mainFontColor)
                    size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_xbig)
                }
            ).toSpannable()
        }

    override fun getScoreTitle(context: Context): String = context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_ranking_score)

    override fun getDriverRankingList(): List<DKDriverRankingItem> = viewModel.rankingDriversData
    override fun getBackgroundColor(): Int = Color.parseColor("#FAFAFA")
    override fun hasInfoButton() = true
    override fun getInfoPopupMessage(context: Context) =
        when (viewModel.selectedRankingSelectorData.rankingPeriod) {
            RankingPeriod.LEGACY -> R.string.dk_achievements_ranking_legacy_info
            RankingPeriod.WEEKLY -> R.string.dk_achievements_ranking_week_info
            RankingPeriod.MONTHLY -> R.string.dk_achievements_ranking_month_info
            RankingPeriod.ALL_TIME -> R.string.dk_achievements_ranking_permanent_info
        }.let {
            context.getString(it)
        }

    override fun getInfoPopupTitle(context: Context) = context.getString(R.string.app_name)
}
