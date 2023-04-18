package com.drivequant.drivekit.ui.drivingconditions.component.context.card

import android.content.Context
import androidx.annotation.ColorRes
import com.drivequant.drivekit.common.ui.component.contextcard.DKContextCardItem
import com.drivequant.drivekit.databaseutils.entity.DKWeather
import com.drivequant.drivekit.driverdata.timeline.DKDriverTimeline
import com.drivequant.drivekit.ui.R

internal class WeatherContextCard(
    private val context: Context,
    private val drivingConditions: DKDriverTimeline.DKDrivingConditions
) : BaseContextCard() {

    private var items: List<DKContextCardItem>? = null

    override fun getItems(): List<DKContextCardItem> {
        this.items?.let {
            return it
        }
        val totalDistance = drivingConditions.distanceByWeatherType.values.sum()
        val cards = DKWeather.values().mapNotNull { weather ->
            if (weather == DKWeather.UNKNOWN) {
                null
            } else {
                drivingConditions.distanceByWeatherType[weather]?.let {
                    if (it > 0) {
                        getContextCardItem(
                            weather.getTitle(context),
                            weather.getColor(),
                            it,
                            totalDistance
                        )
                    } else {
                        null
                    }
                }
            }
        }
        return cards.also {
            this.items = it
        }
    }

    override fun getTitle(context: Context): String {
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

@ColorRes
private fun DKWeather.getColor(): Int = when (this) {
    DKWeather.CLOUD -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor2
    DKWeather.FOG -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor3
    DKWeather.HAIL -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor6
    DKWeather.RAIN -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor4
    DKWeather.SNOW -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor5
    DKWeather.SUN -> com.drivequant.drivekit.common.ui.R.color.dkContextCardColor1
    DKWeather.UNKNOWN -> throw IllegalArgumentException()
}
