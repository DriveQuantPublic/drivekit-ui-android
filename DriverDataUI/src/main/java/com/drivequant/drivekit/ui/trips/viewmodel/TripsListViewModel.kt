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
import com.drivequant.drivekit.ui.extension.orderByDay
import java.util.*

class TripsListViewModel: ViewModel() {
    var tripsByDate: List<TripsByDate> = listOf()
    val tripsData : MutableLiveData<List<TripsByDate>> = MutableLiveData()
    var syncStatus: TripsSyncStatus = TripsSyncStatus.NO_ERROR

    fun fetchTrips(dayTripDescendingOrder: Boolean) {
        if (DriveKitDriverData.isConfigured()) {
            DriveKitDriverData.getTripsOrderByDateDesc(object: TripsQueryListener {
                override fun onResponse(status: TripsSyncStatus, trips: List<Trip>) {
                    syncStatus = status
                    tripsByDate = sortTrips(trips, dayTripDescendingOrder)
                    tripsData.postValue(tripsByDate)
                }
            }, SynchronizationType.DEFAULT)
        }else{
            tripsData.postValue(mutableListOf())
        }
    }

    fun sortTrips(fetchedTrips: List<Trip>, dayTripDescendingOrder: Boolean) : List<TripsByDate> {
        if (fetchedTrips.isNotEmpty()){
            tripsByDate = fetchedTrips.orderByDay(dayTripDescendingOrder)
        }
        return tripsByDate
    }

    fun getTripsByDate(date: Date) : TripsByDate? {
        for (currentTripsBydate in tripsByDate){
            if (currentTripsBydate.date == date){
                return currentTripsBydate
            }
        }
        return null
    }

    fun getVehiclesFilterItems(context: Context): List<FilterItem>? {
        var list = listOf<FilterItem>()
        DriveKitNavigationController.vehicleUIEntryPoint?.getVehiclesFilterItems(context, object : GetVehiclesFilterItems{
            override fun onFilterItemsReceived(vehiclesFilterItems: List<FilterItem>) {
                    list = vehiclesFilterItems
            }
        })
       return list
    }
}