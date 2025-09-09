package com.drivequant.drivekit.ui.tripdetail.viewmodel

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.removeZeroDecimal
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.convertToString
import com.drivequant.drivekit.core.geocoder.CheckReverseGeocodeListener
import com.drivequant.drivekit.databaseutils.entity.Call
import com.drivequant.drivekit.databaseutils.entity.OccupantRole
import com.drivequant.drivekit.databaseutils.entity.Route
import com.drivequant.drivekit.databaseutils.entity.TransportationMode
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.databaseutils.entity.TripAdvice
import com.drivequant.drivekit.dbtripaccess.DbTripAccess
import com.drivequant.drivekit.driverdata.DriveKitDriverData
import com.drivequant.drivekit.driverdata.trip.RouteQueryListener
import com.drivequant.drivekit.driverdata.trip.RouteStatus
import com.drivequant.drivekit.driverdata.trip.TripAdviceFeedbackQueryListener
import com.drivequant.drivekit.driverdata.trip.TripDeleteQueryListener
import com.drivequant.drivekit.driverdata.trip.TripQueryListener
import com.drivequant.drivekit.driverdata.trip.TripsSyncStatus
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.model.DeclarationBadgeStatus
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfiguration
import com.drivequant.drivekit.ui.trips.viewmodel.TripListConfigurationType
import java.util.Date
import kotlin.math.ceil

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
                configurableMapItems.clear()
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
    var reverseGeocoderObserver: MutableLiveData<Boolean> = MutableLiveData()

    fun fetchTripData(context: Context) {
        if (DriveKitDriverData.isConfigured()) {
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

    fun updateLocalTripData() {
        this@TripDetailViewModel.trip = DbTripAccess.findTrip(itinId).executeOneTrip()?.toTrip()
    }

    private fun updateTripAdvice(
        adviceId: String,
        evaluation: Int,
        feedback: Int,
        comment: String?) {
        if (DriveKitDriverData.isConfigured()) {
            trip?.let {
                for (tripAdvice in it.tripAdvices) {
                    if (tripAdvice.id == adviceId) {
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
        for ((loopIndex, value) in configurableMapItems.withIndex()) {
            trip?.let {
                val advice = value.getAdvice(it)
                if (advice != null) {
                    return loopIndex
                }
            }
        }
        return -1
    }

    private fun computeTripEvents(context: Context) {
        if (routeSyncStatus != null && tripSyncStatus != null) {
            if (trip != null && route != null) {
                DriveKitDriverData.checkReverseGeocode(
                    context,
                    trip,
                    route,
                    object : CheckReverseGeocodeListener {
                        override fun onFinished(updated: Boolean) {
                            reverseGeocoderObserver.postValue(updated)
                        }
                    }
                )
                computeTripEvent(trip!!, route!!)
                if (trip!!.unscored && tripListConfiguration != TripListConfiguration.ALTERNATIVE()) {
                    unScoredTrip.postValue(true)
                } else {
                    tripEventsObserver.postValue(events)
                }
            } else {
                if (trip != null) {
                    if (trip!!.unscored) {
                        unScoredTrip.postValue(false)
                    } else {
                        noRoute.postValue(true)
                    }
                } else {
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
            for ((index, indexScreenLocked) in it.withIndex()) {
                // avoid case when `indexScreenLocked` has invalid value
                if (indexScreenLocked < 0 || indexScreenLocked > route.latitude.lastIndex) continue

                // ignore first and last `screenLockedIndex` value
                if (index == 0 || index == it.lastIndex) continue

                val tripEventType = if (route.screenStatus!![index] == 1) {
                    TripEventType.PHONE_DISTRACTION_UNLOCK
                } else {
                    TripEventType.PHONE_DISTRACTION_LOCK
                }

                events.add(
                    TripEvent(
                        tripEventType,
                        Date(trip.endDate.time - ((trip.tripStatistics?.duration!!.toLong() * 1000) - (route.screenLockedTime!![index] * 1000))),
                        route.latitude[it[index]],
                        route.longitude[it[index]]
                    )
                )
            }
        }

        if (!route.callIndex.isNullOrEmpty() && !route.callTime.isNullOrEmpty()) {
            for ((index, routeCallIndex) in route.callIndex!!.withIndex()) {
                if (routeCallIndex == 0 || routeCallIndex == route.latitude.lastIndex) continue
                getCallFromIndex(index)?.let { call ->
                    events.add(
                        TripEvent(
                            if (index.rem(2) == 0) TripEventType.PHONE_DISTRACTION_PICK_UP else TripEventType.PHONE_DISTRACTION_HANG_UP,
                            Date(trip.endDate.time - ((trip.tripStatistics?.duration!!.toLong() * 1000) - route.callTime!![index] * 1000)),
                            route.latitude[routeCallIndex],
                            route.longitude[routeCallIndex],
                            isForbidden = call.isForbidden,
                            value = call.duration.toDouble()
                        )
                    )
                }
            }
        }
        events = events.sortedWith(compareBy { event -> event.time }).toMutableList()
        events.add(endTripEvent)
    }

    fun getCallFromIndex(position: Int): Call? {
        trip?.calls?.let { calls ->
            if (calls.isNotEmpty() && position < 2 * calls.size) {
                return calls[position / 2]
            }
        }
        return null
    }

    fun deleteTrip() {
        if (DriveKitDriverData.isConfigured()) {
            DriveKitDriverData.deleteTrip(itinId, object : TripDeleteQueryListener {
                override fun onResponse(status: Boolean) {
                    deleteTripObserver.postValue(status)
                }
            })
        }
    }

    fun shouldDisplayAdvice(mapItem: DKMapItem): Boolean {
        return getAdviceByMapItem(mapItem) != null
    }

    fun displayDriverPassengerFab(): Boolean = trip?.let {
        DriverDataUI.enableOccupantDeclaration && !it.unscored && it.transportationMode == TransportationMode.CAR
    } ?: false

    @DrawableRes
    fun getDriverPassengerModeFabIcon(): Int = this.trip?.declaredTransportationMode?.let {
        when (it.transportationMode) {
            TransportationMode.CAR -> {
                if (it.passenger == true) {
                    R.drawable.dk_transportation_passenger
                } else {
                    R.drawable.dk_transportation_driver
                }
            }

            TransportationMode.BUS -> R.drawable.dk_transportation_bus
            TransportationMode.TRAIN -> R.drawable.dk_transportation_train
            TransportationMode.BIKE -> R.drawable.dk_transportation_bicycle
            TransportationMode.BOAT -> R.drawable.dk_transportation_boat
            TransportationMode.FLIGHT -> R.drawable.dk_transportation_plane
            TransportationMode.ON_FOOT -> R.drawable.dk_transportation_on_foot

            TransportationMode.UNKNOWN,
            TransportationMode.MOTO,
            TransportationMode.TRUCK,
            TransportationMode.IDLE,
            TransportationMode.OTHER,
            TransportationMode.SKIING,
            null -> null
        }
    } ?: run {
        R.drawable.dk_transportation_driver
    }

    private fun getAdviceByMapItem(mapItem: DKMapItem): TripAdvice? = trip?.let { mapItem.getAdvice(it) }

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

    private fun getDeclarationBadgeStatus(): DeclarationBadgeStatus =
        if (trip?.declaredTransportationMode != null) {
            DeclarationBadgeStatus.LABELLED
        } else if (trip?.occupantInfo?.role == OccupantRole.PASSENGER) {
            DeclarationBadgeStatus.TO_LABEL
        } else {
            DeclarationBadgeStatus.NONE
        }

    @DrawableRes
    fun getDeclarationBadgeResId(): Int? {
        return when (getDeclarationBadgeStatus()) {
            DeclarationBadgeStatus.NONE -> null
            DeclarationBadgeStatus.TO_LABEL -> R.drawable.dk_occupant_role_trip_to_label
            DeclarationBadgeStatus.LABELLED -> R.drawable.dk_occupant_role_trip_labelled
        }
    }

    override fun getTripEvents(): List<TripEvent> {
        return if (configurableMapItems.contains(MapItem.DISTRACTION)) {
            events
        } else {
            events.filterNot {
                it.type == TripEventType.PHONE_DISTRACTION_LOCK ||
                        it.type == TripEventType.PHONE_DISTRACTION_UNLOCK ||
                        it.type == TripEventType.PHONE_DISTRACTION_HANG_UP ||
                        it.type == TripEventType.PHONE_DISTRACTION_PICK_UP
            }
        }
    }

    override fun getSelectedEvent(): MutableLiveData<Int> = selection

    override fun getDistractionScore(): Double = trip?.driverDistraction?.score ?: 0.0

    override fun getUnlockNumberEvent(): Int = trip?.driverDistraction?.nbUnlock ?: 0

    override fun getUnlockDuration(context: Context) =
        DKDataFormatter.formatDuration(
            context,
            DKDataFormatter.ceilDuration(trip?.driverDistraction?.durationUnlock, 600)
        ).convertToString()

    override fun getUnlockDistance(context: Context): String {
        trip?.driverDistraction?.distanceUnlock?.let {
            DKDataFormatter.apply {
                return if (it >= 1000) {
                    formatMeterDistanceInKm(context, ceilDistance(it, 10000)).convertToString()
                } else {
                    formatMeterDistance(
                        context,
                        trip?.driverDistraction?.distanceUnlock
                    ).convertToString()
                }
            }
        }
        return "-"
    }

    override fun getSelectedTraceType(): MutableLiveData<MapTraceType> = selectedMapTraceType

    override fun getPhoneCallsDistance(context: Context): String {
        val distance = trip?.calls?.map { call -> call.distance }?.sum() ?: 0
        DKDataFormatter.apply {
            return if (distance >= 1000) {
                formatMeterDistanceInKm(context,
                    ceilDistance(distance.toDouble(), 10000)).convertToString()
            } else {
                formatMeterDistance(
                    context,
                    distance.toDouble()
                ).convertToString()
            }
        }
    }

    override fun getPhoneCallsNumber(context: Context): Pair<String, String> {
        val phoneCallNumber = trip?.calls?.size ?: 0
        return if (phoneCallNumber == 0) {
            val phoneCallCongrats = context.getString(R.string.dk_driverdata_no_call_congrats)
            val phoneCallContentCongrats = context.getString(R.string.dk_driverdata_no_call_content)
            Pair(phoneCallContentCongrats, phoneCallCongrats)
        } else {
            val phoneCallContent = DKResource.buildString(
                context,
                DKColors.secondaryColor,
                DKColors.secondaryColor,
                R.string.dk_driverdata_distance_travelled,
                getPhoneCallsDistance(context)
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
        val duration = trip?.calls?.map { call -> call.duration }?.sum() ?: 0
        DKDataFormatter.apply {
            return formatDuration(context, ceilDuration(duration.toDouble(), 600)).convertToString()
        }
    }

    override fun hasScreenUnlocking(): Boolean =
        (trip?.driverDistraction?.distanceUnlock
            ?: 0.0) > 0.0 && (trip?.driverDistraction?.durationUnlock ?: 0.0) > 0.0

    override fun getItinId() = itinId

    override fun getTripListConfigurationType() = tripListConfiguration.getTripListConfigurationType()

    override fun getSpeedingScore(): Double = trip?.speedingStatistics?.score ?: 0.0

    override fun getSpeedingDistanceAndPercent(context: Context): Pair<Int, String> {
        val totalDistance = trip?.speedingStatistics?.distance ?: 0
        val speedingDistance = trip?.speedingStatistics?.speedingDistance ?: 0
        val speedingDistancePercent = if (totalDistance != 0 && speedingDistance != 0) {
            val percent = (speedingDistance.toDouble() * 100).div(totalDistance.toDouble())
            if (percent < 0.5) {
                "${percent.format(2)}%"
            } else {
                "${ceil(percent).removeZeroDecimal()}%"
            }
        } else {
            "$speedingDistance%"
        }
        return Pair(trip?.speedingStatistics?.speedingDistance ?: 0, speedingDistancePercent)
    }

    override fun getSpeedingDurationAndPercent(context: Context): Pair<Int, String> {
        val totalDuration = trip?.speedingStatistics?.duration ?: 0
        val speedingDuration = trip?.speedingStatistics?.speedingDuration ?: 0
        val speedingDurationPercent = if (totalDuration != 0 && speedingDuration != 0) {
            val percent = (speedingDuration.toDouble() * 100).div(totalDuration.toDouble())
            if (percent < 0.5) {
                "${percent.format(2)}%"
            } else {
                "${ceil(percent).removeZeroDecimal()}%"
            }
        } else {
            "$speedingDuration%"
        }
        return Pair(trip?.speedingStatistics?.speedingDuration ?: 0, speedingDurationPercent)
    }
}

@Suppress("UNCHECKED_CAST")
class TripDetailViewModelFactory(
    private val itinId: String,
    private val tripListConfiguration: TripListConfiguration
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TripDetailViewModel(itinId, tripListConfiguration) as T
    }
}

interface DKTripDetailViewModel {
    fun getItinId(): String
    fun getTripListConfigurationType(): TripListConfigurationType
    fun getTripEvents(): List<TripEvent>
    fun getSelectedEvent(): MutableLiveData<Int>
    fun getDistractionScore(): Double
    fun getUnlockNumberEvent(): Int
    fun getUnlockDuration(context: Context): String
    fun getUnlockDistance(context: Context): String
    fun getSelectedTraceType(): MutableLiveData<MapTraceType>
    fun getPhoneCallsDistance(context: Context): String
    fun getPhoneCallsNumber(context: Context): Pair<String,String>
    fun getPhoneCallsDuration(context: Context): String
    fun hasScreenUnlocking(): Boolean
    fun getSpeedingScore(): Double
    fun getSpeedingDistanceAndPercent(context: Context): Pair<Int, String>
    fun getSpeedingDurationAndPercent(context: Context): Pair<Int, String>
}
