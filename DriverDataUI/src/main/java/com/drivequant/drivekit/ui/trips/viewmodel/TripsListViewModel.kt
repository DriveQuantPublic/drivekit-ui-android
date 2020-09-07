package com.drivequant.drivekit.ui.trips.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.navigation.GetVehiclesFilterItems
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.trip.TripsQueryListener
import com.drivequant.drivekit.driverdata.trip.TripsSyncStatus
import com.drivequant.drivekit.ui.extension.computeTotalDistance
import com.drivequant.drivekit.ui.extension.orderByDay
import java.util.*

class TripsListViewModel : ViewModel() {
    var tripsByDate: MutableList<TripsByDate> = mutableListOf()
    var filterItems: List<FilterItem> = listOf()
    val tripsData: MutableLiveData<List<TripsByDate>> = MutableLiveData()
    val filterData: MutableLiveData<List<FilterItem>> = MutableLiveData()
    var syncStatus: TripsSyncStatus = TripsSyncStatus.NO_ERROR
    lateinit var computedSynthesis: Pair<Int, Double>

    fun fetchTrips(dayTripDescendingOrder: Boolean, synchronizationType: SynchronizationType) {
        if (DriveKitDriverData.isConfigured()) {
            DriveKitDriverData.getTripsOrderByDateDesc(object : TripsQueryListener {
                override fun onResponse(status: TripsSyncStatus, trips: List<Trip>) {
                    syncStatus = status
                    computedSynthesis = Pair(trips.size, trips.computeTotalDistance())
                    tripsByDate = sortTrips(trips, dayTripDescendingOrder)
                    tripsData.postValue(tripsByDate)
                }
            }, synchronizationType)
        } else {
            tripsData.postValue(mutableListOf())
        }
    }

    fun filterTripsByVehicleId(dayTripDescendingOrder: Boolean, vehicleId: String? = null) {
        val whereReference = DriveKitDriverData.tripsQuery()
        val trips = vehicleId?.let {
            whereReference.whereEqualTo("vehicleId", vehicleId).query().execute()
        } ?: kotlin.run {
            whereReference.noFilter().query().execute()
        }
        computedSynthesis = Pair(trips.size, trips.computeTotalDistance())
        tripsByDate.clear()
        tripsByDate = sortTrips(trips, dayTripDescendingOrder)
        tripsData.postValue(tripsByDate)
    }

    private fun sortTrips(fetchedTrips: List<Trip>, dayTripDescendingOrder: Boolean): MutableList<TripsByDate> {
        if (fetchedTrips.isNotEmpty()) {
            tripsByDate = fetchedTrips.orderByDay(dayTripDescendingOrder)
        }
        return tripsByDate
    }

    fun getTripsByDate(date: Date): TripsByDate? {
        for (currentTripsByDate in tripsByDate) {
            if (currentTripsByDate.date == date) {
                return currentTripsByDate
            }
        }
        return null
    }

    fun getVehiclesFilterItems(context: Context) {
        DriveKitNavigationController.vehicleUIEntryPoint?.getVehiclesFilterItems(
            context,
            object : GetVehiclesFilterItems {
                override fun onFilterItemsReceived(vehiclesFilterItems: List<FilterItem>) {
                    filterItems = vehiclesFilterItems
                    filterData.postValue(filterItems)
                }
            })
    }
}