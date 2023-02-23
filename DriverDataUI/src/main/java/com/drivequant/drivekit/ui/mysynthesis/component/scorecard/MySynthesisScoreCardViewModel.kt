package com.drivequant.drivekit.ui.mysynthesis.component.scorecard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.common.DKPeriod
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.driverdata.timeline.DKScoreEvolutionTrend
import com.drivequant.drivekit.driverdata.timeline.DKScoreSynthesis
import com.drivequant.drivekit.driverdata.timeline.getDriverScoreSynthesis
import com.drivequant.drivekit.ui.R
import java.util.*

// TODO restore internal
class MySynthesisScoreCardViewModel : ViewModel() {

    val changeObserver: MutableLiveData<Any> = MutableLiveData()

    var selectedScoreType: DKScoreType = DKScoreType.SAFETY
    var selectedPeriod: DKPeriod = DKPeriod.WEEK
    var driverTimeline: DKDriverTimeline? = null
    var scoreSynthesis: DKScoreSynthesis? = null
    private lateinit var selectedDate: Date

    fun configure(scoreType: DKScoreType?, period: DKPeriod?, driverTimeline: DKDriverTimeline?, selectedDate: Date?) {
        if (scoreType != null && period != null && selectedDate != null) {
            this.selectedScoreType = scoreType
            this.selectedPeriod = period
            this.selectedDate = selectedDate
        }
        if (driverTimeline != null && selectedDate != null) {
            this.driverTimeline = driverTimeline
            this.scoreSynthesis = driverTimeline.getDriverScoreSynthesis(this.selectedScoreType, selectedDate)
        }
        this.changeObserver.postValue(Any())
    }

    fun hasScoredTrips(): Boolean {
        return driverTimeline?.allContext?.firstOrNull { it.date == this.selectedDate }?.numberTripScored?.let {nbTripScored ->
            nbTripScored > 0
        } ?: run {
            return false
        }
    }

    fun isTimelineEmpty() = driverTimeline?.allContext?.isEmpty() ?: true

    fun hasPreviousData() = scoreSynthesis?.previousScoreValue != null

    @StringRes
    fun getCardTitleResId() = when (this.selectedScoreType) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_score
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_score
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_score
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_score
    }

    @StringRes
    fun getEvolutionTextResId() =
        if (isTimelineEmpty()) {
            R.string.dk_driverdata_mysynthesis_not_enough_data
        } else if (!hasScoredTrips()){
            when (selectedPeriod) {
                DKPeriod.WEEK -> R.string.dk_driverdata_mysynthesis_no_driving_week
                DKPeriod.MONTH -> R.string.dk_driverdata_mysynthesis_no_driving_month
                DKPeriod.YEAR -> R.string.dk_driverdata_mysynthesis_no_driving_year
            }
        } else {
            when (selectedPeriod) {
                DKPeriod.WEEK -> if (hasPreviousData()) R.string.dk_driverdata_mysynthesis_previous_week else R.string.dk_driverdata_mysynthesis_no_trip_prev_week
                DKPeriod.MONTH -> if (hasPreviousData()) R.string.dk_driverdata_mysynthesis_previous_month else R.string.dk_driverdata_mysynthesis_no_trip_prev_month
                DKPeriod.YEAR -> if (hasPreviousData()) R.string.dk_driverdata_mysynthesis_previous_year else R.string.dk_driverdata_mysynthesis_no_trip_prev_year
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