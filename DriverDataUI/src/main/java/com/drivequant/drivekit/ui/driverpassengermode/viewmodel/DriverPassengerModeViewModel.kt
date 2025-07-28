package com.drivequant.drivekit.ui.driverpassengermode.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.trip.TransportationModeQueryListener
import com.drivequant.drivekit.driverdata.trip.TransportationModeUpdateStatus
import com.drivequant.drivekit.driverdata.trip.UpdateDriverPassengerModeStatus
import com.drivequant.drivekit.driverdata.trip.driverpassengermode.DriverPassengerMode

internal class DriverPassengerModeViewModel(private val itinId: String) : ViewModel() {
    var trip: Trip?
        private set
    val updateCarOccupantRoleObserver: MutableLiveData<UpdateDriverPassengerModeStatus> = MutableLiveData()
    val updatePassengerTransportationModeObserver: MutableLiveData<TransportationModeUpdateStatus> = MutableLiveData()
    private var selectedTransportationMode: TransportationMode? = null
    private var selectedDriverPassengerMode: DriverPassengerMode? = null
    var comment: String = ""

    init {
        trip = DbTripAccess.findTrip(itinId).executeOneTrip()?.toTrip()
    }

    fun checkFieldsValidity(): Boolean =
        selectedDriverPassengerMode != null || selectedTransportationMode != null

    fun isCommentValid(comment: String) = comment.length <= 120

    fun selectCarOccupantRole(driverPassengerMode: DriverPassengerMode) {
        this.selectedTransportationMode = TransportationMode.CAR
        this.selectedDriverPassengerMode = driverPassengerMode
    }

    fun selectTransportationMode(transportationMode: TransportationMode) {
        this.selectedTransportationMode = transportationMode
        this.selectedDriverPassengerMode = DriverPassengerMode.PASSENGER
    }

    fun updateDriverPassengerMode() {
        if (this.selectedTransportationMode == TransportationMode.CAR) {
            DriveKitDriverData.updateDriverPassengerMode(
                itinId,
                selectedDriverPassengerMode ?: DriverPassengerMode.PASSENGER,
                comment
            ) { status ->
                updateCarOccupantRoleObserver.postValue(status)
            }
        } else {
            this.selectedTransportationMode?.let { transportationMode ->
                DriveKitDriverData.declareTransportationMode(
                    itinId,
                    transportationMode,
                    passenger = true,
                    comment,
                    object : TransportationModeQueryListener {
                        override fun onResponse(status: TransportationModeUpdateStatus) {
                            updatePassengerTransportationModeObserver.postValue(status)
                        }
                    })
            }
        }
    }

    fun buildTransportationModes() = listOf(
        TransportationMode.CAR,
        TransportationMode.BUS,
        TransportationMode.TRAIN,
        TransportationMode.BIKE,
        TransportationMode.BOAT,
        TransportationMode.FLIGHT,
        TransportationMode.ON_FOOT
    )

    fun buildDriverPassengerModes() = DriverPassengerMode.values().asList()

    @Suppress("UNCHECKED_CAST")
    class DriverPassengerModeViewModelFactory(private val itinId: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DriverPassengerModeViewModel(itinId) as T
        }
    }
}
