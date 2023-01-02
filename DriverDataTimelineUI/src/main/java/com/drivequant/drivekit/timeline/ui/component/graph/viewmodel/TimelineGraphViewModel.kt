package com.drivequant.drivekit.timeline.ui.component.graph.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.databaseutils.entity.Timeline
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.common.ui.component.DKScoreType
import com.drivequant.drivekit.timeline.ui.component.graph.*
import com.drivequant.drivekit.timeline.ui.component.graph.GraphAxisConfig
import com.drivequant.drivekit.timeline.ui.component.graph.GraphItem
import com.drivequant.drivekit.timeline.ui.component.graph.GraphPoint
import com.drivequant.drivekit.timeline.ui.component.graph.GraphType
import com.drivequant.drivekit.timeline.ui.component.graph.view.GraphViewListener
import com.drivequant.drivekit.timeline.ui.toTimelineDate
import java.util.Collections.max
import java.util.Date

internal class TimelineGraphViewModel : ViewModel(), GraphViewModel, GraphViewListener {
    var listener: TimelineGraphListener? = null
    override var graphViewModelDidUpdate: (() -> Unit)? = null
    override var type: GraphType = GraphType.LINE
    override var points: List<GraphPoint?> = emptyList()
    override var selectedIndex: Int? = null
    override var xAxisConfig: GraphAxisConfig? = null
    override var yAxisConfig: GraphAxisConfig? = null
    var titleKey: String = ""
        private set
    var description: String = ""
        private set
    private var sourceDates: List<Date>? = null
    private var timelineSelectedIndex: Int? = null
    private var indexOfFirstPointInTimeline: Int? = null
    private var indexOfLastPointInTimeline: Int? = null

    fun configure(context: Context, timeline: Timeline, timelineSelectedIndex: Int, graphItem: GraphItem, period: DKTimelinePeriod) {
        val sourceDates = timeline.allContext.date.map { it.toTimelineDate()!! }
        val dates: List<Date> = sourceDates.map { it.removeTime() }
        val calendarField = getCalendarField(period)
        val graphPointNumber = GraphConstants.GRAPH_POINT_NUMBER
        val selectedDate = dates[timelineSelectedIndex]
        val now = Date()
        val currentDate = now.startingFrom(calendarField)
        val delta = selectedDate.diffWith(currentDate.removeTime(), calendarField).toInt()
        this.sourceDates = sourceDates
        this.timelineSelectedIndex = timelineSelectedIndex
        val selectedIndexInGraph = (graphPointNumber - 1) - ((-delta) % graphPointNumber)
        val graphStartDate = selectedDate.add(-selectedIndexInGraph, calendarField)
        val graphDates:List<String> = getGraphLabels(graphStartDate, calendarField, period, graphPointNumber)
        val graphPoints: MutableList<GraphPoint?> = mutableListOf()
        for (i in 0 until graphPointNumber) {
            val xLabelDate: Date = graphStartDate.add(i, calendarField)
            var point: GraphPoint? = null
            val shouldInterpolate = graphItem.graphType == GraphType.LINE
            val xLabelDateIndex = dates.indexOf(xLabelDate)
            if (xLabelDateIndex != -1) {
                val value = getValue(xLabelDateIndex, graphItem, timeline)
                point = if (value != null) {
                    GraphPoint(i.toDouble(), value, PointData(sourceDates[xLabelDateIndex], false))
                } else {
                    getInterpolatedSelectableDateWithoutValue(xLabelDateIndex, graphItem, timeline, dates, calendarField, i.toDouble(), sourceDates )
                }
            } else if (shouldInterpolate) {
                if (i == 0) {
                    point = getInterpolatedStartOfGraphPoint(graphStartDate, calendarField, dates, graphItem, timeline, xLabelDate)
                } else if (i == graphPointNumber - 1) {
                    point = getInterpolatedEndOfGraphPoint(graphStartDate, calendarField, dates, graphItem, timeline, xLabelDate)
                }
            }
            graphPoints.add(point)
            computeIndexOfFirstAndLastPointInTimeline(graphPoints)
        }
        configure(graphItem, graphPointNumber, graphPoints, graphDates, selectedIndexInGraph, graphItem.getGraphDescription(context, getValue(timelineSelectedIndex, graphItem, timeline)))
    }

    fun showEmptyGraph(graphItem: GraphItem, period: DKTimelinePeriod) {
        val calendarField = getCalendarField(period)
        val graphPointNumber = GraphConstants.GRAPH_POINT_NUMBER
        val now = Date()
        val currentDate = now.startingFrom(calendarField)
        val startDate = currentDate.add(-graphPointNumber + 1, calendarField)
        val graphDates: List<String> = getGraphLabels(startDate, calendarField, period, graphPointNumber)
        val graphPoints: List<GraphPoint?> = listOf(GraphPoint(0.0, 10.0, null))
        configure(graphItem, graphPointNumber, graphPoints, graphDates, null, "-")
    }

    fun showPreviousGraphData() {
       this.listener?.let { listener ->
           this.sourceDates?.let { sourceDates ->
               this.indexOfFirstPointInTimeline?.let { indexOfFirstPointInTimeline ->
                   if (indexOfFirstPointInTimeline > 0) {
                       listener.onSelectDate(sourceDates[indexOfFirstPointInTimeline - 1])
                   } else {
                       this.timelineSelectedIndex?.let { timelineSelectedIndex ->
                           if (timelineSelectedIndex != indexOfFirstPointInTimeline) {
                               listener.onSelectDate(sourceDates[indexOfFirstPointInTimeline])
                           }
                       }
                   }
               }
           }
       }
    }

    fun showNextGraphData() {
        this.listener?.let { listener ->
            this.sourceDates?.let { sourceDates ->
                this.indexOfLastPointInTimeline?.let { indexOfLastPointInTimeline ->
                    if (indexOfLastPointInTimeline < sourceDates.size - 1) {
                        listener.onSelectDate(sourceDates[indexOfLastPointInTimeline + 1])
                    } else {
                        this.timelineSelectedIndex?.let { timelineSelectedIndex ->
                            if (timelineSelectedIndex != indexOfLastPointInTimeline) {
                                listener.onSelectDate(sourceDates[indexOfLastPointInTimeline])
                            }
                        }
                    }
                }
            }
        }
    }

    private fun configure(graphItem: GraphItem, graphPointNumber: Int, graphPoints: List<GraphPoint?>, graphDates: List<String>, selectedIndex: Int?, graphDescription: String) {
        this.type = graphItem.graphType
        this.points = graphPoints
        this.selectedIndex = selectedIndex
        this.xAxisConfig = GraphAxisConfig(0.0, (graphPointNumber - 1).toDouble(), LabelType.CustomLabels(graphDates))
        val minYValue = graphItem.graphMinValue
        val maxYValue = graphItem.getGraphMaxValue(max(graphPoints.map { it?.y ?: 0.0 }))
        val labels: MutableList<String> = mutableListOf()
        for (i in minYValue.toInt()..10) {
            labels.add(i.toString())
        }
        this.yAxisConfig = GraphAxisConfig(graphItem.graphMinValue, maxYValue, LabelType.RawValues(graphItem.maxNumberOfLabels(maxYValue)))
        this.titleKey = graphItem.graphTitleKey
        this.description = graphDescription
        this.graphViewModelDidUpdate?.let { it() }
    }

    private fun getCalendarField(period: DKTimelinePeriod): CalendarField = when (period) {
        DKTimelinePeriod.WEEK -> CalendarField.WEEK
        DKTimelinePeriod.MONTH -> CalendarField.MONTH
    }

    private fun getDateFormatPattern(period: DKTimelinePeriod): DKDatePattern {
        return when (period) {
            DKTimelinePeriod.WEEK -> DKDatePattern.DAY_MONTH
            DKTimelinePeriod.MONTH -> DKDatePattern.MONTH_ABBREVIATION
        }
    }

    private fun getGraphLabels(
        startDate: Date,
        calendarField: CalendarField,
        period: DKTimelinePeriod,
        graphPointNumber: Int
    ): List<String> {
        val graphDates: MutableList<String> = mutableListOf()
        for (i in 0 until graphPointNumber) {
            val date: Date = startDate.add(i, calendarField)
            val dateString = date.formatDate(getDateFormatPattern(period))
            graphDates.add(dateString)
        }
        return graphDates
    }

    private fun getPreviousIndexWithValue(startIndex: Int, hasValueAtIndex: (Int) -> Boolean): Int? {
        var currentIndex = startIndex
        while (currentIndex > 0) {
            currentIndex -= 1
            if (hasValueAtIndex(currentIndex)) {
                return currentIndex
            }
        }
        return null
    }

    private fun getNextIndexWithValue(startIndex: Int, maxIndex: Int, hasValueAtIndex: (Int) -> Boolean): Int? {
        var currentIndex = startIndex
        while (currentIndex < maxIndex - 1) {
            currentIndex += 1
            if (hasValueAtIndex(currentIndex)) {
                return currentIndex
            }
        }
        return null
    }

    private fun getInterpolatedStartOfGraphPoint(graphStartDate: Date, calendarField: CalendarField, dates: List<Date>, graphItem: GraphItem, timeline: Timeline, xLabelDate: Date): GraphPoint? {
        // Find next valid index
        var point: GraphPoint? = null
        var nextValidIndex: Int = -1
        var i = 0
        var hasMoreKnownDates = true
        val lastDate = dates.last()
        while (nextValidIndex < 0 && hasMoreKnownDates) {
            i += 1
            val nextValidDate = graphStartDate.add(i, calendarField)
            nextValidIndex = dates.indexOf(nextValidDate)
            if (nextValidIndex >= 0) {
                if (getValue(nextValidIndex, graphItem, timeline) == null) {
                    nextValidIndex = -1
                }
            }
            hasMoreKnownDates = nextValidDate <= lastDate
        }
        if (nextValidIndex > 0) {
            var previousValidIndex: Int = nextValidIndex - 1
            while (previousValidIndex >= 0 && getValue(previousValidIndex, graphItem, timeline) == null) {
                previousValidIndex -= 1
            }
            if (previousValidIndex >= 0) {
                val interpolatedValue = getInterpolatedValue(xLabelDate, previousValidIndex, nextValidIndex, dates, calendarField, graphItem, timeline)
                if (interpolatedValue != null) {
                    point = GraphPoint(0.0, interpolatedValue, null)
                }
            }
        }
        return point
    }

    private fun getInterpolatedEndOfGraphPoint(graphStartDate: Date, calendarField: CalendarField, dates: List<Date>, graphItem: GraphItem, timeline: Timeline, xLabelDate: Date): GraphPoint? {
        // Find previous valid index
        var point: GraphPoint? = null
        var previousValidIndex: Int = -1
        var i = GraphConstants.GRAPH_POINT_NUMBER - 1
        var hasMoreKnownDates = true
        val firstDate = dates.first()
        while (previousValidIndex < 0 && hasMoreKnownDates) {
            i -= 1
            val previousValidDate = graphStartDate.add(i, calendarField)
            previousValidIndex = dates.indexOf(previousValidDate)
            if (previousValidIndex >= 0) {
                if (getValue(previousValidIndex, graphItem, timeline) == null) {
                    previousValidIndex = -1
                }
            }
            hasMoreKnownDates = previousValidDate >= firstDate
        }
        val max = dates.size
        if (previousValidIndex >= 0 && previousValidIndex < max - 1) {
            var nextValidIndex: Int = previousValidIndex + 1
            while (nextValidIndex < max && getValue(nextValidIndex, graphItem, timeline) == null) {
                nextValidIndex += 1
            }
            if (nextValidIndex < max) {
                val interpolatedValue = getInterpolatedValue(xLabelDate, previousValidIndex, nextValidIndex, dates, calendarField, graphItem, timeline)
                if (interpolatedValue != null)  {
                    point = GraphPoint((GraphConstants.GRAPH_POINT_NUMBER - 1).toDouble(), interpolatedValue, null)
                }
            }
        }
        return point
    }

    private fun getInterpolatedSelectableDateWithoutValue(dateIndex: Int, graphItem: GraphItem, timeline: Timeline, dates: List<Date>, calendarField: CalendarField, pointX: Double, sourceDates: List<Date>): GraphPoint? {
        val point: GraphPoint?
        if (dates.size == 1) {
            point = GraphPoint(pointX, 0.0, null)
        } else {
            val previousIndexWithValue = getPreviousIndexWithValue(dateIndex) { index ->
                getValue(index, graphItem, timeline) != null
            }
            val nextIndexWithValue = getNextIndexWithValue(dateIndex, dates.size) { index ->
                    getValue(index, graphItem, timeline) != null
            }
            point = if (previousIndexWithValue != null && nextIndexWithValue != null) {
                val interpolatedValue = getInterpolatedValue(dates[dateIndex], previousIndexWithValue, nextIndexWithValue, dates, calendarField, graphItem, timeline)
                if (interpolatedValue != null) {
                    GraphPoint(pointX, interpolatedValue, PointData(sourceDates[dateIndex], true))
                } else {
                    null
                }
            } else {
                null
            }
        }
        return point
    }

    private fun computeIndexOfFirstAndLastPointInTimeline(graphPoints: List<GraphPoint?>) {
        this.indexOfFirstPointInTimeline = null
        this.indexOfLastPointInTimeline = null
        this.sourceDates?.let { sourceDates ->
            for (graphPoint in graphPoints) {
                graphPoint?.data?.let { data ->
                    if (!data.interpolatedPoint && this.indexOfFirstPointInTimeline == null) {
                        this.indexOfFirstPointInTimeline = sourceDates.indexOf(data.date)
                    }
                    this.indexOfLastPointInTimeline = sourceDates.indexOf(data.date)
                }
            }
        }
    }

    private fun getValue(index: Int, graphItem: GraphItem, timeline: Timeline): Double? {
        return when (graphItem) {
            is GraphItem.Score -> when (graphItem.scoreType) {
                DKScoreType.SAFETY -> if (timeline.allContext.numberTripScored[index] > 0) timeline.allContext.safety[index] else null
                DKScoreType.ECO_DRIVING -> if (timeline.allContext.numberTripScored[index] > 0) timeline.allContext.efficiency[index] else null
                DKScoreType.DISTRACTION -> timeline.allContext.phoneDistraction[index]
                DKScoreType.SPEEDING -> timeline.allContext.speeding[index]
            }
            is GraphItem.ScoreItem -> when (graphItem.scoreItemType) {
                TimelineScoreItemType.SAFETY_ACCELERATION -> timeline.allContext.acceleration[index].toDouble()
                TimelineScoreItemType.SAFETY_BRAKING -> timeline.allContext.braking[index].toDouble()
                TimelineScoreItemType.SAFETY_ADHERENCE -> timeline.allContext.adherence[index].toDouble()
                TimelineScoreItemType.ECODRIVING_EFFICIENCY_ACCELERATION -> timeline.allContext.efficiencyAcceleration[index]
                TimelineScoreItemType.ECODRIVING_EFFICIENCY_BRAKE -> timeline.allContext.efficiencyBrake[index]
                TimelineScoreItemType.ECODRIVING_EFFICIENCY_SPEED_MAINTAIN -> timeline.allContext.efficiencySpeedMaintain[index]
                TimelineScoreItemType.ECODRIVING_FUEL_VOLUME -> timeline.allContext.fuelVolume[index]
                TimelineScoreItemType.ECODRIVING_FUEL_SAVINGS -> timeline.allContext.fuelSaving[index]
                TimelineScoreItemType.ECODRIVING_CO2MASS -> timeline.allContext.co2Mass[index]
                TimelineScoreItemType.DISTRACTION_UNLOCK -> timeline.allContext.unlock[index].toDouble()
                TimelineScoreItemType.DISTRACTION_CALL_FORBIDDEN_DURATION -> timeline.allContext.callForbidden[index].toDouble()
                TimelineScoreItemType.DISTRACTION_PERCENTAGE_OF_TRIPS_WITH_FORBIDDEN_CALL -> timeline.allContext.numberTripWithForbiddenCall[index].toDouble() / timeline.allContext.numberTripTotal[index].toDouble()
                TimelineScoreItemType.SPEEDING_DURATION -> timeline.allContext.speedingDuration[index].toDouble()
                TimelineScoreItemType.SPEEDING_DISTANCE -> timeline.allContext.speedingDistance[index]
            }
        }
    }

    private fun getInterpolatedValue(date: Date, previousValidIndex: Int, nextValidIndex: Int, dates: List<Date>, calenderUnit: CalendarField, graphItem: GraphItem, timeline: Timeline): Double? {
        val previousValidDate: Date = dates[previousValidIndex]
        val nextValidDate: Date = dates[nextValidIndex]
        val diffBetweenPreviousAndNext = nextValidDate.diffWith(previousValidDate, calenderUnit)
        val diffBetweenPreviousAndDate = date.diffWith(previousValidDate, calenderUnit)
        val previousValue = getValue(previousValidIndex, graphItem, timeline)
        val nextValue = getValue(nextValidIndex, graphItem, timeline)
        return if (previousValue != null && nextValue != null) {
            val valueDelta = nextValue - previousValue
            val interpolatedValue = previousValue + valueDelta * diffBetweenPreviousAndDate.toDouble() / diffBetweenPreviousAndNext.toDouble()
            interpolatedValue
        } else {
            null
        }
    }

    override fun onSelectPoint(point: GraphPoint) {
        point.data?.let { data ->
            this.listener?.onSelectDate(data.date)
        }
    }
}
