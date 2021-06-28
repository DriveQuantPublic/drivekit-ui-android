package com.drivequant.drivekit.common.ui.component.triplist.views

import android.content.Context
import androidx.core.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import kotlinx.android.synthetic.main.trip_info_item.view.*

internal class TripInfoView : LinearLayout {

    constructor(context: Context, trip: DKTripListItem) : super(context) {
        init(trip)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private fun init(trip: DKTripListItem) {
        val view = View.inflate(context, R.layout.trip_info_item, null)

        DrawableCompat.setTint(view.background, DriveKitUI.colors.secondaryColor())
        trip.infoImageResource()?.let {
            view.image_view_trip_info.setImageResource(it)
        }

        trip.infoText(context)?.let {
            view.text_view_trip_info.apply {
                visibility = View.VISIBLE
                text = it
            }
        } ?: run {
            view.text_view_trip_info.visibility = View.GONE
        }
        view.setOnClickListener {
            if (trip.hasInfoActionConfigured()) {
                trip.infoClickAction(context)
            } else {
                DriveKitNavigationController.driverDataUIEntryPoint?.startTripDetailActivity(context, trip.getItinId())
            }
        }
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}