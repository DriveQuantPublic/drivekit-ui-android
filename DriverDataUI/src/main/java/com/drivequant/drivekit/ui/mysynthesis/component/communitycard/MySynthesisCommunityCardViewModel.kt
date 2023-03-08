package com.drivequant.drivekit.ui.mysynthesis.component.communitycard

import android.content.Context
import android.text.SpannableString
import androidx.annotation.ColorInt
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.graphical.DKStyle
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.FormatType
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.community.statistics.DKCommunityStatistics
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.driverdata.timeline.DKScoreSynthesis
import com.drivequant.drivekit.driverdata.timeline.getDriverScoreSynthesis
import com.drivequant.drivekit.ui.R
import java.text.NumberFormat
import java.util.*

internal class MySynthesisCommunityCardViewModel : ViewModel() {

    var onViewModelUpdated: (() -> Unit)? = null

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
            this.allContextItem = driverTimeline.allContext.first { it.date == this.selectedDate }
        }
        this.onViewModelUpdated?.invoke()
    }

    private fun hasNoTrip(allContextItem: DKDriverTimeline.DKAllContextItem?) =
        allContextItem == null

    private fun hasData(
        score: DKScoreType,
        allContextItem: DKDriverTimeline.DKAllContextItem?
    ): Boolean {
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
            val score = getDriverScore(this.selectedScoreType, this.allContextItem)
            val scoreStatistics = getDKScoreStatistics(this.selectedScoreType)
            return score?.let {
                val lowerThanPercent = scoreStatistics.percentCommunityLowerThan(score)
                val greaterThanPercent = scoreStatistics.percentCommunityGreaterThan(score)

                if (lowerThanPercent > 55) {
                    context.getString(
                        R.string.dk_driverdata_mysynthesis_you_are_best,
                        DKDataFormatter.formatPercentage(lowerThanPercent, appendSpace = false)
                    )
                } else if (greaterThanPercent > 45) {
                    context.getString(
                        R.string.dk_driverdata_mysynthesis_you_are_lower,
                        DKDataFormatter.formatPercentage(greaterThanPercent, appendSpace = false)
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
        DriveKitUI.colors.complementaryFontColor()
    } else if (!hasData(this.selectedScoreType, this.allContextItem)) {
        DriveKitUI.colors.complementaryFontColor()
    } else {
        DriveKitUI.colors.primaryColor()
    }

    private fun getDriverScore(
        scoreType: DKScoreType,
        allContextItem: DKDriverTimeline.DKAllContextItem?
    ) = when (scoreType) {
        DKScoreType.SAFETY -> allContextItem?.safety?.score
        DKScoreType.ECO_DRIVING -> allContextItem?.ecoDriving?.score
        DKScoreType.DISTRACTION -> allContextItem?.phoneDistraction?.score
        DKScoreType.SPEEDING -> allContextItem?.speeding?.score
    }

    private fun getDKScoreStatistics(scoreType: DKScoreType) = when (scoreType) {
        DKScoreType.SAFETY -> this.statistics.safety
        DKScoreType.ECO_DRIVING -> this.statistics.ecoDriving
        DKScoreType.DISTRACTION -> this.statistics.distraction
        DKScoreType.SPEEDING -> this.statistics.speeding
    }

    fun getCommunityTripsCountText(context: Context) = getTripsCountText(context, this.statistics.tripNumber)

    fun getCommunityDistanceKmText(context: Context) = getDistanceText(context, this.statistics.distance)

    fun getDriverTripsCountText(context: Context) = getTripsCountText(context, driverTimeline?.allContext?.sumOf { it.numberTripScored } ?: 0)

    fun getDriverDistanceKmText(context: Context) = getDistanceText(context, driverTimeline?.allContext?.sumOf { it.distance } ?: 0.0)

    fun getCommunityActiveDriversText(context: Context) = DKSpannable().apply {
        append(
            context,
            NumberFormat.getNumberInstance().format(statistics.activeDriverNumber),
            DriveKitUI.colors.complementaryFontColor(),
            DKStyle.NORMAL_TEXT
        )
        space()
        append(
            context,
            context.getString(R.string.dk_driverdata_mysynthesis_drivers),
            DriveKitUI.colors.complementaryFontColor(),
            DKStyle.SMALL_TEXT
        )
    }.toSpannable()

    private fun getTripsCountText(context: Context, tripsCount: Int): SpannableString {
        val spannable = DKSpannable()
        val tripsString = context.resources.getQuantityString(R.plurals.trip_plural, tripsCount)
        if (tripsCount == 0) {
            spannable.append(
                context,
                context.getString(R.string.dk_common_no_trip),
                DriveKitUI.colors.complementaryFontColor(),
                DKStyle.SMALL_TEXT
            )
        } else {
            spannable.append(
                context,
                NumberFormat.getNumberInstance().format(tripsCount),
                DriveKitUI.colors.complementaryFontColor(),
                DKStyle.NORMAL_TEXT
            ).space().append(
                context, tripsString,
                DriveKitUI.colors.complementaryFontColor(),
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
            minDistanceToRemoveFractions = 10.0
        ).forEach {
            when (it) {
                is FormatType.VALUE -> spannable.append(context, it.value, DriveKitUI.colors.complementaryFontColor(), DKStyle.NORMAL_TEXT)
                is FormatType.UNIT -> spannable.append(context, it.value, DriveKitUI.colors.complementaryFontColor(), DKStyle.SMALL_TEXT)
                is FormatType.SEPARATOR -> spannable.space()
            }
        }
        return spannable.toSpannable()
    }
}
