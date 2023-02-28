package com.drivequant.drivekit.ui.mysynthesis.component.scorecard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.driverdata.timeline.DKScoreEvolutionTrend
import com.drivequant.drivekit.driverdata.timeline.DKScoreSynthesis
import com.drivequant.drivekit.driverdata.timeline.getDriverScoreSynthesis
import com.drivequant.drivekit.ui.R
import java.util.*

internal class MySynthesisScoreCardViewModel : ViewModel() {

    var onViewModelUpdated: (() -> Unit)? = null

    private var selectedScoreType: DKScoreType = DKScoreType.SAFETY
    private var selectedPeriod: DKPeriod = DKPeriod.WEEK
    private lateinit var driverTimeline: DKDriverTimeline
    private lateinit var selectedDate: Date
    private var scoreSynthesis: DKScoreSynthesis? = null

    val score: Double?
        get() = scoreSynthesis?.scoreValue
    val previousScore: Double?
        get() = scoreSynthesis?.previousScoreValue

    fun configure(scoreType: DKScoreType, period: DKPeriod, driverTimeline: DKDriverTimeline, selectedDate: Date) {
        this.selectedScoreType = scoreType
        this.selectedPeriod = period

        this.selectedDate = selectedDate
        this.driverTimeline = driverTimeline
        this.scoreSynthesis = driverTimeline.getDriverScoreSynthesis(this.selectedScoreType, selectedDate)

        this.onViewModelUpdated?.invoke()
    }

    private fun hasTrips() = driverTimeline.allContext.first { it.date == this.selectedDate }.numberTripTotal > 0

    private fun hasScoredTrips() = driverTimeline.allContext.first { it.date == this.selectedDate }.numberTripScored > 0

    fun showEvolutionScoreOutOfTen() =
        (hasScoredTrips() || (selectedScoreType == DKScoreType.DISTRACTION || selectedScoreType == DKScoreType.SPEEDING))
            && previousScore != null

    private fun isTimelineEmpty() = driverTimeline.allContext.isEmpty()

    @StringRes
    fun getCardTitleResId() = when (this.selectedScoreType) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_score
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_score
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_score
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_score
    }

    @StringRes
    fun getEvolutionTextResId() =
        if (isTimelineEmpty() || (!hasScoredTrips() && (selectedScoreType == DKScoreType.SAFETY && selectedScoreType == DKScoreType.ECO_DRIVING))) {
            R.string.dk_driverdata_mysynthesis_not_enough_data
        } else {
            if (!hasTrips()){
                when (selectedPeriod) {
                    DKPeriod.WEEK -> R.string.dk_driverdata_mysynthesis_no_driving_week
                    DKPeriod.MONTH -> R.string.dk_driverdata_mysynthesis_no_driving_month
                    DKPeriod.YEAR -> R.string.dk_driverdata_mysynthesis_no_driving_year
                }
            } else {
                when (selectedPeriod) {
                    DKPeriod.WEEK -> if (previousScore != null) R.string.dk_driverdata_mysynthesis_previous_week else R.string.dk_driverdata_mysynthesis_no_trip_prev_week
                    DKPeriod.MONTH -> if (previousScore != null) R.string.dk_driverdata_mysynthesis_previous_month else R.string.dk_driverdata_mysynthesis_no_trip_prev_month
                    DKPeriod.YEAR -> if (previousScore != null) R.string.dk_driverdata_mysynthesis_previous_year else R.string.dk_driverdata_mysynthesis_no_trip_prev_year
                }
            }
        }

    @DrawableRes
    fun getTrendIconResId(): Int {
        scoreSynthesis?.evolutionTrend?.let { trend ->
            return when (trend) {
                DKScoreEvolutionTrend.UP -> R.drawable.dk_driver_data_trend_positive
                DKScoreEvolutionTrend.DOWN -> R.drawable.dk_driver_data_trend_negative
                DKScoreEvolutionTrend.SAME -> R.drawable.dk_driver_data_trend_steady
            }
        }
        return R.drawable.dk_driver_data_trend_steady
    }
}
