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
    var tripsByDate: List<TripsByDate> = listOf()
    var filterItems: List<FilterItem> = listOf()
    val tripsData: MutableLiveData<List<TripsByDate>> = MutableLiveData()
    val filterData: MutableLiveData<List<FilterItem>> = MutableLiveData()
    var syncStatus: TripsSyncStatus = TripsSyncStatus.NO_ERROR
    var currentItemPosition: Int = 0
    lateinit var computedSynthesis: Pair<Int, Double>

    fun fetchTrips(dayTripDescendingOrder: Boolean) {
        if (DriveKitDriverData.isConfigured()) {
            DriveKitDriverData.getTripsOrderByDateDesc(object : TripsQueryListener {
                override fun onResponse(status: TripsSyncStatus, trips: List<Trip>) {
                    syncStatus = status
                    tripsByDate = sortTrips(trips, dayTripDescendingOrder)
                    tripsData.postValue(tripsByDate)
                }
            }, SynchronizationType.DEFAULT)
        } else {
            tripsData.postValue(mutableListOf())
        }
    }

    private fun filterTrips(fetchedTrips: List<Trip>): List<Trip> {
        return if (currentItemPosition != 0) {
            val tripsByVehicleId = mutableListOf<Trip>()
            for (trip in fetchedTrips) {
                if (trip.vehicleId == filterItems[currentItemPosition].itemId) {
                    tripsByVehicleId.add(trip)
                }
            }
            tripsByVehicleId
        } else {
            fetchedTrips
        }
    }

    fun sortTrips(fetchedTrips: List<Trip>, dayTripDescendingOrder: Boolean): List<TripsByDate> {
        val filteredTrips = filterTrips(fetchedTrips)
        tripsByDate = filteredTrips.orderByDay(dayTripDescendingOrder)
        computedSynthesis = Pair(filteredTrips.size, filteredTrips.computeTotalDistance())
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