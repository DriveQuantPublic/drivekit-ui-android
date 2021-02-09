package com.drivequant.drivekit.ui.commons.views

import android.app.Activity
import android.content.Context
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.databaseutils.entity.Trip
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.tripdetail.activity.TripDetailActivity
import com.drivequant.drivekit.ui.trips.viewmodel.AdviceTripInfo
import com.drivequant.drivekit.ui.trips.viewmodel.DKTripInfo
import kotlinx.android.synthetic.main.trip_info_item.view.*

class TripInfoView : LinearLayout {

    constructor(context: Context, trip: Trip, tripInfo: DKTripInfo) : super(context) {
        init(trip, tripInfo)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private fun init(trip: Trip, tripInfo: DKTripInfo) {
        val view = View.inflate(context, R.layout.trip_info_item, null)


        DrawableCompat.setTint(view.background, DriveKitUI.colors.secondaryColor())
        tripInfo.getImageResource(trip)?.let {
            view.image_view_trip_info.setImageResource(it)
        }
        tripInfo.text(trip)?.let {
            view.text_view_trip_info.apply {
                setTextColor(DriveKitUI.colors.fontColorOnSecondaryColor())
                visibility = View.VISIBLE
                text = it
                if (tripInfo !is AdviceTripInfo) {
                    this.normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
                }
            }
        } ?: run {
            view.text_view_trip_info.visibility = View.GONE
        }
        view.setOnClickListener {
            if (tripInfo.hasActionConfigured(trip)) {
                tripInfo.onClickAction(context, trip)
            } else {
                TripDetailActivity.launchActivity(context as Activity, trip.itinId)
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