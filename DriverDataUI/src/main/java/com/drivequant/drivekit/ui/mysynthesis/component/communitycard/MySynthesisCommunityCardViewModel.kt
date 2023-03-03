package com.drivequant.drivekit.ui.mysynthesis.component.communitycard

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.utils.DKScoreTypeLevel
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.community.statistics.DKCommunityStatistics
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.driverdata.timeline.DKScoreEvolutionTrend
import com.drivequant.drivekit.driverdata.timeline.DKScoreSynthesis
import com.drivequant.drivekit.driverdata.timeline.getDriverScoreSynthesis
import com.drivequant.drivekit.ui.R
import java.util.*

internal class MySynthesisCommunityCardViewModel : ViewModel() {

    var onViewModelUpdated: (() -> Unit)? = null

    var selectedScoreType: DKScoreType = DKScoreType.SAFETY
        private set
    private var selectedPeriod: DKPeriod = DKPeriod.WEEK
    private var driverTimeline: DKDriverTimeline? = null
    private var scoreSynthesis: DKScoreSynthesis? = null
    private var allContextItem: DKDriverTimeline.DKAllContextItem? = null
    private lateinit var statistics: DKCommunityStatistics
    private lateinit var selectedDate: Date

    fun configure(scoreType: DKScoreType, period: DKPeriod, driverTimeline: DKDriverTimeline?, selectedDate: Date?, statistics: DKCommunityStatistics) {
        this.statistics = statistics
        this.selectedScoreType = scoreType
        this.selectedPeriod = period
        if (driverTimeline != null && selectedDate != null) {
            this.selectedDate = selectedDate
            this.driverTimeline = driverTimeline
            this.scoreSynthesis = driverTimeline.getDriverScoreSynthesis(this.selectedScoreType, selectedDate)
            this.allContextItem = driverTimeline.allContext.first { it.date == this.selectedDate }
        }
        this.onViewModelUpdated?.invoke()
    }

    private fun hasNoTrip(allContextItem: DKDriverTimeline.DKAllContextItem?) = allContextItem == null

    private fun hasData(score: DKScoreType, allContextItem: DKDriverTimeline.DKAllContextItem?): Boolean {
        return when (score) {
            DKScoreType.SAFETY -> allContextItem?.safety != null
            DKScoreType.ECO_DRIVING -> allContextItem?.ecoDriving != null
            DKScoreType.DISTRACTION -> allContextItem?.phoneDistraction != null
            DKScoreType.SPEEDING -> allContextItem?.speeding != null
        }
    }

    fun getTitleText(context: Context): String {
        if (hasNoTrip(this.allContextItem)) {
            return when (this.selectedPeriod) {
                DKPeriod.WEEK -> R.string.dk_driverdata_mysynthesis_no_driving_week
                DKPeriod.MONTH -> R.string.dk_driverdata_mysynthesis_no_driving_month
                DKPeriod.YEAR -> R.string.dk_driverdata_mysynthesis_no_driving_year
            }.let { context.getString(it) }
        } else if (!hasData(this.selectedScoreType, this.allContextItem)) {
            return context.getString(R.string.dk_driverdata_mysynthesis_not_enough_data)
        } else {
            // dk_driverdata_mysynthesis_you_are_best + dk_driverdata_mysynthesis_you_are_average + lower
            val scoreStatistics = when (this.selectedScoreType) {
                DKScoreType.SAFETY -> this.statistics.safety
                DKScoreType.ECO_DRIVING -> this.statistics.ecoDriving
                DKScoreType.DISTRACTION -> this.statistics.distraction
                DKScoreType.SPEEDING -> this.statistics.speeding
            }
            return scoreStatistics.toString()
        }
    }

    @StringRes
    fun getLegendTitle() = when (selectedScoreType) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_safety_score
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_eco_score
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_distraction_score
        DKScoreType.SPEEDING -> R.string.dk_driverdata_speeding_score
    }

    @StringRes
    fun getLegendDescription() = when (selectedScoreType) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_score_info
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_score_info
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_score_info
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_score_info
    }

    @StringRes
    fun getLegendScoreAppreciationTitle(scoreLevel: DKScoreTypeLevel) = when (scoreLevel) {
        DKScoreTypeLevel.EXCELLENT -> R.string.dk_driverdata_mysynthesis_score_title_excellent
        DKScoreTypeLevel.VERY_GOOD -> R.string.dk_driverdata_mysynthesis_score_title_very_good
        DKScoreTypeLevel.GREAT -> R.string.dk_driverdata_mysynthesis_score_title_good
        DKScoreTypeLevel.MEDIUM -> R.string.dk_driverdata_mysynthesis_score_title_average
        DKScoreTypeLevel.NOT_GOOD -> R.string.dk_driverdata_mysynthesis_score_title_low
        DKScoreTypeLevel.BAD -> R.string.dk_driverdata_mysynthesis_score_title_bad
        DKScoreTypeLevel.VERY_BAD -> R.string.dk_driverdata_mysynthesis_score_title_very_bad
    }

    private fun compute() {

    }
}
