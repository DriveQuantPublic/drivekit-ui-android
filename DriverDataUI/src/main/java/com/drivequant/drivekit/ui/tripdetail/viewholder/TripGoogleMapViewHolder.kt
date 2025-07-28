package com.drivequant.drivekit.ui.tripdetail.viewholder

import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.intColor
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.databaseutils.entity.Route
import com.drivequant.drivekit.databaseutils.entity.TripAdvice
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.adapter.CustomInfoWindowAdapter
import com.drivequant.drivekit.ui.tripdetail.fragments.TripDetailFragment
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKMapItem
import com.drivequant.drivekit.ui.tripdetail.viewmodel.DKMarkerType
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapItem
import com.drivequant.drivekit.ui.tripdetail.viewmodel.MapTraceType
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripDetailViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripEvent
import com.drivequant.drivekit.ui.tripdetail.viewmodel.TripEventType
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

internal class TripGoogleMapViewHolder(
    var fragment: TripDetailFragment,
    private var itemView: View,
    var viewModel: TripDetailViewModel,
    private var googleMap: GoogleMap
) : GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnMarkerClickListener {

    private val googleMarkerList: MutableList<Marker> = mutableListOf()
    private val customInfoWindowAdapter = CustomInfoWindowAdapter(itemView.context, viewModel)
    private var computedPolyline: Polyline? = null
    private var builder = LatLngBounds.Builder()

    companion object {
        private const val SPEEDING_POLYLINE_TAG = "polyline-speeding-tag"
        private const val DEFAULT_POLYLINE_TAG = "default-polyline-tag"
    }

    init {
        viewModel.displayMapItem.observe(fragment.viewLifecycleOwner) {
            it?.let { mapItem ->
                configureAdviceButton(mapItem)
                configurePassengerButton(mapItem)
                when (mapItem) {
                    MapItem.SPEEDING -> {
                        traceRoute(mapItem, MapTraceType.SPEEDING)
                    }
                    else -> {
                        viewModel.selectedMapTraceType.value?.let { selectedMapTraceType ->
                            val mapTraceType = when (mapItem) {
                                MapItem.INTERACTIVE_MAP -> MapTraceType.UNLOCK_SCREEN
                                else -> selectedMapTraceType
                            }
                            traceRoute(mapItem, mapTraceType)
                        } ?: run {
                            traceRoute(mapItem)
                        }
                    }
                }
            }
        }
        viewModel.selection.observe(fragment.viewLifecycleOwner) {
            it?.let { position ->
                val marker = googleMarkerList[position]
                val cameraUpdate = CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(
                        marker.position,
                        16.5f
                    )
                )
                googleMap.animateCamera(cameraUpdate)
            }
        }
        viewModel.selectedMapTraceType.observe(fragment.viewLifecycleOwner) {
            it?.let {
                traceRoute(MapItem.DISTRACTION, it)
            }
        }
        googleMap.setOnInfoWindowClickListener(this)
        googleMap.uiSettings.isMapToolbarEnabled = false

        googleMap.setOnPolylineClickListener {
                val alert = DKAlertDialog.LayoutBuilder()
                    .init(itemView.context)
                    .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                    .cancelable(true)
                    .positiveButton(itemView.context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_ok)) { dialog, _ -> dialog.dismiss() }
                    .show()

                val title = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
                val description = alert.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)
                val icon = alert.findViewById<ImageView>(com.drivequant.drivekit.common.ui.R.id.image_view_alert_icon)

                title?.setText(R.string.dk_driverdata_speeding_event)
                description?.setText(R.string.dk_driverdata_speeding_event_info_content)
                icon?.setImageResource(com.drivequant.drivekit.common.ui.R.drawable.dk_common_speeding)
                title?.headLine1()
                description?.normalText()
        }
    }

    private fun configureAdviceButton(mapItem: DKMapItem) {
        val adviceFabButton = itemView.findViewById<FloatingActionButton>(R.id.fab_trip_advice)
        adviceFabButton.backgroundTintList = ColorStateList.valueOf(DKColors.secondaryColor)
        var shouldDisplayAdvice = false
        viewModel.trip?.let { trip ->
            val tripAdvice: TripAdvice? = mapItem.getAdvice(trip)
            if (tripAdvice != null) {
                shouldDisplayAdvice = true
            }
            adviceFabButton.hide()
            if (shouldDisplayAdvice) {
                mapItem.getAdviceImageResource()?.let {
                    adviceFabButton.setImageResource(it)
                    adviceFabButton.imageTintList = ColorStateList.valueOf(DKColors.fontColorOnSecondaryColor)
                }
                adviceFabButton.show()
                adviceFabButton.setOnClickListener {
                    fragment.displayAdvice(mapItem)
                    if (mapItem == MapItem.SAFETY){
                        DriveKitUI.analyticsListener?.trackScreen(itemView.context.getString(R.string.dk_tag_trips_detail_advice_safety), javaClass.simpleName)
                    } else if (mapItem == MapItem.ECO_DRIVING){
                        DriveKitUI.analyticsListener?.trackScreen(itemView.context.getString(R.string.dk_tag_trips_detail_advice_efficiency), javaClass.simpleName)
                    }
                }
            }
        }
    }

    private fun configurePassengerButton(mapItem: DKMapItem) {
        val passengerFabButton = itemView.findViewById<FloatingActionButton>(R.id.fab_trip_passenger)
        passengerFabButton.backgroundTintList = ColorStateList.valueOf(DKColors.secondaryColor)
        var shouldDisplayAdvice = false
        viewModel.trip?.let {
            shouldDisplayAdvice = true //TODO mock


            passengerFabButton.hide()
            if (shouldDisplayAdvice) {

                //TODO set image resource + imageTintList

                passengerFabButton.apply {
                    show()
                    setOnClickListener {
                        //TODO should we?
                        //DriveKitUI.analyticsListener?.trackScreen(itemView.context.getString(R.string.…), javaClass.simpleName)

                        fragment.displayDriverPassengerMode()
                    }
                }
            }

        }
    }

    fun traceRoute(mapItem: DKMapItem?, mapTraceType: MapTraceType = MapTraceType.UNLOCK_SCREEN) {
        clearMap()
        viewModel.route?.let { route ->
            val unlockColor = DriverDataUI.mapTraceWarningColor.intColor(itemView.context)
            val lockColor = DriverDataUI.mapTraceMainColor.intColor(itemView.context)
            val authorizedCallColor = DriverDataUI.mapTraceAuthorizedCallColor.intColor(itemView.context)
            if (mapItem != null && ((mapItem.shouldShowDistractionArea() && viewModel.configurableMapItems.contains(MapItem.DISTRACTION)) || mapItem.shouldShowPhoneDistractionArea() || mapItem.shouldShowSpeedingArea())) {
                when (mapTraceType) {
                    MapTraceType.UNLOCK_SCREEN -> {
                        if (mapItem.shouldShowDistractionArea() && route.screenLockedIndex != null) {
                                var unlock: Boolean
                                for (i in 1 until route.screenLockedIndex!!.size) {
                                    unlock = route.screenStatus!![i - 1] == 1
                                    drawRoute(
                                        route,
                                        route.screenLockedIndex!![i - 1], route.screenLockedIndex!![i],
                                        if (unlock) unlockColor else lockColor
                                    )
                                }
                        } else {
                            drawRoute(route, 0, route.latitude.size - 1, lockColor)
                        }
                    }
                    MapTraceType.PHONE_CALL -> {
                        if (mapItem.shouldShowPhoneDistractionArea() && route.callIndex != null) {
                            drawRoute(route, 0, route.callIndex!!.first(), lockColor)
                            for (i in 1 until route.callIndex!!.size) {
                                viewModel.getCallFromIndex(i - 1)?.let { call ->
                                    val phoneCallColor =
                                        if (call.isForbidden) unlockColor else authorizedCallColor
                                    drawRoute(
                                        route,
                                        route.callIndex!![i - 1], route.callIndex!![i],
                                        if (i.rem(2) != 0) phoneCallColor else lockColor
                                    )
                                }
                            }
                            drawRoute(
                                route,
                                route.callIndex!!.last(),
                                route.latitude.size - 1,
                                lockColor)
                        } else {
                            drawRoute(route, 0, route.latitude.size - 1, lockColor)
                        }
                    }
                    MapTraceType.SPEEDING -> {
                        if (mapItem.shouldShowSpeedingArea() && !route.speedingIndex.isNullOrEmpty() && !route.speedingTime.isNullOrEmpty()) {
                            drawRoute(route, 0, route.speedingIndex!!.first(), lockColor)
                            var speeding: Boolean
                            for (i in 1 until route.speedingIndex!!.size) {
                                speeding = i.rem(2) != 0
                                drawRoute(
                                    route,
                                    route.speedingIndex!![i - 1], route.speedingIndex!![i],
                                    if (speeding) unlockColor else lockColor,
                                    if (speeding) SPEEDING_POLYLINE_TAG else DEFAULT_POLYLINE_TAG
                                )
                            }
                            drawRoute(
                                route,
                                route.speedingIndex!!.last(),
                                route.latitude.size - 1,
                                lockColor
                            )
                        } else {
                            drawRoute(route, 0, route.latitude.size - 1, lockColor)
                        }
                    }
                }
            } else {
                drawRoute(
                    route,
                    0,
                    route.latitude.size - 1,
                    lockColor
                )
            }
            drawMarker(mapItem, mapTraceType)
        }
    }

    private fun drawRoute(route: Route, startIndex: Int, endIndex: Int, color: Int, tag: String = DEFAULT_POLYLINE_TAG) {
        val options = PolylineOptions()
        for (i in startIndex..endIndex) {
            val routeSeg = LatLng(route.latitude[i], route.longitude[i])
            builder.include(routeSeg)
            options.color(color)
            options.add(routeSeg)
        }
        if (tag == SPEEDING_POLYLINE_TAG) {
            options.clickable(true)
            computedPolyline?.tag = tag
        }
        computedPolyline = googleMap.addPolyline(options)
    }

    private fun drawMarker(mapItem: DKMapItem?, mapTraceType: MapTraceType) {
        mapItem?.let { item ->
            if (item.displayedMarkers().isNotEmpty()) {
                item.displayedMarkers().forEach { type ->
                    when (type) {
                        DKMarkerType.SAFETY -> viewModel.displayEvents =
                            viewModel.events.filterNot {
                                it.type == TripEventType.PHONE_DISTRACTION_LOCK ||
                                        it.type == TripEventType.PHONE_DISTRACTION_UNLOCK ||
                                        it.type == TripEventType.PHONE_DISTRACTION_HANG_UP ||
                                        it.type == TripEventType.PHONE_DISTRACTION_PICK_UP
                            }
                        DKMarkerType.DISTRACTION -> {
                            when(mapTraceType) {
                                MapTraceType.UNLOCK_SCREEN -> {
                                    viewModel.displayEvents =
                                        viewModel.events.filterNot {
                                                    it.type == TripEventType.SAFETY_BRAKE ||
                                                    it.type == TripEventType.SAFETY_ACCEL ||
                                                    it.type == TripEventType.SAFETY_ADHERENCE ||
                                                    it.type == TripEventType.PHONE_DISTRACTION_PICK_UP ||
                                                    it.type == TripEventType.PHONE_DISTRACTION_HANG_UP }
                                }
                                MapTraceType.PHONE_CALL -> {
                                    viewModel.displayEvents =
                                        viewModel.events.filterNot {
                                            it.type == TripEventType.SAFETY_BRAKE ||
                                                    it.type == TripEventType.SAFETY_ACCEL ||
                                                    it.type == TripEventType.SAFETY_ADHERENCE ||
                                                    it.type == TripEventType.PHONE_DISTRACTION_LOCK ||
                                                    it.type == TripEventType.PHONE_DISTRACTION_UNLOCK
                                        }
                                }
                                else -> {
                                    //DO NOTHING
                                }
                            }
                        }

                        DKMarkerType.ALL -> {
                            if (viewModel.configurableMapItems.contains(MapItem.DISTRACTION)) {
                                viewModel.displayEvents = viewModel.events
                            } else {
                                viewModel.displayEvents =
                                    viewModel.events.filterNot {
                                        it.type == TripEventType.PHONE_DISTRACTION_LOCK ||
                                                it.type == TripEventType.PHONE_DISTRACTION_UNLOCK ||
                                                it.type == TripEventType.PHONE_DISTRACTION_HANG_UP ||
                                                it.type == TripEventType.PHONE_DISTRACTION_PICK_UP
                                    }
                            }
                        }
                    }
                }
            } else {
                viewModel.displayEvents = viewModel.events.filter {
                    it.type == TripEventType.START || it.type == TripEventType.FINISH
                }
            }
        } ?: run {
            viewModel.displayEvents = viewModel.events.filter {
                it.type == TripEventType.START || it.type == TripEventType.FINISH
            }
        }
        drawEvents(viewModel.displayEvents)
        googleMap.setOnMarkerClickListener(this)
    }

    private fun drawEvents(events: List<TripEvent>) {
        for ((i, event) in events.withIndex()) {
            val location = LatLng(event.latitude, event.longitude)
            val marker = googleMap.addMarker(
                MarkerOptions().position(location)
                    .icon(BitmapDescriptorFactory.fromResource(event.getMapImageResource()))
                    .anchor(event.getXAnchor(), event.getYAnchor())
                    .title(event.getTitle(itemView.context))
            )
            if (marker != null) {
                marker.tag = i
                googleMarkerList.add(marker)
                builder.include(location)
            }
        }
    }

    private fun clearMap() {
        computedPolyline?.remove()
        googleMap.clear()
        googleMarkerList.clear()
    }

    fun updateCamera() {
        val paddingPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            100f,
            itemView.context.resources.displayMetrics
        ).toInt()

        val bounds = builder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, paddingPx)
        googleMap.setOnMapLoadedCallback {
            googleMap.setInfoWindowAdapter(customInfoWindowAdapter)
            try {
                googleMap.animateCamera(cameraUpdate)
            } catch (e: Exception) {
                // catch exception if there is not enough space for map display
            }
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        customInfoWindowAdapter.displayInfo(marker)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        viewModel.selection.postValue(marker.tag as Int)
        marker.showInfoWindow()
        return true
    }
}
