package com.drivequant.drivekit.ui.trips.viewmodel

import androidx.annotation.Keep
import com.drivequant.drivekit.databaseutils.entity.TransportationMode

sealed class TripListConfiguration {
    data class MOTORIZED(val vehicleId: String? = null) : TripListConfiguration()
    data class ALTERNATIVE(val transportationMode: TransportationMode? = null) :
        TripListConfiguration()

    fun getTransportationModes(): List<TransportationMode> {
        return when (this) {
            is MOTORIZED -> listOf(
                TransportationMode.UNKNOWN,
                TransportationMode.CAR,
                TransportationMode.MOTO,
                TransportationMode.TRUCK
            )
            is ALTERNATIVE -> listOf(
                TransportationMode.BUS,
                TransportationMode.TRAIN,
                TransportationMode.BOAT,
                TransportationMode.BIKE,
                TransportationMode.FLIGHT,
                TransportationMode.SKIING,
                TransportationMode.ON_FOOT,
                TransportationMode.IDLE,
                TransportationMode.OTHER
            )
        }
    }
}

@Keep
enum class TripListConfigurationType {
    MOTORIZED,
    ALTERNATIVE;

    companion object {
        fun getType(tripListConfiguration: TripListConfiguration): TripListConfigurationType {
            return when (tripListConfiguration) {
                is TripListConfiguration.MOTORIZED -> MOTORIZED
                is TripListConfiguration.ALTERNATIVE -> ALTERNATIVE
            }
        }
    }

    fun getTripListConfiguration(): TripListConfiguration {
        return when (this) {
            MOTORIZED -> TripListConfiguration.MOTORIZED()
            ALTERNATIVE -> TripListConfiguration.ALTERNATIVE()
        }
    }
}