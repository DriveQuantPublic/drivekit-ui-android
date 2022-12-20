package com.drivequant.drivekit.timeline.ui.component.graph

import android.content.Context
import com.drivequant.drivekit.common.ui.extension.ceiledToValueDivisibleBy10
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.timeline.ui.DKTimelineScoreType
import kotlin.math.ceil
import kotlin.math.min

internal sealed class GraphItem {
    data class Score(val scoreType: DKTimelineScoreType) : GraphItem()
    data class ScoreItem(val scoreItemType: TimelineScoreItemType) : GraphItem()

    val graphType: GraphType
        get() {
            return when (this) {
                is Score -> GraphType.LINE
                is ScoreItem -> getGraphType(this.scoreItemType)
            }
        }

    val graphTitleKey: String
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
        val internalCountBetweenBounds = (ceil(maxValue) - graphMinValue).toInt()
        // Add one because we need X intervals so X+1 values
        return min(internalCountBetweenBounds, GraphConstants.defaultNumberOfIntervalInYAxis) + 1
    }

    fun getGraphMaxValue(realMaxValue: Double?): Double {
        this.defaultGraphMaxValue?.let {
            return it
        } ?: run {
            val maxValue = realMaxValue ?: GraphConstants.defaultMaxValueInYAxis.toDouble()
            if (maxValue <= GraphConstants.notEnoughDataInGraphThreshold) {
                return GraphConstants.maxValueInYAxisWhenNotEnoughDataInGraph.toDouble()
            } else {
                return maxValue.ceiledToValueDivisibleBy10()
            }
        }
    }

    private val defaultGraphMaxValue: Double?
        get() {
            return when (this) {
                is Score -> when (scoreType) {
                    DKTimelineScoreType.SAFETY,
                    DKTimelineScoreType.ECO_DRIVING,
                    DKTimelineScoreType.DISTRACTION,
                    DKTimelineScoreType.SPEEDING -> 10.0
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
            TimelineScoreItemType.ECODRIVING_FUEL_VOLUME -> DKDataFormatter.formatLiter(context, value)
            TimelineScoreItemType.ECODRIVING_FUEL_SAVINGS -> DKDataFormatter.formatConsumption(context, value).convertToString()
            TimelineScoreItemType.ECODRIVING_CO2MASS -> DKDataFormatter.formatCO2Mass(context, value)
            TimelineScoreItemType.DISTRACTION_UNLOCK -> value.format(1)
            TimelineScoreItemType.DISTRACTION_CALL_FORBIDDEN_DURATION -> DKDataFormatter.formatDuration(context, value).convertToString()
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

    private fun getGraphTitleKey(scoreType: DKTimelineScoreType): String {
        return when (scoreType) {
            DKTimelineScoreType.SAFETY -> "dk_timeline_safety_score"
            DKTimelineScoreType.ECO_DRIVING -> "dk_timeline_eco_score"
            DKTimelineScoreType.DISTRACTION -> "dk_timeline_distraction_score"
            DKTimelineScoreType.SPEEDING -> "dk_timeline_speeding_score"
        }
    }

    private fun getGraphTitleKey(scoreItemType: TimelineScoreItemType): String {
        return when (scoreItemType) {
            TimelineScoreItemType.SAFETY_ACCELERATION -> "dk_timeline_accelerations"
            TimelineScoreItemType.SAFETY_BRAKING -> "dk_timeline_brakings"
            TimelineScoreItemType.SAFETY_ADHERENCE -> "dk_timeline_adherence"
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_ACCELERATION -> "dk_timeline_acceleration_score"
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_BRAKE -> "dk_timeline_deceleration_score"
            TimelineScoreItemType.ECODRIVING_EFFICIENCY_SPEED_MAINTAIN -> "dk_timeline_maintain_score"
            TimelineScoreItemType.ECODRIVING_FUEL_VOLUME -> "dk_timeline_consumption"
            TimelineScoreItemType.ECODRIVING_FUEL_SAVINGS -> "dk_timeline_fuel_savings"
            TimelineScoreItemType.ECODRIVING_CO2MASS -> "dk_timeline_co2_mass"
            TimelineScoreItemType.DISTRACTION_UNLOCK -> "dk_timeline_nb_unlocks"
            TimelineScoreItemType.DISTRACTION_CALL_FORBIDDEN_DURATION -> "dk_timeline_calls_duration"
            TimelineScoreItemType.DISTRACTION_PERCENTAGE_OF_TRIPS_WITH_FORBIDDEN_CALL -> "dk_timeline_trips_forbidden_calls"
            TimelineScoreItemType.SPEEDING_DURATION -> "dk_timeline_overspeeding_duration"
            TimelineScoreItemType.SPEEDING_DISTANCE -> "dk_timeline_overspeeding_distance"
        }
    }

    private fun getGraphMinValue(scoreType: DKTimelineScoreType): Double {
        return when (scoreType) {
            DKTimelineScoreType.SAFETY -> 3.0
            DKTimelineScoreType.ECO_DRIVING -> 6.0
            DKTimelineScoreType.DISTRACTION -> 0.0
            DKTimelineScoreType.SPEEDING -> 0.0
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
