package com.drivekit.demoapp.simulator.viewmodel

import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.tripsimulator.PresetTrip
import com.drivequant.drivekit.tripsimulator.model.PresetTripCrash1


internal enum class PresetTripType {
    SHORT_TRIP,
    MIXED_TRIP,
    CITY_TRIP,
    SUBURBAN_TRIP,
    HIGHWAY_TRIP,
    BOAT_TRIP,
    TRAIN_TRIP,
    BUS_TRIP,
    TRIP_WITH_CRASH_CONFIRMED_10KMH,
    TRIP_WITH_CRASH_CONFIRMED_20KMH,
    TRIP_WITH_CRASH_CONFIRMED_30KMH,
    TRIP_WITH_CRASH_UNCONFIRMED_0KMH;

    fun getTitleResId() = when (this) {
        SHORT_TRIP -> R.string.trip_simulator_short_trip_title
        MIXED_TRIP -> R.string.trip_simulator_city_suburban_title
        CITY_TRIP -> R.string.trip_simulator_city_title
        SUBURBAN_TRIP -> R.string.trip_simulator_suburban_title
        HIGHWAY_TRIP ->R.string.trip_simulator_highway_title
        BOAT_TRIP -> R.string.trip_simulator_boat_title
        TRAIN_TRIP -> R.string.trip_simulator_train_title
        BUS_TRIP -> R.string.trip_simulator_bus_title
        TRIP_WITH_CRASH_CONFIRMED_10KMH -> R.string.trip_simulator_crash_10_title
        TRIP_WITH_CRASH_CONFIRMED_20KMH -> R.string.trip_simulator_crash_20_title
        TRIP_WITH_CRASH_CONFIRMED_30KMH -> R.string.trip_simulator_crash_30_title
        TRIP_WITH_CRASH_UNCONFIRMED_0KMH -> R.string.trip_simulator_crash_0_title
    }

    fun getDescriptionResId() = when (this) {
        SHORT_TRIP -> R.string.trip_simulator_short_trip_description
        MIXED_TRIP -> R.string.trip_simulator_city_suburban_description
        CITY_TRIP -> R.string.trip_simulator_city_description
        SUBURBAN_TRIP ->R.string.trip_simulator_suburban_description
        HIGHWAY_TRIP ->R.string.trip_simulator_highway_description
        BOAT_TRIP -> R.string.trip_simulator_boat_description
        TRAIN_TRIP -> R.string.trip_simulator_train_description
        BUS_TRIP -> R.string.trip_simulator_bus_description
        TRIP_WITH_CRASH_CONFIRMED_10KMH -> R.string.trip_simulator_crash_10_description
        TRIP_WITH_CRASH_CONFIRMED_20KMH -> R.string.trip_simulator_crash_20_description
        TRIP_WITH_CRASH_CONFIRMED_30KMH -> R.string.trip_simulator_crash_30_description
        TRIP_WITH_CRASH_UNCONFIRMED_0KMH -> R.string.trip_simulator_crash_0_description
    }

    companion object {
        fun getPresetTrip(presetTripType: PresetTripType) = when (presetTripType) {
            SHORT_TRIP -> PresetTrip.SHORT_TRIP
            MIXED_TRIP -> PresetTrip.MIXED_TRIP
            CITY_TRIP -> PresetTrip.CITY_TRIP
            SUBURBAN_TRIP -> PresetTrip.SUBURBAN_TRIP
            HIGHWAY_TRIP -> PresetTrip.HIGHWAY_TRIP
            BOAT_TRIP -> PresetTrip.BOAT_TRIP
            TRAIN_TRIP -> PresetTrip.TRAIN_TRIP
            BUS_TRIP -> PresetTrip.BUS_TRIP
            TRIP_WITH_CRASH_CONFIRMED_10KMH -> PresetTrip.TRIP_WITH_CRASH_1(PresetTripCrash1.CONFIRMED_10KMH)
            TRIP_WITH_CRASH_CONFIRMED_20KMH -> PresetTrip.TRIP_WITH_CRASH_1(PresetTripCrash1.CONFIRMED_20KMH)
            TRIP_WITH_CRASH_CONFIRMED_30KMH -> PresetTrip.TRIP_WITH_CRASH_1(PresetTripCrash1.CONFIRMED_30KMH)
            TRIP_WITH_CRASH_UNCONFIRMED_0KMH -> PresetTrip.TRIP_WITH_CRASH_1(PresetTripCrash1.UNCONFIRMED_0KMH)
        }
    }
}