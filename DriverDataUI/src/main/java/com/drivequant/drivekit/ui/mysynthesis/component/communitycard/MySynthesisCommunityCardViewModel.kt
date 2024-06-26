package com.drivequant.drivekit.ui.mysynthesis.component.communitycard

import android.content.Context
import android.text.SpannableString
import androidx.annotation.ColorInt
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.graphical.DKStyle
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.FormatType
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.community.statistics.DKCommunityStatistics
import com.drivequant.drivekit.driverdata.community.statistics.DKScoreStatistics
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.driverdata.timeline.DKScoreSynthesis
import com.drivequant.drivekit.driverdata.timeline.allContextItemAt
import com.drivequant.drivekit.driverdata.timeline.getDriverScoreSynthesis
import com.drivequant.drivekit.driverdata.timeline.getValue
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.getMedianScore
import java.util.Date

internal class MySynthesisCommunityCardViewModel : ViewModel() {

    var onViewModelUpdated: (() -> Unit)? = null
    val gaugeViewModel = MySynthesisGaugeViewModel()

    private var selectedScoreType: DKScoreType = DKScoreType.SAFETY
    private var selectedPeriod: DKPeriod = DKPeriod.WEEK
    private var driverTimeline: DKDriverTimeline? = null
    private var scoreSynthesis: DKScoreSynthesis? = null
    private var allContextItem: DKDriverTimeline.DKAllContextItem? = null
    private lateinit var statistics: DKCommunityStatistics
    private var selectedDate: Date? = null

    fun configure(
        scoreType: DKScoreType,
        period: DKPeriod,
        driverTimeline: DKDriverTimeline?,
        selectedDate: Date?,
        statistics: DKCommunityStatistics
    ) {
        this.statistics = statistics
        this.selectedScoreType = scoreType
        this.selectedPeriod = period
        this.selectedDate = selectedDate
        this.driverTimeline = driverTimeline

        if (driverTimeline != null && selectedDate != null) {
            this.scoreSynthesis = driverTimeline.getDriverScoreSynthesis(this.selectedScoreType, selectedDate)
            this.allContextItem = driverTimeline.allContextItemAt(selectedDate)
        }

        val scoreStatistics: DKScoreStatistics = statistics.getScoreStatistics(scoreType)
        this.gaugeViewModel.configure(scoreType, this.scoreSynthesis?.scoreValue, scoreStatistics.min, scoreStatistics.getMedianScore(), scoreStatistics.max)
        this.onViewModelUpdated?.invoke()
    }

    private fun hasNoTrip(allContextItem: DKDriverTimeline.DKAllContextItem?) =
        allContextItem == null

    fun getTitleText(context: Context): String {
        val bestThanCommunityThreshold = 55
        val worstThanCommunityThreshold = 45

        if (hasNoTrip(this.allContextItem)) {
            return context.getString(R.string.dk_driverdata_mysynthesis_no_driving)
        } else {
            val score = allContextItem?.getValue(this.selectedScoreType)
            val scoreStatistics = this.statistics.getScoreStatistics(this.selectedScoreType)
            return score?.let {
                val userPositionFromLowerScores = scoreStatistics.percentCommunityLowerThan(score)
                val userPositionFromHigherScores = scoreStatistics.percentCommunityGreaterThan(score)

                if (userPositionFromLowerScores > bestThanCommunityThreshold) {
                    context.getString(
                        R.string.dk_driverdata_mysynthesis_you_are_best,
                        DKDataFormatter.formatPercentage(userPositionFromLowerScores)
                    )
                } else if (userPositionFromLowerScores < worstThanCommunityThreshold) {
                    context.getString(
                        R.string.dk_driverdata_mysynthesis_you_are_lower,
                        DKDataFormatter.formatPercentage(userPositionFromHigherScores)
                    )
                } else {
                    context.getString(R.string.dk_driverdata_mysynthesis_you_are_average)
                }
            } ?: run {
                return "-" // this should not happen
            }
        }
    }

    @ColorInt
    fun getTitleColor(): Int = if (hasNoTrip(this.allContextItem)) {
        DKColors.complementaryFontColor
    } else {
        DKColors.mainFontColor
    }

    fun getCommunityTripsCountText(context: Context) = getTripsCountText(context, this.statistics.tripNumber)

    fun getCommunityDistanceKmText(context: Context) = getDistanceText(context, this.statistics.distance)

    fun getDriverTripsCountText(context: Context): SpannableString {
        val tripsCount = this.selectedDate?.let {
            driverTimeline?.allContextItemAt(it)?.numberTripTotal
        } ?: 0
        return getTripsCountText(context, tripsCount)
    }

    fun getDriverDistanceKmText(context: Context): SpannableString {
        val distance = this.selectedDate?.let {
            driverTimeline?.allContextItemAt(it)?.distance
        } ?: 0.0
        return getDistanceText(context, distance)
    }

    fun getCommunityActiveDriversText(context: Context) = DKSpannable().apply {
        append(
            context,
            DKDataFormatter.formatNumber(statistics.activeDriverNumber),
            DKColors.complementaryFontColor,
            DKStyle.NORMAL_TEXT
        )
        space()
        append(
            context,
            context.getString(R.string.dk_driverdata_mysynthesis_drivers),
            DKColors.complementaryFontColor,
            DKStyle.SMALL_TEXT
        )
    }.toSpannable()

    private fun getTripsCountText(context: Context, tripsCount: Int): SpannableString {
        val spannable = DKSpannable()
        val tripsString = context.resources.getQuantityString(com.drivequant.drivekit.common.ui.R.plurals.trip_plural, tripsCount)
        if (tripsCount == 0) {
            spannable.append(
                context,
                context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_no_trip),
                DKColors.complementaryFontColor,
                DKStyle.SMALL_TEXT
            )
        } else {
            spannable.append(
                context,
                DKDataFormatter.formatNumber(tripsCount),
                DKColors.complementaryFontColor,
                DKStyle.NORMAL_TEXT
            ).space().append(
                context, tripsString,
                DKColors.complementaryFontColor,
                DKStyle.SMALL_TEXT
            )
        }
        return spannable.toSpannable()
    }

    private fun getDistanceText(context: Context, distanceKm: Double): SpannableString {
        val spannable = DKSpannable()

        DKDataFormatter.formatMeterDistanceInKm(
            context = context,
            distance = distanceKm * 1000,
            minDistanceToRemoveFractions = 0.0,
        ).forEach {
            when (it) {
                is FormatType.VALUE -> spannable.append(context, it.value, DKColors.complementaryFontColor, DKStyle.NORMAL_TEXT)
                is FormatType.UNIT -> spannable.append(context, it.value, DKColors.complementaryFontColor, DKStyle.SMALL_TEXT)
                is FormatType.SEPARATOR -> spannable.space()
            }
        }
        return spannable.toSpannable()
    }
}
