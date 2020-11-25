package com.drivequant.drivekit.ui.trips.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.adapter.FilterItem
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.trip.TripsQueryListener
import com.drivequant.drivekit.driverdata.trip.TripsSyncStatus
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.extension.computeTotalDistance
import com.drivequant.drivekit.ui.extension.orderByDay
import java.util.*

class TripsListViewModel : ViewModel() {
    var tripsByDate: MutableList<TripsByDate> = mutableListOf()
    var filterItems: MutableList<FilterItem> = mutableListOf()
    val tripsData: MutableLiveData<List<TripsByDate>> = MutableLiveData()
    val filterData: MutableLiveData<List<FilterItem>> = MutableLiveData()
    var syncTripsError: MutableLiveData<Any> = MutableLiveData()
    var currentFilterItemPosition = 0
    private lateinit var computedSynthesis: Pair<Int, Double>
    private var allTrips = listOf<Trip>()

    fun fetchTrips(dayTripDescendingOrder: Boolean, synchronizationType: SynchronizationType = SynchronizationType.DEFAULT) {
        if (DriveKitDriverData.isConfigured()) {
            DriveKitDriverData.getTripsOrderByDateDesc(object : TripsQueryListener {
                override fun onResponse(status: TripsSyncStatus, trips: List<Trip>) {
                    if (status == TripsSyncStatus.FAILED_TO_SYNC_TRIPS_CACHE_ONLY) {
                        syncTripsError.postValue(Any())
                    }
                    allTrips = trips
                    computeTrips(trips, dayTripDescendingOrder)
                }
            }, synchronizationType)
        } else {
            syncTripsError.postValue(Any())
        }
    }

    private fun computeTrips(trips: List<Trip>, dayTripDescendingOrder: Boolean) {
        computedSynthesis = Pair(trips.size, trips.computeTotalDistance())
        tripsByDate = sortTrips(trips, dayTripDescendingOrder)
        tripsData.postValue(tripsByDate)
    }

    fun filterTrips(dayTripDescendingOrder: Boolean) {
        val trips = filterItems[currentFilterItemPosition].getItemId()?.let {
            allTrips.filter { it1 -> it1.vehicleId == it as String }
        } ?: kotlin.run {
            allTrips
        }
        tripsByDate.clear()
        computeTrips(trips, dayTripDescendingOrder)
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
        DriveKitNavigationController.vehicleUIEntryPoint?.getVehiclesFilterItems(context)?.let {
            filterItems.add(AllTripsFilterItem())
            filterItems.addAll(it)
        }
        filterData.postValue(filterItems)
    }

    fun getTripSynthesisText(context: Context): SpannableString {
        val tripsNumber = computedSynthesis.first
        val tripsDistance = computedSynthesis.second
        val trip =
            context.resources.getQuantityString(R.plurals.trip_plural, tripsNumber)
        return DKSpannable().append("$tripsNumber", context.resSpans {
            color(DriveKitUI.colors.primaryColor())
            size(R.dimen.dk_text_medium)
            typeface(Typeface.BOLD)
        }).append(" $trip - ", context.resSpans {

        }).append(
            DKDataFormatter.formatDistance(context, tripsDistance),
            context.resSpans {
                color(DriveKitUI.colors.primaryColor())
                size(R.dimen.dk_text_medium)
                typeface(Typeface.BOLD)
            }).toSpannable()
    }

    fun getVehicleFilterVisibility(): Boolean {
        return filterItems.size > 1
    }
}