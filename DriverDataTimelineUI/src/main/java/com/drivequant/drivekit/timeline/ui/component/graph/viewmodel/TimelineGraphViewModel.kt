package com.drivequant.drivekit.timeline.ui.component.graph.viewmodel

import androidx.lifecycle.ViewModel
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKDatePattern
import com.drivequant.drivekit.databaseutils.entity.Timeline
import com.drivequant.drivekit.driverdata.timeline.DKTimelinePeriod
import com.drivequant.drivekit.timeline.ui.DKTimelineScoreType
import com.drivequant.drivekit.timeline.ui.component.graph.*
import com.drivequant.drivekit.timeline.ui.component.graph.GraphAxisConfig
import com.drivequant.drivekit.timeline.ui.component.graph.GraphItem
import com.drivequant.drivekit.timeline.ui.component.graph.GraphPoint
import com.drivequant.drivekit.timeline.ui.component.graph.GraphType
import com.drivequant.drivekit.timeline.ui.toTimelineDate
import java.util.Date

internal class TimelineGraphViewModel : ViewModel(), GraphViewModel {
    private companion object {
        private const val GRAPH_POINT_NUMBER: Int = 8
    }
    var listener: TimelineGraphListener? = null
    override var graphViewModelDidUpdate: (() -> Void)? = null
    override var type: GraphType = GraphType.LINE
    override var points: List<GraphPoint?> = emptyList()
    override var selectedIndex: Int = 0
    override var xAxisConfig: GraphAxisConfig? = null
    override var yAxisConfig: GraphAxisConfig? = null
    var title: String = ""
        private set
    var description: String = ""
        private set
    private var sourceDates: List<String>? = null
    private var timelineSelectedIndex: Int? = null
    private var indexOfFirstPointInTimeline: Int? = null
    private var indexOfLastPointInTimeline: Int? = null

    fun configure(timeline: Timeline, timelineSelectedIndex: Int, graphItem: GraphItem, period: DKTimelinePeriod) {
        val sourceDates = timeline.allContext.date
        val dates: List<Date> = sourceDates.map { it.toTimelineDate()!!.removeTime() }
        val calendarField = getCalendarField(period)
        val graphPointNumber = GRAPH_POINT_NUMBER
        val selectedDate = dates[timelineSelectedIndex]
        val now = Date()
        val currentDate = now.startingFrom(calendarField)
        val delta = selectedDate.diffWith(currentDate, calendarField).toInt()
        this.sourceDates = sourceDates
        this.timelineSelectedIndex = timelineSelectedIndex
        val selectedIndexInGraph = (graphPointNumber - 1) - (delta % graphPointNumber)
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
//                if (value != null) {
//                    point =
//                } else {
//                    point = getInterpolatedValue()
//                }
            }
        }
/*
        for i in 0..<graphPointNumber {
                if let xLabelDateIndex = dates.firstIndex(of: xLabelDate) {
                    if let value = getValue(atIndex: xLabelDateIndex, for: graphItem, in: timeline) {
                        point = (x: Double(i), y: value, data: PointData(date: sourceDates[xLabelDateIndex], interpolatedPoint: false))
                    } else if shouldInterpolate {
                        point = interpolateSelectableDateWithoutValue(fromDateIndex: xLabelDateIndex, graphItem: graphItem, timeline: timeline, dates: dates, dateComponent: dateComponent, pointX: Double(i), sourceDates: sourceDates)
                    }
                } else if shouldInterpolate {
                    if let graphStartDate {
                        if i == 0 {
                            point = interpolateStartOfGraph(from: graphStartDate, dateComponent: dateComponent, dates: dates, graphItem: graphItem, timeline: timeline, xLabelDate: xLabelDate)
                        } else if i == graphPointNumber - 1 {
                            point = interpolateEndOfGraph(from: graphStartDate, dateComponent: dateComponent, dates: dates, graphItem: graphItem, timeline: timeline, xLabelDate: xLabelDate)
                        }
                    }
                }
            }
            graphPoints.append(point)
            computeIndexOfFirstAndLastPointInTimeline(from: graphPoints)
        }
        configure(graphItem: graphItem, graphPointNumber: graphPointNumber, graphPoints: graphPoints, graphDates: graphDates, selectedIndex: selectedIndexInGraph, graphDescription: graphItem.getGraphDescription(fromValue: getValue(atIndex: timelineSelectedIndex, for: graphItem, in: timeline)))

 */

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

//    private fun getInterpolatedStartOfGraphPoint(graphStartDate: Date, calendarField: Int, dates: List<Date>, graphItem: GraphItem, timeline: Timeline, xLabelDate: Date): GraphPoint? {
//        // Find next valid index
//        var point: GraphPoint? = nil
//        var nextValidIndex: Int?
//        var i = 0
//        var hasMoreKnownDates = true
//        let lastDate = dates.last!
//        while nextValidIndex == nil && hasMoreKnownDates {
//            i += 1
//            guard let nextValidDate = graphStartDate.date(byAdding: i, calendarUnit: dateComponent) else {
//            preconditionFailure("We should always be able to add one \(dateComponent) to date: \(graphStartDate)")
//        }
//            nextValidIndex = dates.firstIndex(of: nextValidDate)
//            if let index = nextValidIndex {
//                if getValue(atIndex: index, for: graphItem, in: timeline) == nil {
//                nextValidIndex = nil
//            }
//            }
//            hasMoreKnownDates = nextValidDate <= lastDate
//        }
//        if let nextValidIndex, nextValidIndex > 0 {
//            var previousValidIndex: Int = nextValidIndex - 1
//            while previousValidIndex >= 0 && getValue(atIndex: previousValidIndex, for: graphItem, in: timeline) == nil {
//            previousValidIndex -= 1
//        }
//            if previousValidIndex >= 0 {
//                if let interpolatedValue = interpolateValueFrom(date: xLabelDate, previousValidIndex: previousValidIndex, nextValidIndex: nextValidIndex, dates: dates, dateComponent: dateComponent, graphItem: graphItem, timeline: timeline) {
//                point = (x: Double(0), y: interpolatedValue, data: nil)
//            }
//            }
//        }
//        return point
//    }
//
//    private func interpolateEndOfGraph(from graphStartDate: Date, dateComponent: Calendar.Component, dates: [Date], graphItem: GraphItem, timeline: DKTimeline, xLabelDate: Date) -> GraphPoint? {
//        // Find previous valid index
//        var point: GraphPoint? = nil
//        var previousValidIndex: Int?
//        var i = Self.graphPointNumber - 1
//        var hasMoreKnownDates = true
//        let firstDate = dates.first!
//        while previousValidIndex == nil && hasMoreKnownDates {
//            i -= 1
//            guard let previousValidDate = graphStartDate.date(byAdding: i, calendarUnit: dateComponent) else {
//            preconditionFailure("We should always be able to add one \(dateComponent) to date: \(graphStartDate)")
//        }
//            previousValidIndex = dates.firstIndex(of: previousValidDate)
//            if let index = previousValidIndex {
//                if getValue(atIndex: index, for: graphItem, in: timeline) == nil {
//                previousValidIndex = nil
//            }
//            }
//            hasMoreKnownDates = previousValidDate >= firstDate
//        }
//        let max = dates.count
//                if let previousValidIndex, previousValidIndex < max - 1 {
//            var nextValidIndex: Int = previousValidIndex + 1
//            while nextValidIndex < max && getValue(atIndex: nextValidIndex, for: graphItem, in: timeline) == nil {
//            nextValidIndex += 1
//        }
//            if nextValidIndex < max {
//                if let interpolatedValue = interpolateValueFrom(date: xLabelDate, previousValidIndex: previousValidIndex, nextValidIndex: nextValidIndex, dates: dates, dateComponent: dateComponent, graphItem: graphItem, timeline: timeline) {
//                point = (x: Double(Self.graphPointNumber - 1), y: interpolatedValue, data: nil)
//            }
//            }
//        }
//        return point
//    }
//
//    private func interpolateSelectableDateWithoutValue(fromDateIndex dateIndex: Int, graphItem: GraphItem, timeline: DKTimeline, dates: [Date], dateComponent: Calendar.Component, pointX: Double, sourceDates: [Date]) -> GraphPoint? {
//        let point: GraphPoint?
//        if dates.count == 1 {
//            point = (x: pointX, y: 0, data: nil)
//        } else {
//            let previousIndexWithValue = previousIndexWithValue(from: dateIndex) { index in
//                    getValue(atIndex: index, for: graphItem, in: timeline) != nil
//            }
//            let nextIndexWithValue = nextIndexWithValue(from: dateIndex, to: dates.count) { index in
//                    getValue(atIndex: index, for: graphItem, in: timeline) != nil
//            }
//            if let previousIndexWithValue, let nextIndexWithValue, let interpolatedValue = interpolateValueFrom(
//            date: dates[dateIndex],
//            previousValidIndex: previousIndexWithValue,
//            nextValidIndex: nextIndexWithValue,
//            dates: dates,
//            dateComponent: dateComponent,
//            graphItem: graphItem,
//            timeline: timeline
//            ) {
//                point = (x: pointX, y: interpolatedValue, data: PointData(date: sourceDates[dateIndex], interpolatedPoint: true))
//            } else {
//                point = nil
//            }
//        }
//        return point
//    }
//
//    private func computeIndexOfFirstAndLastPointInTimeline(from graphPoints: [GraphPoint?]) {
//        self.indexOfFirstPointInTimeline = nil
//        self.indexOfLastPointInTimeline = nil
//        if let sourceDates = self.sourceDates {
//            for graphPoint in graphPoints {
//                if let data = graphPoint?.data, !data.interpolatedPoint {
//                if self.indexOfFirstPointInTimeline == nil {
//                    self.indexOfFirstPointInTimeline = sourceDates.firstIndex(of: data.date)
//                }
//                self.indexOfLastPointInTimeline = sourceDates.firstIndex(of: data.date)
//            }
//            }
//        }
//    }

    private fun getValue(index: Int, graphItem: GraphItem, timeline: Timeline): Double? {
        return when (graphItem) {
            is GraphItem.Score -> when (graphItem.scoreType) {
                DKTimelineScoreType.SAFETY -> if (timeline.allContext.numberTripScored[index] > 0) timeline.allContext.safety[index] else null
                DKTimelineScoreType.ECO_DRIVING -> if (timeline.allContext.numberTripScored[index] > 0) timeline.allContext.efficiency[index] else null
                DKTimelineScoreType.DISTRACTION -> timeline.allContext.phoneDistraction[index]
                DKTimelineScoreType.SPEEDING -> timeline.allContext.speeding[index]
            }
            is GraphItem.ScoreItem -> when (graphItem.scoreItemType) {
                TimelineScoreItemType.SAFETY_ACCELERATION -> timeline.allContext.acceleration[index].toDouble()
                TimelineScoreItemType.SAFETY_BRAKING -> timeline.allContext.braking[index].toDouble()
                TimelineScoreItemType.SAFETY_ADHERENCE -> timeline.allContext.adherence[index].toDouble()
                TimelineScoreItemType.ECODRIVING_EFFICIENCY_ACCELERATION -> timeline.allContext.efficiencyAcceleration[index]
                TimelineScoreItemType.ECODRIVING_EFFICIENCY_BRAKE -> timeline.allContext.efficiencyBrake[index]
                TimelineScoreItemType.ECODRIVING_EFFICIENCY_SPEED_MAINTAIN -> timeline.allContext.efficiencySpeedMaintain[index]
                TimelineScoreItemType.ECODRIVING_FUEL_VOLUME -> timeline.allContext.fuelVolume[index]
                TimelineScoreItemType.ECODRIVING_FUEL_SAVINGS -> TODO()
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
        val diffBetweenPreviousAndNext = previousValidDate.diffWith(nextValidDate, calenderUnit)
        val diffBetweenPreviousAndDate = previousValidDate.diffWith(date, calenderUnit)
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
}