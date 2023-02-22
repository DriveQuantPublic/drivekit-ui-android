package com.drivequant.drivekit.ui.mysynthesis.component.scorecard

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.core.common.DKPeriod
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.driverdata.timeline.DKScoreSynthesis
import com.drivequant.drivekit.driverdata.timeline.getDriverScoreSynthesis
import com.drivequant.drivekit.ui.R
import java.util.*

// TODO internal?
class MySynthesisScoreCardViewModel : ViewModel() {

    val changeObserver: MutableLiveData<Any> = MutableLiveData()

    lateinit var selectedScoreType: DKScoreType
    lateinit var selectedPeriod: DKPeriod
    lateinit var driverTimeline: DKDriverTimeline
    var scoreSynthesis: DKScoreSynthesis? = null
    private lateinit var selectedDate: Date

    fun configure(scoreType: DKScoreType?, period: DKPeriod?, driverTimeline: DKDriverTimeline?, selectedDate: Date?) {
        if (scoreType != null && period != null && driverTimeline != null && selectedDate != null) {
            this.selectedScoreType = scoreType
            this.selectedPeriod = period
            this.driverTimeline = driverTimeline
            this.selectedDate = selectedDate
            this.scoreSynthesis = driverTimeline.getDriverScoreSynthesis(this.selectedScoreType, selectedDate)
        } else {
            // TODO empty Timeline
        }
        this.changeObserver.postValue(Any())
    }

    @StringRes
    fun getCardTitleResId() = when (this.selectedScoreType) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_score
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_score
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_score
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_score
    }
}