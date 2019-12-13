package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.databaseutils.entity.Safety
import com.drivequant.drivekit.databaseutils.entity.Trip
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class SynthesisViewModel(private val context: Context, private val trip: Trip) : ViewModel() {

    private val notAvailable = "-"

    fun getRoadContextValue() = null

    fun getWeaherValue() = null

    fun getCondition() = null

    fun getCO2Mass():String? {
        return trip.fuelEstimation?.co2Mass?.let {
            var formattedMass = it
            var massUnit = "kg"
            if (it < 1){
                formattedMass = it*1000
                massUnit = "g"
            }
            "$formattedMass $massUnit"
        }?.run {
            notAvailable
        }
    }

    fun getCo2Emission(): String? {
        return trip.fuelEstimation?.let {
            return String.format("%s %s", it.co2Emission.roundToInt(), "g/km")
        }?.let {
            notAvailable
        }
    }

    fun getFuelConsumption(): String? {
        return trip.fuelEstimation?.let {
            String.format("%s %s", it.fuelConsumption.roundToLong(), "l/100km")
        }?.let {
            notAvailable
        }

    }

    fun getIdlingDuration(): String[] {

    }
}

class SynthesisViewModelFactory(private val context: Context, private val trip: Trip) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SynthesisViewModel(context, trip) as T
    }
}