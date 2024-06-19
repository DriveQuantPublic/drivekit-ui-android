package com.drivequant.drivekit.common.ui.component.triplist.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController

internal class TripInfoView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    companion object {
        fun new(context: Context): TripInfoView {
            val view = View.inflate(context, R.layout.trip_info_item, null) as TripInfoView
            return view
        }
    }

    private lateinit var tripInfoImageView: ImageView
    private lateinit var tripInfoTextView: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        tripInfoImageView = findViewById(R.id.image_view_trip_info)
        tripInfoTextView = findViewById(R.id.text_view_trip_info)
    }

    fun update(trip: DKTripListItem) {
        trip.infoImageResource()?.let {
            tripInfoImageView.setImageResource(it)
        } ?: run {
            tripInfoImageView.setImageDrawable(null)
        }

        tripInfoTextView.text = trip.infoText(context)

        setOnClickListener {
            if (trip.hasInfoActionConfigured()) {
                trip.infoClickAction(context)
            } else {
                DriveKitNavigationController.driverDataUIEntryPoint?.startTripDetailActivity(context, trip.getItinId())
            }
        }
    }
}
