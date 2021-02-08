package com.drivequant.drivekit.ui.tripdetail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Call
import com.drivequant.drivekit.databaseutils.entity.Route
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.databaseutils.entity.TripAdvice
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.trip.*
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfiguration
import java.util.*

internal class TripDetailViewModel(
    private val itinId: String,
    private val tripListConfiguration: TripListConfiguration
) : ViewModel(), DKTripDetailViewModel {

    private var mapItems: MutableList<DKMapItem> = mutableListOf()

    init {
        when (tripListConfiguration){
            is TripListConfiguration.MOTORIZED -> {
                val items : MutableList<DKMapItem> = mutableListOf()
                items.addAll(DriverDataUI.mapItems)
                DriverDataUI.customMapItem?.let { item ->
                    items.add(item)
                }
                mapItems = items
            }
            is TripListConfiguration.ALTERNATIVE -> {
                mapItems.add(AlternativeTripMapItem())
            }
        }
    }

    var trip: Trip? = null
        set(value) {
            field = value
            field?.let { trip ->
                if (!trip.unscored) {
                    for (item in mapItems) {
                        item.canShowMapItem(trip)?.let {
                            if (it) configurableMapItems.add(item)
                        }
                    }
                } else {
                    for (item in mapItems) {
                        if (item.overrideShortTrip()) {
                            configurableMapItems.add(item)
                        }
                    }
                }
                if (configurableMapItems.isNotEmpty()) {
                    displayMapItem.value = configurableMapItems[0]
                }
            }
        }
    var route: Route? = null

    private var routeSyncStatus: RouteStatus? = null
    private var tripSyncStatus: TripsSyncStatus? = null

    var events: MutableList<TripEvent> = mutableListOf()
    var displayEvents: List<TripEvent> = mutableListOf()
    var configurableMapItems = mutableListOf<DKMapItem>()
    var displayMapItem: MutableLiveData<DKMapItem> = MutableLiveData()

    var tripEventsObserver: MutableLiveData<List<TripEvent>> = MutableLiveData()
    var selection: MutableLiveData<Int> = MutableLiveData()
    var unScoredTrip: MutableLiveData<Boolean> = MutableLiveData()
    var noRoute: MutableLiveData<Boolean> = MutableLiveData()
    var noData: MutableLiveData<Boolean> = MutableLiveData()
    var deleteTripObserver: MutableLiveData<Boolean> = MutableLiveData()
    var sendAdviceFeedbackObserver: MutableLiveData<Boolean> = MutableLiveData()
    var selectedMapTraceType: MutableLiveData<MapTraceType> = MutableLiveData()

    fun fetchTripData(context: Context){
        if (DriveKitDriverData.isConfigured()){
            DriveKitDriverData.getTrip(itinId, object: TripQueryListener {
                override fun onResponse(status: TripsSyncStatus, trip: Trip?) {
                    tripSyncStatus = status
                    trip?.let {
                        this@TripDetailViewModel.trip = it
                        computeTripEvents(context)
                    }
                }
            })

            DriveKitDriverData.getRoute(itinId, object: RouteQueryListener {
                override fun onResponse(status: RouteStatus, route: Route?) {
                    routeSyncStatus = status
                    route?.let {
                        this@TripDetailViewModel.route = route
                    }
                    computeTripEvents(context)
                }
            })
        }
    }

    private fun updateTripAdvice(adviceId: String, evaluation: Int, feedback: Int, comment: String?){
        if (DriveKitDriverData.isConfigured()){
            trip?.let {
                for (tripAdvice in it.tripAdvices){
                    if (tripAdvice.id == adviceId){
                        tripAdvice.evaluation = evaluation
                        tripAdvice.feedback = feedback
                        tripAdvice.comment = comment
                    }
                }
            }
        }
    }

    fun changeMapItem(position: Int) {
        displayMapItem.postValue(configurableMapItems[position])
    }

    fun getFirstMapItemIndexWithAdvice(): Int {
        for ((loopIndex, value) in configurableMapItems.withIndex()){
            trip?.let {
                val advice = value.getAdvice(it)
                if (advice != null) {
                    return loopIndex
                }
            }
        }
        return -1
    }

    private fun computeTripEvents(context: Context){
        if (routeSyncStatus != null && tripSyncStatus != null){
            if (trip != null && route != null) {
                DriveKitDriverData.checkReverseGeocode(context, trip, route)
                computeTripEvent(trip!!, route!!)
                if (trip!!.unscored && tripListConfiguration != TripListConfiguration.ALTERNATIVE()){
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
        var chunkedCallIndex = listOf<List<Int>>()
        var chunkedCallTime = listOf<List<Int>>()
        route.callIndex?.let {
            chunkedCallIndex = it.chunked(2)
        }
        route.callTime?.let {
            chunkedCallTime = it.chunked(2)
        }
        trip.calls?.let { calls ->
            if (chunkedCallIndex.isNotEmpty()) {
                for ((indexPhoneCall, call) in calls.withIndex()) {
                    for ((index, indexCall) in chunkedCallIndex[indexPhoneCall].withIndex()) {
                        if ((route.latitude[indexCall]  == route.latitude.first() &&
                             route.longitude[indexCall] == route.longitude.first()) ||
                            (route.latitude[indexCall]  == route.latitude.last() &&
                             route.longitude[indexCall] == route.longitude.last())) continue
                        events.add(
                            TripEvent(
                                if (index.rem(2) == 0) TripEventType.PHONE_DISTRACTION_PICK_UP else TripEventType.PHONE_DISTRACTION_HANG_UP,
                                Date(trip.endDate.time - ((trip.tripStatistics?.duration!!.toLong() * 1000) - (chunkedCallTime[indexPhoneCall][index] * 1000))),
                                route.latitude[chunkedCallIndex[indexPhoneCall][index]],
                                route.longitude[chunkedCallIndex[indexPhoneCall][index]],
                                isForbidden = call.isForbidden,
                                callDuration = call.duration.toDouble()
                            )
                        )
                    }
                }
            }
        }
        events = events.sortedWith(compareBy { it.time }).toMutableList()
    }

    fun getCallFromIndex(position: Int): Call? {
        return trip?.let { trip ->
            trip.calls?.let { calls ->
                calls[position.rem(2)]
            }
        }
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

    fun shouldDisplayAdvice(mapItem: DKMapItem): Boolean {
        return getAdviceByMapItem(mapItem) != null
    }

    private fun getAdviceByMapItem(mapItem: DKMapItem): TripAdvice? {
        return trip?.let {
            return mapItem.getAdvice(it)
        }?.let {
            null
        }
    }

    fun getAdviceTitle(mapItem: DKMapItem): String? {
        return getAdviceByMapItem(mapItem)?.title
    }

    fun getAdviceMessage(mapItem: DKMapItem): String? {
        return getAdviceByMapItem(mapItem)?.message
    }

    fun shouldDisplayFeedbackButtons(mapItem: DKMapItem): Boolean {
        return getAdviceByMapItem(mapItem)?.evaluation == 0 && DriverDataUI.enableAdviceFeedback
    }

    fun sendTripAdviceFeedback(
        mapItem: DKMapItem,
        isPositive: Boolean,
        feedback: Int,
        comment: String? = null
    ) {
        val evaluation = if (isPositive) 1 else 2
        trip?.let {
            mapItem.getAdvice(it)?.id?.let { adviceId ->
                DriveKitDriverData.sendTripAdviceFeedback(
                    it.itinId,
                    adviceId,
                    evaluation,
                    feedback,
                    comment,
                    listener = object : TripAdviceFeedbackQueryListener {
                        override fun onResponse(status: Boolean) {
                            if (status) {
                                updateTripAdvice(adviceId, evaluation, feedback, comment)
                            }
                            sendAdviceFeedbackObserver.postValue(status)
                        }
                    })
            }
        }
    }

    override fun getTripEvents(): List<TripEvent> {
        return if (configurableMapItems.contains(MapItem.DISTRACTION)) {
            events
        } else {
            events.filterNot { it.type == TripEventType.PHONE_DISTRACTION_LOCK || it.type == TripEventType.PHONE_DISTRACTION_UNLOCK }
        }
    }

    override fun getSelectedEvent(): MutableLiveData<Int> = selection

    override fun getScore(): Double {
        trip?.driverDistraction?.score?.let {
            return it
        }
        return 0.toDouble()
    }

    override fun getUnlockNumberEvent(): Int {
        trip?.driverDistraction?.nbUnlock?.let {
            return it
        }
        return 0
    }

    override fun getUnlockDuration(context: Context) =
        DKDataFormatter.formatDuration(context, trip?.driverDistraction?.durationUnlock, 10)

    override fun getUnlockDistance(context: Context): String {
        trip?.driverDistraction?.distanceUnlock?.let {
            return if (it >= 1000) {
                DKDataFormatter.formatMeterDistanceInKm(context, it, unit = true, distanceMax = 10)
            } else {
                DKDataFormatter.formatMeterDistance(
                    context,
                    trip?.driverDistraction?.distanceUnlock
                )
            }
        }
        return "-"
    }

    override fun getSelectedTraceType(): MutableLiveData<MapTraceType> = selectedMapTraceType

    override fun getPhoneCallsDistance(context: Context): String {
        var distance = 0
        trip?.let { trip ->
            trip.calls?.let { calls ->
                distance = calls.map { call ->
                    call.distance
                }.sum()
            }
        }
        return DKDataFormatter.formatMeterDistance(context, distance.toDouble())
    }

    override fun getPhoneCallsNumber(context: Context): Pair<String, String> {
        var phoneCallNumber = 0
        trip?.let {
            it.calls?.let { calls ->
                phoneCallNumber = calls.size
            }
        }
        return if (phoneCallNumber == 0) {
            val phoneCallCongrats =
                DKResource.convertToString(context, "dk_driverdata_no_call_congrats")
            val phoneCallContentCongrats =
                DKResource.convertToString(context, "dk_driverdata_no_call_content")
            Pair(phoneCallCongrats, phoneCallContentCongrats)
        } else {
            val phoneCallContent = DKResource.buildString(
                context,
                DriveKitUI.colors.secondaryColor(),
                DriveKitUI.colors.secondaryColor(),
                "dk_driverdata_distance_travelled", getPhoneCallsDistance(context)
            )
            val phoneCallEvent = context.resources.getQuantityString(
                R.plurals.phone_call_plural,
                phoneCallNumber,
                phoneCallNumber
            )
            Pair(phoneCallEvent, phoneCallContent.toString())
        }
    }

    override fun getPhoneCallsDuration(context: Context): String {
        var duration = 0
        trip?.let { trip ->
            trip.calls?.let { calls ->
                duration = calls.map { call ->
                    call.duration
                }.sum()
            }
        }
        return DKDataFormatter.formatDuration(context, durationInSeconds = duration.toDouble(), maxNbMinute = 10)
    }
}

@Suppress("UNCHECKED_CAST")
class TripDetailViewModelFactory(
    private val itinId: String,
    private val tripListConfiguration: TripListConfiguration
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TripDetailViewModel(itinId, tripListConfiguration) as T
    }
}

interface DKTripDetailViewModel {
    fun getTripEvents(): List<TripEvent>
    fun getSelectedEvent(): MutableLiveData<Int>
    fun getScore(): Double
    fun getUnlockNumberEvent(): Int
    fun getUnlockDuration(context: Context): String
    fun getUnlockDistance(context: Context): String
    fun getSelectedTraceType(): MutableLiveData<MapTraceType>
    fun getPhoneCallsDistance(context: Context): String
    fun getPhoneCallsNumber(context: Context): Pair<String,String>
    fun getPhoneCallsDuration(context: Context): String
}
