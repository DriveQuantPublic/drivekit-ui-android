package com.drivequant.drivekit.ui.mysynthesis.component.scorecard

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.getValue
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.driverdata.timeline.DKScoreEvolutionTrend
import com.drivequant.drivekit.driverdata.timeline.DKScoreSynthesis
import com.drivequant.drivekit.ui.R

internal class MySynthesisScoreCardViewModel : ViewModel() {

    var onViewModelUpdated: (() -> Unit)? = null

    private lateinit var selectedScore: DKScoreType
    private var selectedPeriod: DKPeriod = DKPeriod.WEEK
    private var scoreSynthesis: DKScoreSynthesis? = null

    private var allContextItem: DKDriverTimeline.DKAllContextItem? = null

    val score: Double?
        get() = scoreSynthesis?.scoreValue
    val previousScore: Double?
        get() = scoreSynthesis?.previousScoreValue

    fun configure(
        score: DKScoreType,
        period: DKPeriod,
        scoreSynthesis: DKScoreSynthesis?,
        allContextItem: DKDriverTimeline.DKAllContextItem?
    ) {
        this.selectedScore = score
        this.selectedPeriod = period
        this.scoreSynthesis = scoreSynthesis
        this.allContextItem = allContextItem

        this.onViewModelUpdated?.invoke()
    }
    fun showEvolutionScoreOutOfTen() = this.score != null && this.previousScore != null

    @StringRes
    fun getCardTitleResId() = when (this.selectedScore) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_score
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_score
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_score
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_score
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
    fun getEvolutionText(context: Context): String {
        val allContextItem = this.allContextItem
        if (hasNoTrip(allContextItem)) {
            return when (selectedPeriod) {
                DKPeriod.WEEK -> R.string.dk_driverdata_mysynthesis_no_driving_week
                DKPeriod.MONTH -> R.string.dk_driverdata_mysynthesis_no_driving_month
                DKPeriod.YEAR -> R.string.dk_driverdata_mysynthesis_no_driving_year
            }.let { context.getString(it) }
        } else if (!hasData(this.selectedScore, allContextItem)) {
            return context.getString(R.string.dk_driverdata_mysynthesis_not_enough_data)
        } else if (this.previousScore != null) {
            return when (selectedPeriod) {
                DKPeriod.WEEK -> context.getString(R.string.dk_driverdata_mysynthesis_previous_week)
                DKPeriod.MONTH -> context.getString(R.string.dk_driverdata_mysynthesis_previous_month)
                DKPeriod.YEAR -> {
                    val currentYear = allContextItem?.date?.getValue(CalendarField.YEAR)
                    String.format(context.getString(R.string.dk_driverdata_mysynthesis_previous_year), currentYear)
                }
            }
        } else {
            return when (selectedPeriod) {
                DKPeriod.WEEK -> R.string.dk_driverdata_mysynthesis_no_trip_prev_week
                DKPeriod.MONTH -> R.string.dk_driverdata_mysynthesis_no_trip_prev_month
                DKPeriod.YEAR -> R.string.dk_driverdata_mysynthesis_no_trip_prev_year
            }.let { context.getString(it) }
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
