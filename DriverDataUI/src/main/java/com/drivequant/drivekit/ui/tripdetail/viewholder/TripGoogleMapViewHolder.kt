package com.drivequant.drivekit.ui.tripdetail.viewholder

import android.arch.lifecycle.Observer
import android.content.res.ColorStateList
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.databaseutils.entity.Route
import com.drivequant.drivekit.databaseutils.entity.TripAdvice
import com.drivequant.drivekit.ui.DriverDataUI
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.adapter.CustomInfoWindowAdapter
import com.drivequant.drivekit.ui.tripdetail.fragments.TripDetailFragment
import com.drivequant.drivekit.ui.tripdetail.viewmodel.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

class TripGoogleMapViewHolder(
    var fragment: TripDetailFragment,
    var itemView: View,
    var viewModel: TripDetailViewModel,
    var googleMap: GoogleMap
) : GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnMarkerClickListener {

    private val googleMarkerList: MutableList<Marker> = mutableListOf()
    private val customInfoWindowAdapter = CustomInfoWindowAdapter(itemView.context, viewModel)
    private var computedPolyline: Polyline? = null
    private var builder = LatLngBounds.Builder()

    init {
        viewModel.displayMapItem.observe(fragment, Observer {
            it?.let { mapItem ->
                configureAdviceButton(mapItem)
                traceRoute(mapItem)
            }
        })
        viewModel.selection.observe(fragment, Observer {
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
        })
        googleMap.setOnInfoWindowClickListener(this)
        googleMap.uiSettings.isMapToolbarEnabled = false
    }
    
    private fun configureAdviceButton(mapItem: DKMapItem){
        val adviceFabButton = itemView.findViewById<FloatingActionButton>(R.id.fab_trip_advice)
        adviceFabButton.backgroundTintList =
            ColorStateList.valueOf(DriveKitUI.colors.secondaryColor())
        var shouldDisplayAdvice = false
        viewModel.trip?.let { trip ->
            val tripAdvice: TripAdvice? = mapItem.getAdvice(trip)
            if (tripAdvice != null) {
                shouldDisplayAdvice = true
            }
            adviceFabButton.hide()
            if (shouldDisplayAdvice) {
                if (mapItem.getAdviceImageResource() != -1) {
                    adviceFabButton.setImageResource(mapItem.getAdviceImageResource())
                }
                adviceFabButton.show()
                adviceFabButton.setOnClickListener {
                    fragment.displayAdvice(mapItem)
                    //TODO create a new method in DKMapItem
                    if (mapItem == MapItem.SAFETY){
                        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(itemView.context, "dk_tag_trips_detail_advice_safety"), javaClass.simpleName)
                    } else if (mapItem == MapItem.ECO_DRIVING){
                        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(itemView.context, "dk_tag_trips_detail_advice_efficiency"), javaClass.simpleName)
                    }
                }
            }
        }
    }

    fun traceRoute(mapItem: DKMapItem?) {
        clearMap()
        viewModel.route?.let { route ->
            val unlockColor =
                ContextCompat.getColor(itemView.context, DriverDataUI.mapTraceWarningColor)
            val lockColor = ContextCompat.getColor(itemView.context, DriverDataUI.mapTraceMainColor)
            if (mapItem != null && mapItem.shouldShowDistractionArea() && viewModel.configurableMapItems.contains(MapItem.INTERACTIVE_MAP)) {
                var unlock: Boolean
                route.screenLockedIndex?.let { screenLockedIndex ->
                    for (i in 1 until screenLockedIndex.size) {
                        unlock = route.screenStatus!![i - 1] == 1
                        drawRoute(
                            route,
                            screenLockedIndex[i - 1], screenLockedIndex[i],
                            if (unlock) unlockColor else lockColor
                        )
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
            drawMarker(mapItem)
        }
    }

    private fun drawRoute(route: Route, startIndex: Int, endIndex: Int, color: Int) {
        val options = PolylineOptions()
        for (i in startIndex..endIndex) {
            val routeSeg = LatLng(route.latitude[i], route.longitude[i])
            builder.include(routeSeg)
            options.color(color)
            options.add(routeSeg)
        }
        computedPolyline = googleMap.addPolyline(options)
    }

    private fun drawMarker(mapItem: DKMapItem?) {
        mapItem?.let { it ->
            if (it.displayedMarkers().contains(DKMarkerType.ALL)) {
                viewModel.displayEvents = viewModel.events
            } else {
                if (it.displayedMarkers().isNotEmpty()) {
                    it.displayedMarkers().forEach { type ->
                        when (type) {
                            DKMarkerType.SAFETY -> viewModel.displayEvents =
                                viewModel.events.filterNot { it.type == TripEventType.PHONE_DISTRACTION_LOCK || it.type == TripEventType.PHONE_DISTRACTION_UNLOCK }
                            DKMarkerType.DISTRACTION -> viewModel.displayEvents =
                                viewModel.events.filterNot { it.type == TripEventType.SAFETY_BRAKE || it.type == TripEventType.SAFETY_ACCEL || it.type == TripEventType.SAFETY_ADHERENCE }
                        }
                    }
                } else {
                    viewModel.displayEvents = viewModel.events.filter {
                        it.type == TripEventType.START || it.type == TripEventType.FINISH
                    }
                }
            }
        } ?: kotlin.run {
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
            marker.tag = i
            googleMarkerList.add(marker)
            builder.include(location)
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

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.let {
            viewModel.selection.postValue(it.tag as Int)
            it.showInfoWindow()
        }
        return true
    }
}