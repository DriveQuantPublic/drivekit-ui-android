package com.drivequant.drivekit.common.ui.component.tripslist

import android.content.Context
import androidx.core.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import kotlinx.android.synthetic.main.trip_info_item.view.*

class TripInfoView : LinearLayout {

    constructor(context: Context, trip: DKTripListItem, tripData: TripData) : super(context) {
        init(trip, tripData)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private fun init(trip: DKTripListItem, tripData: TripData) {
        val view = View.inflate(context, R.layout.trip_info_item, null)

        DrawableCompat.setTint(view.background, DriveKitUI.colors.secondaryColor())
        trip.infoImageResource()?.let {
            view.image_view_trip_info.setImageResource(it)
        }
        trip.infoText()?.let {
            view.text_view_trip_info.apply {
                setTextColor(DriveKitUI.colors.fontColorOnSecondaryColor())
                visibility = View.VISIBLE
                text = it
                /*if (trip !is AdviceTripInfo) {
                    this.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
                }*/ //TODO check
            }
        } ?: run {
            view.text_view_trip_info.visibility = View.GONE
        }
        view.setOnClickListener {
            if (trip.hasInfoActionConfigured()) {
                trip.infoClickAction(context)
            } else {
                //TripDetailActivity.launchActivity(context as Activity, trip.itinId)
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