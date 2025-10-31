package com.drivequant.drivekit.timeline.ui.component.graph

import android.content.Context
import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.utils.Co2Unit
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKUnitSystem
import com.drivequant.drivekit.common.ui.utils.Liter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.core.scoreslevels.DKScoreType
import com.drivequant.drivekit.timeline.ui.R
import com.drivequant.drivekit.timeline.ui.ceilToLowestValueWithNiceStep
import kotlin.math.ceil
import kotlin.math.min

internal sealed class GraphItem {
    data class Score(val scoreType: DKScoreType) : GraphItem()
    data class ScoreItem(val scoreItemType: TimelineScoreItemType) : GraphItem()

    val graphType: GraphType
        get() {
            return when (this) {
                is Score -> GraphType.LINE
                is ScoreItem -> getGraphType(this.scoreItemType)
            }
        }

    @get:StringRes
    val graphTitleKey: Int
        get() {
            return when (this) {
                is Score -> getGraphTitleKey(this.scoreType)
                is ScoreItem -> getGraphTitleKey(this.scoreItemType)
            }
        }

    val graphMinValue: Double
        get() {
            return when (this) {
                is Score -> getGraphMinValue(this.scoreType)
                is ScoreItem -> getGraphMinValue(this.scoreItemType)
            }
        }

    fun maxNumberOfLabels(maxValue: Double): Int {
        val intervalCountBetweenBounds = (ceil(maxValue) - this.graphMinValue).toInt()
        // Add one because we need X intervals so X+1 values
        return min(intervalCountBetweenBounds, GraphConstants.DEFAULT_NUMBER_OF_INTERVAL_IN_Y_AXIS) + 1
    }

    fun getGraphMaxValue(realMaxValue: Double?): Double {
        this.defaultGraphMaxValue?.let {
            return it
        } ?: run {
            val maxValue = realMaxValue ?: GraphConstants.DEFAULT_MAX_VALUE_IN_Y_AXIS.toDouble()
            if (maxValue <= GraphConstants.NOT_ENOUGH_DATA_IN_GRAPH_THRESHOLD) {
                return GraphConstants.MAX_VALUE_IN_Y_AXIS_WHEN_NOT_ENOUGH_DATA_IN_GRAPH
            } else {
                return maxValue.ceilToLowestValueWithNiceStep()
            }
        }
    }

    private val defaultGraphMaxValue: Double?
        get() {
            return when (this) {
                is Score -> when (scoreType) {
                    DKScoreType.SAFETY,
                    DKScoreType.ECO_DRIVING,
                    DKScoreType.DISTRACTION,
                    DKScoreType.SPEEDING -> 10.0
                }
                is ScoreItem -> when (scoreItemType) {
                    TimelineScoreItemType.SAFETY_ACCELERATION -> null
                    TimelineScoreItemType.SAFETY_BRAKING -> null
                    TimelineScoreItemType.SAFETY_ADHERENCE -> null
                    TimelineScoreItemType.ECODRIVING_EFFICIENCY_ACCELERATION -> 5.0
                    TimelineScoreItemType.ECODRIVING_EFFICIENCY_BRAKE -> 5.0
                    TimelineScoreItemType.ECODRIVING_EFFICIENCY_SPEED_MAINTAIN -> 5.0
                    TimelineScoreItemType.ECODRIVING_FUEL_VOLUME -> null
                    TimelineScoreItemType.ECODRIVING_FUEL_SAVINGS -> null
                    TimelineScoreItemType.ECODRIVING_CO2MASS -> null
                    TimelineScoreItemType.DISTRACTION_UNLOCK -> null
                    TimelineScoreItemType.DISTRACTION_CALL_FORBIDDEN_DURATION -> null
                    TimelineScoreItemType.DISTRACTION_PERCENTAGE_OF_TRIPS_WITH_FORBIDDEN_CALL -> null
                    TimelineScoreItemType.SPEEDING_DURATION -> 100.0
                    TimelineScoreItemType.SPEEDING_DISTANCE -> 100.0
                }
            }
        }

    fun getGraphDescription(context: Context, value: Double?): String {
        return if (value == null) {
            "-"
        } else {
            when (this) {
                is Score -> DKDataFormatter.formatScore(context, value)
                is ScoreItem -> getGraphDescription(context, value, this.scoreItemType)
            }
        }
    }

    private fun getGraphDescription(context: Context, value: Double, scoreItemType: TimelineScoreItemType): String {
        return when (scoreItemType) {
            TimelineScoreItemType.SAFETY_ACCELERATION -> value.format(1)
            TimelineScoreItemType.SAFETY_BRAKING -> value.format(1)
            TimelineScoreItemType.SAFETY_ADHERENCE -> value.format(1)
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_ACCELERATION -> context.getString(DKDataFormatter.getAccelerationDescriptionKey(value))
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_BRAKE -> context.getString(DKDataFormatter.getDecelerationDescriptionKey(value))
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_SPEED_MAINTAIN -> context.getString(DKDataFormatter.getSpeedMaintainDescription(value))
            TimelineScoreItemType.ECODRIVING_FUEL_VOLUME -> DKDataFormatter.formatVolume(context, Liter(value))
            TimelineScoreItemType.ECODRIVING_CO2MASS -> DKDataFormatter.formatCO2Mass(context, value, Co2Unit.KILOGRAM, Co2Unit.KILOGRAM)
            TimelineScoreItemType.ECODRIVING_FUEL_SAVINGS -> DKDataFormatter.formatVolume(context, (Liter(value)))
            TimelineScoreItemType.DISTRACTION_UNLOCK -> value.format(1)
            TimelineScoreItemType.DISTRACTION_CALL_FORBIDDEN_DURATION -> {
                // The value is in minute so we convert it back to seconds before reformatting. The conversion is done here to keep the graph Y Axis in minute
                DKDataFormatter.formatDuration(context, value * 60).convertToString()
            }
            TimelineScoreItemType.DISTRACTION_PERCENTAGE_OF_TRIPS_WITH_FORBIDDEN_CALL -> DKDataFormatter.formatPercentage(value)
            TimelineScoreItemType.SPEEDING_DURATION -> DKDataFormatter.formatPercentage(value)
            TimelineScoreItemType.SPEEDING_DISTANCE -> DKDataFormatter.formatPercentage(value)
        }
    }

    private fun getGraphType(scoreItemType: TimelineScoreItemType): GraphType {
        return when (scoreItemType) {
            TimelineScoreItemType.SAFETY_ACCELERATION -> GraphType.BAR
            TimelineScoreItemType.SAFETY_BRAKING -> GraphType.BAR
            TimelineScoreItemType.SAFETY_ADHERENCE -> GraphType.BAR
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_ACCELERATION -> GraphType.LINE
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_BRAKE -> GraphType.LINE
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_SPEED_MAINTAIN -> GraphType.LINE
            TimelineScoreItemType.ECODRIVING_FUEL_VOLUME -> GraphType.BAR
            TimelineScoreItemType.ECODRIVING_FUEL_SAVINGS -> GraphType.BAR
            TimelineScoreItemType.ECODRIVING_CO2MASS -> GraphType.BAR
            TimelineScoreItemType.DISTRACTION_UNLOCK -> GraphType.BAR
            TimelineScoreItemType.DISTRACTION_CALL_FORBIDDEN_DURATION -> GraphType.BAR
            TimelineScoreItemType.DISTRACTION_PERCENTAGE_OF_TRIPS_WITH_FORBIDDEN_CALL -> GraphType.BAR
            TimelineScoreItemType.SPEEDING_DURATION -> GraphType.BAR
            TimelineScoreItemType.SPEEDING_DISTANCE -> GraphType.BAR
        }
    }

    @StringRes
    private fun getGraphTitleKey(scoreType: DKScoreType): Int {
        return when (scoreType) {
            DKScoreType.SAFETY -> R.string.dk_timeline_safety_score
            DKScoreType.ECO_DRIVING -> R.string.dk_timeline_eco_score
            DKScoreType.DISTRACTION -> R.string.dk_timeline_distraction_score
            DKScoreType.SPEEDING -> R.string.dk_timeline_speeding_score
        }
    }

    @StringRes
    private fun getGraphTitleKey(scoreItemType: TimelineScoreItemType): Int {
        val isMetric = DriveKitUI.unitSystem == DKUnitSystem.METRIC

        return when (scoreItemType) {
            TimelineScoreItemType.SAFETY_ACCELERATION -> if (isMetric) R.string.dk_timeline_accelerations else R.string.dk_timeline_accelerations_miles
            TimelineScoreItemType.SAFETY_BRAKING -> if (isMetric) R.string.dk_timeline_brakings else R.string.dk_timeline_brakings_miles
            TimelineScoreItemType.SAFETY_ADHERENCE -> if (isMetric) R.string.dk_timeline_adherence else R.string.dk_timeline_adherence_miles
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_ACCELERATION -> R.string.dk_timeline_acceleration_score
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_BRAKE -> R.string.dk_timeline_deceleration_score
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_SPEED_MAINTAIN -> R.string.dk_timeline_maintain_score
            TimelineScoreItemType.ECODRIVING_FUEL_VOLUME -> R.string.dk_timeline_consumption
            TimelineScoreItemType.ECODRIVING_FUEL_SAVINGS -> R.string.dk_timeline_fuel_savings
            TimelineScoreItemType.ECODRIVING_CO2MASS -> R.string.dk_timeline_co2_mass
            TimelineScoreItemType.DISTRACTION_UNLOCK -> if (isMetric) R.string.dk_timeline_nb_unlocks else R.string.dk_timeline_nb_unlocks_miles
            TimelineScoreItemType.DISTRACTION_CALL_FORBIDDEN_DURATION -> if (isMetric) R.string.dk_timeline_calls_duration else R.string.dk_timeline_calls_duration_miles
            TimelineScoreItemType.DISTRACTION_PERCENTAGE_OF_TRIPS_WITH_FORBIDDEN_CALL -> R.string.dk_timeline_trips_forbidden_calls
            TimelineScoreItemType.SPEEDING_DURATION -> R.string.dk_timeline_overspeeding_duration
            TimelineScoreItemType.SPEEDING_DISTANCE -> R.string.dk_timeline_overspeeding_distance
        }
    }

    private fun getGraphMinValue(scoreType: DKScoreType): Double {
        return when (scoreType) {
            DKScoreType.SAFETY -> 3.0
            DKScoreType.ECO_DRIVING -> 6.0
            DKScoreType.DISTRACTION -> 0.0
            DKScoreType.SPEEDING -> 0.0
        }
    }

    private fun getGraphMinValue(scoreItemType: TimelineScoreItemType): Double {
        return when (scoreItemType) {
            TimelineScoreItemType.SAFETY_ACCELERATION -> 0.0
            TimelineScoreItemType.SAFETY_BRAKING -> 0.0
            TimelineScoreItemType.SAFETY_ADHERENCE -> 0.0
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_ACCELERATION -> -5.0
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_BRAKE -> -5.0
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_SPEED_MAINTAIN -> 0.0
            TimelineScoreItemType.ECODRIVING_FUEL_VOLUME -> 0.0
            TimelineScoreItemType.ECODRIVING_FUEL_SAVINGS -> 0.0
            TimelineScoreItemType.ECODRIVING_CO2MASS -> 0.0
            TimelineScoreItemType.DISTRACTION_UNLOCK -> 0.0
            TimelineScoreItemType.DISTRACTION_CALL_FORBIDDEN_DURATION -> 0.0
            TimelineScoreItemType.DISTRACTION_PERCENTAGE_OF_TRIPS_WITH_FORBIDDEN_CALL -> 0.0
            TimelineScoreItemType.SPEEDING_DURATION -> 0.0
            TimelineScoreItemType.SPEEDING_DISTANCE -> 0.0
        }
    }
}
