package com.drivequant.drivekit.common.ui.component.triplist.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController

internal class TripInfoView : LinearLayout {

    constructor(context: Context, trip: DKTripListItem) : super(context) {
        init(trip)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private fun init(trip: DKTripListItem) {
        val view = View.inflate(context, R.layout.trip_info_item, null)

        val tripInfoImageView: ImageView = view.findViewById(R.id.image_view_trip_info)
        val tripInfoTextView: TextView = view.findViewById(R.id.text_view_trip_info)

        DrawableCompat.setTint(view.background, DriveKitUI.colors.secondaryColor())
        trip.infoImageResource()?.let {
            tripInfoImageView.setImageResource(it)
        }

        trip.infoText(context)?.let {
            tripInfoTextView.apply {
                visibility = View.VISIBLE
                text = it
            }
        } ?: run {
            tripInfoTextView.visibility = View.GONE
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
