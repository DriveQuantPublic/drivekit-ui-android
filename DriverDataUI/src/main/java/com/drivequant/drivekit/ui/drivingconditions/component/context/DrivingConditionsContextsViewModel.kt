package com.drivequant.drivekit.ui.drivingconditions.component.context

import android.content.Context
import androidx.annotation.ColorRes
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCard
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCardItem
import com.drivequant.drivekit.common.ui.extension.capitalizeFirstLetter
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.databaseutils.entity.DKWeather
import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.ui.R

internal class DrivingConditionsContextsViewModel {

    private companion object {
        const val LESS_THAN_BOUND = 0.45 / (1 - 0.45)
        const val GREATER_THAN_BOUND = 0.55 / (1 - 0.55)
    }

    var contextCards: List<DKContextCard> = listOf()
        private set

    var onViewModelUpdate: (() -> Unit)? = null

    fun configure(context: Context, contextKinds: List<DKContextKind>, drivingConditions: DKDriverTimeline.DKDrivingConditions, roadContexts: Map<RoadContext, DKDriverTimeline.DKRoadContextItem>) {
        this.contextCards = contextKinds.map { contextKind ->
            object : DKContextCard {
                override fun getTitle(context: Context): String = when (contextKind) {
                    DKContextKind.DAY_NIGHT -> getDayNightTitle(context, drivingConditions)
                    DKContextKind.ROAD -> getRoadTitle(context, roadContexts)
                    DKContextKind.TRIP_DISTANCE -> getTripDistanceTitle(context, drivingConditions)
                    DKContextKind.WEATHER -> getWeatherTitle(context, drivingConditions)
                    DKContextKind.WEEK -> getWeekTitle(context, drivingConditions)
                }
                override fun getItems(): List<DKContextCardItem> = when (contextKind) {
                    DKContextKind.DAY_NIGHT -> getDayNightItems(context, drivingConditions)
                    DKContextKind.ROAD -> getRoadItems(context, roadContexts)
                    DKContextKind.TRIP_DISTANCE -> getTripDistanceItems(context, drivingConditions)
                    DKContextKind.WEATHER -> getWeatherItems(context, drivingConditions)
                    DKContextKind.WEEK -> getWeekItems(context, drivingConditions)
                }
                override fun getEmptyDataDescription(context: Context): String = ""
            }
        }
        this.onViewModelUpdate?.invoke()
    }

    private fun getDayNightTitle(context: Context, drivingConditions: DKDriverTimeline.DKDrivingConditions): String {
        val ratio = if (drivingConditions.nightDistance <= 0) 2.0 else drivingConditions.dayDistance / drivingConditions.nightDistance
        return when {
            ratio < LESS_THAN_BOUND -> R.string.dk_driverdata_drivingconditions_main_night
            ratio > GREATER_THAN_BOUND -> R.string.dk_driverdata_drivingconditions_main_day
            else -> R.string.dk_driverdata_drivingconditions_all_day
        }.let {
            context.getString(it)
        }
    }

    private fun getDayNightItems(context: Context, drivingConditions: DKDriverTimeline.DKDrivingConditions): List<DKContextCardItem> {
        val totalDistance = drivingConditions.dayDistance + drivingConditions.nightDistance
        val dayCard = if (drivingConditions.dayDistance > 0) {
            getContextCardItem(
                context.getString(R.string.dk_driverdata_drivingconditions_day),
                R.color.dkGrayColor, //TODO
                drivingConditions.dayDistance,
                totalDistance
            )
        } else {
            null
        }
        val nightCard = if (drivingConditions.nightDistance > 0) {
            getContextCardItem(
                context.getString(R.string.dk_driverdata_drivingconditions_night),
                R.color.dkGrayColor, //TODO
                drivingConditions.nightDistance,
                totalDistance
            )
        } else {
            null
        }
        return listOfNotNull(dayCard, nightCard)
    }


    private fun getRoadTitle(context: Context, roadContexts: Map<RoadContext, DKDriverTimeline.DKRoadContextItem>): String {
        val cityDistance = getDistance(RoadContext.CITY, roadContexts) + getDistance(
            RoadContext.HEAVY_URBAN_TRAFFIC, roadContexts)
        val expresswaysDistance = getDistance(RoadContext.EXPRESSWAYS, roadContexts)
        val suburbanDistance = getDistance(RoadContext.SUBURBAN, roadContexts)
        return when (max(cityDistance, expresswaysDistance, suburbanDistance)) {
            cityDistance -> R.string.dk_driverdata_drivingconditions_main_city
            expresswaysDistance -> R.string.dk_driverdata_drivingconditions_main_expressways
            suburbanDistance -> R.string.dk_driverdata_drivingconditions_main_suburban
            else -> 0
        }.let {
            if (it == 0) {
                ""
            } else {
                context.getString(it)
            }
        }
    }

    private fun getRoadItems(context: Context, roadContexts: Map<RoadContext, DKDriverTimeline.DKRoadContextItem>): List<DKContextCardItem> {
        val totalDistance = roadContexts.map { (roadContext, roadContextItem) ->
            if (roadContext != RoadContext.TRAFFIC_JAM) {
                roadContextItem.distance
            } else {
                0.0
            }
        }.sum()
        val cards = listOf(RoadContext.HEAVY_URBAN_TRAFFIC, RoadContext.CITY, RoadContext.SUBURBAN, RoadContext.EXPRESSWAYS).mapNotNull { roadContext ->
            roadContexts[roadContext]?.distance?.let { distance ->
                if (distance > 0) {
                    getContextCardItem(
                        roadContext.getTitle(context),
                        R.color.dkGrayColor, //TODO
                        distance,
                        totalDistance
                    )
                } else {
                    null
                }
            }
        }
        return cards
    }


    private fun getTripDistanceTitle(context: Context, drivingConditions: DKDriverTimeline.DKDrivingConditions): String {
        val maxCategory = getMaxKey(drivingConditions.distanceByCategory)
        return if (maxCategory == null) {
            ""
        } else {
            when (maxCategory) {
                DKDriverTimeline.DKDrivingCategory.LESS_THAN_2_KM -> context.getString(R.string.dk_driverdata_drivingconditions_main_short_trips)
                DKDriverTimeline.DKDrivingCategory.FROM_2_TO_10_KM -> context.getString(R.string.dk_driverdata_drivingconditions_main_interval_distance, 2, 10)
                DKDriverTimeline.DKDrivingCategory.FROM_10_TO_50_KM -> context.getString(R.string.dk_driverdata_drivingconditions_main_interval_distance, 10, 50)
                DKDriverTimeline.DKDrivingCategory.FROM_50_TO_100_KM -> context.getString(R.string.dk_driverdata_drivingconditions_main_interval_distance, 50, 100)
                DKDriverTimeline.DKDrivingCategory.MORE_THAN_100_KM -> context.getString(R.string.dk_driverdata_drivingconditions_main_long_trips)
            }
        }
    }

    private fun getTripDistanceItems(context: Context, drivingConditions: DKDriverTimeline.DKDrivingConditions): List<DKContextCardItem> {
        val totalDistance = drivingConditions.distanceByCategory.values.sum()
        val cards = DKDriverTimeline.DKDrivingCategory.values().mapNotNull { category ->
            drivingConditions.distanceByCategory[category]?.let {
                if (it > 0) {
                    getContextCardItem(
                        category.getTitle(context),
                        R.color.dkGrayColor, //TODO
                        it,
                        totalDistance
                    )
                } else {
                    null
                }
            }
        }
        return cards
    }


    private fun getWeatherTitle(context: Context, drivingConditions: DKDriverTimeline.DKDrivingConditions): String {
        val maxCategory = getMaxKey(drivingConditions.distanceByWeatherType)
        return if (maxCategory == null) {
            ""
        } else {
            when (maxCategory) {
                DKWeather.CLOUD -> R.string.dk_driverdata_drivingconditions_main_cloud
                DKWeather.FOG -> R.string.dk_driverdata_drivingconditions_main_fog
                DKWeather.HAIL -> R.string.dk_driverdata_drivingconditions_main_ice
                DKWeather.RAIN -> R.string.dk_driverdata_drivingconditions_main_rain
                DKWeather.SNOW -> R.string.dk_driverdata_drivingconditions_main_snow
                DKWeather.SUN -> R.string.dk_driverdata_drivingconditions_main_sun
                DKWeather.UNKNOWN -> 0
            }.let {
                if (it == 0) {
                    ""
                } else {
                    context.getString(it)
                }
            }
        }
    }

    private fun getWeatherItems(context: Context, drivingConditions: DKDriverTimeline.DKDrivingConditions): List<DKContextCardItem> {
        val totalDistance = drivingConditions.distanceByWeatherType.values.sum()
        val cards = DKWeather.values().mapNotNull { weather ->
            if (weather == DKWeather.UNKNOWN) {
                null
            } else {
                drivingConditions.distanceByWeatherType[weather]?.let {
                    if (it > 0) {
                        getContextCardItem(
                            weather.getTitle(context),
                            R.color.dkGrayColor, //TODO
                            it,
                            totalDistance
                        )
                    } else {
                        null
                    }
                }
            }
        }
        return cards
    }


    private fun getWeekTitle(context: Context, drivingConditions: DKDriverTimeline.DKDrivingConditions): String {
        val ratio = if (drivingConditions.weekendDistance <= 0) 2.0 else drivingConditions.weekdaysDistance / drivingConditions.weekendDistance
        return when {
            ratio < LESS_THAN_BOUND -> R.string.dk_driverdata_drivingconditions_main_weekend
            ratio > GREATER_THAN_BOUND -> R.string.dk_driverdata_drivingconditions_main_weekdays
            else -> R.string.dk_driverdata_drivingconditions_all_week
        }.let {
            context.getString(it)
        }
    }

    private fun getWeekItems(context: Context, drivingConditions: DKDriverTimeline.DKDrivingConditions): List<DKContextCardItem> {
        val totalDistance = drivingConditions.weekdaysDistance + drivingConditions.weekendDistance
        val weekdaysCard = if (drivingConditions.weekdaysDistance > 0) {
            getContextCardItem(
                context.getString(R.string.dk_driverdata_drivingconditions_weekdays),
                R.color.dkGrayColor, //TODO
                drivingConditions.weekdaysDistance,
                totalDistance
            )
        } else {
            null
        }
        val weekendCard = if (drivingConditions.weekendDistance > 0) {
            getContextCardItem(
                context.getString(R.string.dk_driverdata_drivingconditions_weekend),
                R.color.dkGrayColor, //TODO
                drivingConditions.weekendDistance,
                totalDistance
            )
        } else {
            null
        }
        return listOfNotNull(weekdaysCard, weekendCard)
    }



    private fun getDistance(
        roadContext: RoadContext,
        roadContexts: Map<RoadContext, DKDriverTimeline.DKRoadContextItem>
    ): Double = roadContexts[roadContext]?.distance ?: 0.0

    private fun max(vararg values: Double?): Double? {
        var max: Double? = null
        for (value in values) {
            if (value != null && (max == null || value > max)) {
                max = value
            }
        }
        return max
    }

    private fun <T> getMaxKey(map: Map<T, Double>): T? {
        var maxValue: Double? = null
        var maxKey: T? = null
        for ((key, value) in map) {
            if (maxValue == null || value > maxValue) {
                maxValue = value
                maxKey = key
            }
        }
        return maxKey
    }

    private fun getContextCardItem(title: String, @ColorRes color: Int, distance: Double, totalDistance: Double): DKContextCardItem {
        return object : DKContextCardItem {
            override fun getColorResId(): Int = color
            override fun getTitle(context: Context): String = title
            override fun getSubtitle(context: Context): String = DKDataFormatter.formatMeterDistanceInKm(context, distance * 1000, true, 10.0).convertToString()
            override fun getPercent(): Double = distance / totalDistance
        }
    }

    private fun DKDriverTimeline.DKDrivingCategory.getTitle(context: Context): String = when (this) {
        DKDriverTimeline.DKDrivingCategory.LESS_THAN_2_KM -> context.getString(R.string.dk_driverdata_drivingconditions_short_trips)
        DKDriverTimeline.DKDrivingCategory.FROM_2_TO_10_KM -> context.getString(R.string.dk_driverdata_drivingconditions_interval_distance, 2, 10)
        DKDriverTimeline.DKDrivingCategory.FROM_10_TO_50_KM -> context.getString(R.string.dk_driverdata_drivingconditions_interval_distance, 10, 50)
        DKDriverTimeline.DKDrivingCategory.FROM_50_TO_100_KM -> context.getString(R.string.dk_driverdata_drivingconditions_interval_distance, 50, 100)
        DKDriverTimeline.DKDrivingCategory.MORE_THAN_100_KM -> context.getString(R.string.dk_driverdata_drivingconditions_long_trips)
    }

    private fun DKWeather.getTitle(context: Context): String = when (this) {
        DKWeather.CLOUD -> R.string.dk_driverdata_drivingconditions_cloud
        DKWeather.FOG -> R.string.dk_driverdata_drivingconditions_fog
        DKWeather.HAIL -> R.string.dk_driverdata_drivingconditions_ice
        DKWeather.RAIN -> R.string.dk_driverdata_drivingconditions_rain
        DKWeather.SNOW -> R.string.dk_driverdata_drivingconditions_snow
        DKWeather.SUN -> R.string.dk_driverdata_drivingconditions_sun
        DKWeather.UNKNOWN -> 0
    }.let {
        if (it == 0) {
            ""
        } else {
            context.getString(it)
        }
    }

    private fun RoadContext.getTitle(context: Context): String = when (this) {
        RoadContext.CITY -> com.drivequant.drivekit.common.ui.R.string.dk_common_driving_context_city
        RoadContext.EXPRESSWAYS -> com.drivequant.drivekit.common.ui.R.string.dk_common_driving_context_fastlane
        RoadContext.HEAVY_URBAN_TRAFFIC -> com.drivequant.drivekit.common.ui.R.string.dk_common_driving_context_city_dense
        RoadContext.SUBURBAN -> com.drivequant.drivekit.common.ui.R.string.dk_common_driving_context_external
        RoadContext.TRAFFIC_JAM -> null
    }?.let {
        context.getString(it).capitalizeFirstLetter()
    } ?: ""

}
