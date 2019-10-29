package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.drivequant.drivekit.databaseutils.entity.Route
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.trip.*
import com.drivequant.drivekit.ui.trips.viewmodel.TripInfo
import java.io.Serializable
import java.util.*

class TripDetailViewModel(private val itinId: String, private val mapItems: List<MapItem>): ViewModel(), Serializable {

    var trip: Trip? = null
        set(value) {
            field = value
            field?.let {trip ->
                if (!trip.unscored){
                    for (item in mapItems){
                        when (item){
                            MapItem.ECO_DRIVING -> trip.ecoDriving?.let {

                                if(it.score <= 10) configurableMapItems.add(item)
                            }
                            MapItem.SAFETY -> trip.safety?.let {
                                if(it.safetyScore <= 10) configurableMapItems.add(item)
                            }
                            MapItem.INTERACTIVE_MAP -> configurableMapItems.add(item)
                            MapItem.DISTRACTION -> trip.driverDistraction?.let {
                                if(it.score <= 10) configurableMapItems.add(item)
                            }
                        }
                    }
                    displayMapItem.value = configurableMapItems[0]
                }
            }

        }
    var route: Route? = null

    private var routeSyncStatus: RouteStatus? = null
    private var tripSyncStatus: TripsSyncStatus? = null

    var events: MutableList<TripEvent> = mutableListOf()
    var displayEvents: List<TripEvent> = mutableListOf()
    var configurableMapItems = mutableListOf<MapItem>()
    var displayMapItem : MutableLiveData<MapItem> = MutableLiveData()

    var tripEventsObserver: MutableLiveData<List<TripEvent>> = MutableLiveData()
    var selection: MutableLiveData<Int> = MutableLiveData()
    var unScoredTrip : MutableLiveData<Boolean> = MutableLiveData()
    var noRoute: MutableLiveData<Boolean> = MutableLiveData()
    var noData: MutableLiveData<Boolean> = MutableLiveData()
    var deleteTripObserver: MutableLiveData<Boolean> = MutableLiveData()

    fun fetchTripData(){
        if (DriveKitDriverData.isConfigured()){
            DriveKitDriverData.getTrip(itinId, object: TripQueryListener {
                override fun onResponse(status: TripsSyncStatus, trip: Trip?) {
                    tripSyncStatus = status
                    trip?.let {
                        this@TripDetailViewModel.trip = it
                        computeTripEvents()
                    }
                }
            })

            DriveKitDriverData.getRoute(itinId, object: RouteQueryListener {
                override fun onResponse(status: RouteStatus, route: Route?) {
                    routeSyncStatus = status
                    route?.let {
                        this@TripDetailViewModel.route = route
                    }
                    computeTripEvents()
                }
            })
        }
    }

    fun changeMapItem(position: Int) {
        displayMapItem.postValue(configurableMapItems[position])
    }

    fun getAdviceMapItemIndex(tripInfo: TripInfo): Int {
        return when (tripInfo){
            TripInfo.SAFETY -> {
                configurableMapItems.indexOf(MapItem.SAFETY)
            }
            TripInfo.ECO_DRIVING -> {
                configurableMapItems.indexOf(MapItem.ECO_DRIVING)
            }
            TripInfo.COUNT -> {
                getFirstMapItemIndexWithAdvice()
            }
            TripInfo.NONE -> -1
        }
    }

    private fun getFirstMapItemIndexWithAdvice(): Int {
        for ((loopIndex, value) in configurableMapItems.withIndex()){
            trip?.let {
                val advice = value.getAdvice(it.tripAdvices)
                if (advice != null) {
                    return loopIndex
                }
            }
        }
        return -1
    }

    private fun computeTripEvents(){
        if (routeSyncStatus != null && tripSyncStatus != null){
            if (trip != null && route != null) {
                computeTripEvent(trip!!, route!!)
                if (trip!!.unscored){
                    unScoredTrip.postValue(true)
                }else{
                    tripEventsObserver.postValue(events)
                }
            }else{
                if (trip != null){
                    if (trip!!.unscored){
                        unScoredTrip.postValue(false)
                    }else{
                        noRoute.postValue(true)
                    }
                }else{
                    noData.postValue(true)
                }

            }
        }
    }

    private fun computeTripEvent(trip: Trip, route: Route){
        val startTripEvent = TripEvent(
            TripEventType.START,
            Date(trip.endDate.time - (trip.tripStatistics?.duration!!.toLong() * 1000)),
            route.latitude[0],
            route.longitude[0]
        )
        val endTripEvent = TripEvent(
            TripEventType.FINISH,
            trip.endDate,
            route.latitude.last(),
            route.longitude.last()
        )
        events.add(startTripEvent)
        events.add(endTripEvent)
        trip.safetyEvents?.let {
            for (event in it){
                when (event.type){
                    1 -> events.add(
                        TripEvent(
                            TripEventType.SAFETY_ADHERENCE,
                            Date(trip.endDate.time - ((trip.tripStatistics?.duration!!.toLong() * 1000) - (event.time.toLong() * 1000))),
                            event.latitude,
                            event.longitude,
                            event.value > 0.3,
                            event.value
                        )
                    )
                    2 -> events.add(
                        TripEvent(
                            TripEventType.SAFETY_ACCEL,
                            Date(trip.endDate.time - ((trip.tripStatistics?.duration!!.toLong() * 1000) - (event.time.toLong() * 1000))),
                            event.latitude,
                            event.longitude,
                            event.value > 2.5,
                            event.value
                        )
                    )
                    3 -> events.add(
                        TripEvent(
                            TripEventType.SAFETY_BRAKE,
                            Date(trip.endDate.time - ((trip.tripStatistics?.duration!!.toLong() * 1000) - (event.time.toLong() * 1000))),
                            event.latitude,
                            event.longitude,
                            event.value < -2.4,
                            event.value
                        )
                    )
                }
            }
        }
        route.screenLockedIndex?.let {
            for ((index, indexScreenLocked) in it.withIndex()){
                if (indexScreenLocked == 0 || indexScreenLocked == route.latitude.size - 1) continue
                if (route.screenStatus!![index] == 1) {
                    events.add(
                        TripEvent(
                            TripEventType.PHONE_DISTRACTION_UNLOCK,
                            Date(trip.endDate.time - ((trip.tripStatistics?.duration!!.toLong() * 1000) - (route.screenLockedTime!![index] * 1000))),
                            route.latitude[it[index]],
                            route.longitude[it[index]]
                        )
                    )
                }else{
                    events.add(
                        TripEvent(
                            TripEventType.PHONE_DISTRACTION_LOCK,
                            Date(trip.endDate.time - ((trip.tripStatistics?.duration!!.toLong() * 1000) - (route.screenLockedTime!![index] * 1000))),
                            route.latitude[it[index]],
                            route.longitude[it[index]]
                        )
                    )
                }
            }
        }

        events = events.sortedWith(compareBy {it.time}).toMutableList()
    }

    fun deleteTrip(){
        if (DriveKitDriverData.isConfigured()){
            DriveKitDriverData.deleteTrip(itinId, object: TripDeleteQueryListener {
                override fun onResponse(status: Boolean) {
                    deleteTripObserver.postValue(status)
                }
            })
        }
    }
}

class TripDetailViewModelFactory(private val itinId: String, private val mapItems: List<MapItem>) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TripDetailViewModel(itinId, mapItems) as T
    }
}