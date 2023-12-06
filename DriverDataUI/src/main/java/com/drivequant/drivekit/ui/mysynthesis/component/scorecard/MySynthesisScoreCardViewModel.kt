package com.drivequant.drivekit.ui.mysynthesis.component.scorecard

import android.content.Context
import android.text.SpannableString
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.graphical.DKStyle
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.extension.CalendarField
import com.drivequant.drivekit.core.extension.getValue
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.databaseutils.entity.DKPeriod
import com.drivequant.drivekit.driverdata.timeline.DKScoreEvolutionTrend
import com.drivequant.drivekit.driverdata.timeline.DKScoreSynthesis
import com.drivequant.drivekit.ui.R
import java.util.Date

internal class MySynthesisScoreCardViewModel : ViewModel() {

    var onViewModelUpdated: (() -> Unit)? = null

    private lateinit var selectedScore: DKScoreType
    private var selectedPeriod: DKPeriod = DKPeriod.WEEK
    private var scoreSynthesis: DKScoreSynthesis? = null
    private var previousPeriodDate: Date? = null
    private var hasOnlyShortTripsForCurrentPeriod: Boolean = false
    private var hasOnlyShortTripsForPreviousPeriod: Boolean = false

    private val hasCurrentScore: Boolean
        get() = this.scoreSynthesis?.scoreValue != null

    private val hasPreviousScore: Boolean
        get() = this.scoreSynthesis?.previousScoreValue != null

    val score: Double?
        get() = scoreSynthesis?.scoreValue
    val previousScore: Double?
        get() = scoreSynthesis?.previousScoreValue

    fun configure(
        score: DKScoreType,
        period: DKPeriod,
        scoreSynthesis: DKScoreSynthesis?,
        hasOnlyShortTripsForCurrentPeriod: Boolean = false,
        hasOnlyShortTripsForPreviousPeriod: Boolean = false,
        previousPeriodDate: Date?
    ) {
        this.selectedScore = score
        this.selectedPeriod = period
        this.scoreSynthesis = scoreSynthesis
        this.previousPeriodDate = previousPeriodDate
        this.hasOnlyShortTripsForCurrentPeriod = hasOnlyShortTripsForCurrentPeriod
        this.hasOnlyShortTripsForPreviousPeriod = hasOnlyShortTripsForPreviousPeriod
        this.onViewModelUpdated?.invoke()
    }

    @StringRes
    fun getCardTitleResId() = when (this.selectedScore) {
        DKScoreType.SAFETY -> R.string.dk_driverdata_mysynthesis_safety_score
        DKScoreType.ECO_DRIVING -> R.string.dk_driverdata_mysynthesis_ecodriving_score
        DKScoreType.DISTRACTION -> R.string.dk_driverdata_mysynthesis_distraction_score
        DKScoreType.SPEEDING -> R.string.dk_driverdata_mysynthesis_speeding_score
    }

    fun getEvolutionText(context: Context): SpannableString {
        val evolutionTextPrefix: String? = getEvolutionPrefixText(context)
        val dkSpannable = DKSpannable()
        if (evolutionTextPrefix != null) {
            dkSpannable.append(
                context,
                evolutionTextPrefix.toString(),
                DriveKitUI.colors.complementaryFontColor(),
                DKStyle.SMALL_TEXT
            )
        }

        return if (scoreSynthesis?.previousScoreValue == null){
            dkSpannable.toSpannable()
        } else {
            val score = computeScoreOutOfTen(context, scoreSynthesis?.previousScoreValue)
            dkSpannable.space().append(context, score, DriveKitUI.colors.complementaryFontColor(), DKStyle.HEADLINE2).toSpannable()
        }
    }

    fun computeScoreOutOfTen(context: Context, score: Double?): String =
        (score?.format(1) ?: "-")
            .plus(" ")
            .plus(context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_unit_score))

    private fun getEvolutionPrefixText(context: Context): String? {
        val previousPeriod: TripKind = if (hasPreviousScore) TripKind.SCORED_TRIPS else {
            if (hasOnlyShortTripsForPreviousPeriod) TripKind.ONLY_SHORT_TRIPS else TripKind.NO_TRIP
        }
        val currentPeriod: TripKind = if (hasCurrentScore) TripKind.SCORED_TRIPS else {
            if (hasOnlyShortTripsForCurrentPeriod) TripKind.ONLY_SHORT_TRIPS else TripKind.NO_TRIP
        }

        if (hasNoTripAtAll(previousPeriod, currentPeriod)) {
            return context.getString(R.string.dk_driverdata_mysynthesis_no_trip_at_all)
        } else if (hasNoPreviousTrip(previousPeriod, currentPeriod)) {
            return when (this.selectedPeriod) {
                DKPeriod.WEEK -> R.string.dk_driverdata_mysynthesis_no_trip_prev_week
                DKPeriod.MONTH -> R.string.dk_driverdata_mysynthesis_no_trip_prev_month
                DKPeriod.YEAR -> R.string.dk_driverdata_mysynthesis_no_trip_prev_year
            }.let { context.getString(it) }
        } else if (hasPreviousTrip(previousPeriod, currentPeriod)) {
            return when (this.selectedPeriod) {
                DKPeriod.WEEK -> context.getString(R.string.dk_driverdata_mysynthesis_previous_week)
                DKPeriod.MONTH -> context.getString(R.string.dk_driverdata_mysynthesis_previous_month)
                DKPeriod.YEAR -> String.format(
                    context.getString(R.string.dk_driverdata_mysynthesis_previous_year),
                    previousPeriodDate?.getValue(CalendarField.YEAR)
                )
            }
        }
        return null
    }

    @DrawableRes
    fun getTrendIconResId(): Int {
        scoreSynthesis?.evolutionTrend?.let { trend ->
            return when (trend) {
                DKScoreEvolutionTrend.UP -> R.drawable.dk_driver_data_trend_positive
                DKScoreEvolutionTrend.DOWN -> R.drawable.dk_driver_data_trend_negative
                DKScoreEvolutionTrend.STABLE -> R.drawable.dk_driver_data_trend_steady
            }
        }
        return R.drawable.dk_driver_data_trend_steady
    }

    private enum class TripKind {
        NO_TRIP, ONLY_SHORT_TRIPS, SCORED_TRIPS
    }

    private fun hasNoTripAtAll(previousPeriod: TripKind, currentPeriod: TripKind) =
        (previousPeriod == TripKind.NO_TRIP && currentPeriod == TripKind.NO_TRIP) ||
                (previousPeriod == TripKind.ONLY_SHORT_TRIPS && currentPeriod == TripKind.ONLY_SHORT_TRIPS) ||
                (previousPeriod == TripKind.ONLY_SHORT_TRIPS && currentPeriod == TripKind.SCORED_TRIPS) // TODO impossible case before

    // Can be simplified with previousPeriod == TripKind.NO_TRIP
    private fun hasNoPreviousTrip(previousPeriod: TripKind, currentPeriod: TripKind) =
        (previousPeriod == TripKind.NO_TRIP && currentPeriod == TripKind.SCORED_TRIPS) ||
                (previousPeriod == TripKind.NO_TRIP && currentPeriod == TripKind.ONLY_SHORT_TRIPS)

    // Can be simplified with previousPeriod == TripKind.SCORED_TRIPS
    private fun hasPreviousTrip(previousPeriod: TripKind, currentPeriod: TripKind) =
        (previousPeriod == TripKind.SCORED_TRIPS && currentPeriod == TripKind.SCORED_TRIPS) ||
                (previousPeriod == TripKind.SCORED_TRIPS && currentPeriod == TripKind.ONLY_SHORT_TRIPS)
}
